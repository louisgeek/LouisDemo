#include <jni.h>
#include <string>
#include <cstring>
#include <cstdio>
#include <stdlib.h>
#include <limits.h>
#include <map>
#include <memory>
#include <mutex>
#include <set>
#include <atomic>
#include <vector>
#include <fstream>
#include <android/log.h>
#include <unistd.h>

#define LOG_TAG "MyFFmpeg"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// 引入 FFmpeg 头文件
extern "C" {
#include <libavformat/avformat.h>
#include <libavcodec/avcodec.h>
#include <libavutil/avutil.h>
#include <libavutil/time.h>
}

// ---------------------------------------------------------------------------
// 结果码：必须与 Java 侧 RemuxResult.CODE_* 常量一一对应
// ---------------------------------------------------------------------------

enum RemuxCode {
    kCodeSuccess = 0,
    kCodeCancelled = 1,
    kCodeInvalidArgument = 2,
    kCodeNotPrepared = 3,
    kCodeAlreadyRunning = 4,
    kCodeBusy = 5,
    kCodeInputError = 6,
    kCodeUnsupportedStream = 7,
    kCodeInsecureInput = 8,
    kCodeOutputError = 9,
    kCodeMuxError = 10,
    kCodeInternalError = 11
};

struct RemuxResult {
    int code;
    std::string error;
};

// ---------------------------------------------------------------------------
// 通用工具
// ---------------------------------------------------------------------------

static std::string avErrorString(int err) {
    char buf[AV_ERROR_MAX_STRING_SIZE] = {0};
    av_strerror(err, buf, sizeof(buf));
    return std::string(buf);
}

// 忽略大小写的后缀判断（C++14，无 std::string::ends_with）
static bool endsWithCase(const std::string &s, const char *suffix) {
    const size_t suffixLen = strlen(suffix);
    if (s.size() < suffixLen) {
        return false;
    }
    for (size_t i = 0; i < suffixLen; ++i) {
        char a = s[s.size() - suffixLen + i];
        char b = suffix[i];
        if (a >= 'A' && a <= 'Z') a = static_cast<char>(a - 'A' + 'a');
        if (b >= 'A' && b <= 'Z') b = static_cast<char>(b - 'A' + 'a');
        if (a != b) {
            return false;
        }
    }
    return true;
}

// 仅对已存在的路径返回规范路径；不存在或不可解析时返回空串
static std::string canonicalPath(const std::string &path) {
    char resolved[PATH_MAX];
    if (realpath(path.c_str(), resolved) == nullptr) {
        return std::string();
    }
    return std::string(resolved);
}

static std::string directoryOf(const std::string &path) {
    const size_t slash = path.find_last_of('/');
    if (slash == std::string::npos) {
        return ".";
    }
    if (slash == 0) {
        return "/";
    }
    return path.substr(0, slash);
}

static bool isUnderDir(const std::string &path, const std::string &dir) {
    if (dir.empty() || path.size() < dir.size()) {
        return false;
    }
    if (path.compare(0, dir.size(), dir) != 0) {
        return false;
    }
    if (path.size() == dir.size()) {
        return true;
    }
    return path[dir.size()] == '/' || dir[dir.size() - 1] == '/';
}

// 输出路径的冲突检测 key：输出文件本身可能尚不存在，不能整体 realpath，
// 因此取「规范化父目录 + 原样文件名」。这样 ./out.mp4 与 /a/b/../out.mp4
// 会得到同一 key，避免绕过同路径并发互斥。父目录不可解析时退回原始串
// （此时 avio_open 同样会失败，互斥即便不准确也无实际影响）。
static std::string outputConflictKey(const std::string &outputPath) {
    const size_t slash = outputPath.find_last_of('/');
    const std::string name = slash == std::string::npos
            ? outputPath
            : outputPath.substr(slash + 1);
    if (name.empty()) {
        return outputPath;
    }
    const std::string dir = canonicalPath(directoryOf(outputPath));
    if (dir.empty()) {
        return outputPath;
    }
    return dir + "/" + name;
}

// 标签行匹配：#EXT-X-ENDLIST 之后必须是行尾/冒号/空白，避免误匹配前缀相同的自定义标签
static bool startsWithTag(const std::string &line, const char *tag) {
    const size_t tagLen = strlen(tag);
    if (line.compare(0, tagLen, tag) != 0) {
        return false;
    }
    return line.size() == tagLen || line[tagLen] == ':' || line[tagLen] == ' ' ||
           line[tagLen] == '\t';
}

// ---------------------------------------------------------------------------
// 本地 HLS 清单预扫描：所有引用必须位于允许根目录内，且不允许远程 URI
// ---------------------------------------------------------------------------

static void collectUriAttributes(const std::string &line, std::vector<std::string> &out) {
    const std::string key = "URI=\"";
    size_t pos = 0;
    while ((pos = line.find(key, pos)) != std::string::npos) {
        const size_t begin = pos + key.size();
        const size_t end = line.find('"', begin);
        if (end == std::string::npos) {
            break;
        }
        out.push_back(line.substr(begin, end - begin));
        pos = end + 1;
    }
}

static bool validatePlaylistFile(const std::string &playlistPath, const std::string &rootDir,
                                 int depth, std::set<std::string> &visited, std::string &error) {
    if (depth > 4) {
        error = "playlist nesting is too deep: " + playlistPath;
        return false;
    }
    const std::string canonical = canonicalPath(playlistPath);
    if (canonical.empty()) {
        error = "cannot resolve playlist path: " + playlistPath;
        return false;
    }
    if (!isUnderDir(canonical, rootDir)) {
        error = "playlist is outside the allowed input root: " + canonical;
        return false;
    }
    if (!visited.insert(canonical).second) {
        return true; // 已校验过（或存在循环引用）
    }

    std::ifstream input(canonical.c_str());
    if (!input.is_open()) {
        error = "cannot read playlist: " + canonical;
        return false;
    }
    const std::string baseDir = directoryOf(canonical);
    bool sawEndList = false;
    bool sawMediaUri = false;
    std::string line;
    while (std::getline(input, line)) {
        while (!line.empty() &&
               (line.back() == '\r' || line.back() == ' ' || line.back() == '\t')) {
            line.pop_back();
        }
        const size_t begin = line.find_first_not_of(" \t");
        if (begin == std::string::npos) {
            continue;
        }
        if (begin > 0) {
            line.erase(0, begin);
        }

        std::vector<std::string> uris;
        const bool isTagLine = line[0] == '#';
        if (isTagLine) {
            if (startsWithTag(line, "#EXT-X-ENDLIST")) {
                sawEndList = true;
            }
            // EXT-X-KEY / EXT-X-MAP / EXT-X-MEDIA / EXT-X-I-FRAME-STREAM-INF 等
            collectUriAttributes(line, uris);
        } else {
            uris.push_back(line);
        }

        for (size_t i = 0; i < uris.size(); ++i) {
            const std::string &uri = uris[i];
            if (uri.empty()) {
                continue;
            }
            if (uri.find_first_of("?#") != std::string::npos) {
                // 本地文件名本就不含 query/fragment；若放行，扫描器校验的
                // 是去掉 ?# 后的路径，与 demuxer 实际打开的带 ?# 路径不一致
                // （过了扫描、执行必失败），因此在扫描阶段直接拒绝
                error = "playlist URI must not contain query/fragment: " + uri;
                return false;
            }
            if (uri.find("://") != std::string::npos) {
                error = "remote URI is not allowed in a local playlist: " + uri;
                return false;
            }
            const std::string candidate = uri[0] == '/' ? uri : baseDir + "/" + uri;
            const std::string resolved = canonicalPath(candidate);
            if (resolved.empty()) {
                error = "playlist references a missing file: " + candidate;
                return false;
            }
            if (!isUnderDir(resolved, rootDir)) {
                error = "playlist references a file outside the allowed input root: " + resolved;
                return false;
            }
            if (endsWithCase(resolved, ".m3u8") || endsWithCase(resolved, ".m3u")) {
                if (!validatePlaylistFile(resolved, rootDir, depth + 1, visited, error)) {
                    return false;
                }
            } else if (!isTagLine) {
                // 非标签行、解析到非子清单文件 = 媒体分片引用
                sawMediaUri = true;
            }
        }
    }
    if (sawMediaUri && !sawEndList) {
        // 无 ENDLIST 的媒体清单是 live/半截清单：HLS demuxer 会当作直播源
        // 反复重读清单等待新分片，executeRemux 永不返回。本地 VOD 包按 RFC 8216
        // 必含 ENDLIST，缺失即拒绝，把问题在扫描阶段暴露出来
        error = "media playlist without #EXT-X-ENDLIST is not supported "
                "(live or truncated playlist): " + canonical;
        return false;
    }
    return true;
}

// ---------------------------------------------------------------------------
// 可取消的转封装操作管理（可取消 RemuxOperation 模型：登记-执行-自动注销）
// ---------------------------------------------------------------------------

struct RemuxOperation {
    std::atomic_bool cancelled{false};
    std::atomic_bool running{false};
};

static std::mutex gRemuxMutex;
static std::map<std::string, std::shared_ptr<RemuxOperation> > gRemuxOperations;

// 全局并发上限：移动端转封装属于重 IO/CPU 操作，并行过多只会互相拖慢
static const size_t kMaxConcurrentRemuxes = 2;
static size_t gRunningRemuxCount = 0;
// 同一输出路径不允许并行写入（临时文件名固定为 <output>.part）；
// key 为规范化后的路径（见 outputConflictKey），./out.mp4 与 /a/./out.mp4
// 会被识别为同一目标文件
static std::set<std::string> gActiveOutputPaths;

// 取消信号通过 thread_local 绑定到执行线程：取消方在任意线程置位，
// 执行线程在 packet 循环和 FFmpeg interrupt callback 里轮询当前线程绑定的标志。
static thread_local std::shared_ptr<RemuxOperation> tCurrentRemuxOperation;

static bool isRemuxCancelled() {
    const std::shared_ptr<RemuxOperation> op = tCurrentRemuxOperation;
    return op != nullptr && op->cancelled.load(std::memory_order_relaxed);
}

// 挂到 avformat 的 interrupt_callback 上，用于打断阻塞中的 FFmpeg 内部 IO。
static int remuxInterruptCallback(void *opaque) {
    (void) opaque;
    return isRemuxCancelled() ? 1 : 0;
}

// RAII：进入 remux 时绑定当前操作到线程局部变量，退出时解绑。
class CurrentRemuxOperationScope {
public:
    explicit CurrentRemuxOperationScope(std::shared_ptr<RemuxOperation> op) : op_(std::move(op)) {
        tCurrentRemuxOperation = op_;
    }

    ~CurrentRemuxOperationScope() {
        tCurrentRemuxOperation.reset();
    }

private:
    std::shared_ptr<RemuxOperation> op_;
};

// ---------------------------------------------------------------------------
// 进度回调：在调用 executeRemux 的线程上同步回调 Java listener
// ---------------------------------------------------------------------------

struct ProgressSink {
    JNIEnv *env = nullptr;
    jobject listener = nullptr;
    jmethodID onProgress = nullptr;
    int lastPercent = -1;
    int64_t lastReportUs = 0;

    bool enabled() const {
        return env != nullptr && listener != nullptr && onProgress != nullptr;
    }
};

static void invokeProgress(ProgressSink &sink, int percent) {
    sink.env->CallVoidMethod(sink.listener, sink.onProgress, percent);
    if (sink.env->ExceptionCheck()) {
        // 回调方抛异常不能影响转封装本身
        sink.env->ExceptionClear();
        LOGE("progress listener threw an exception; ignored");
    }
}

static void reportProgress(ProgressSink &sink, int percent) {
    if (!sink.enabled()) {
        return;
    }
    if (percent < 0) percent = 0;
    if (percent > 99) percent = 99;
    if (percent <= sink.lastPercent) {
        return;
    }
    const int64_t nowUs = av_gettime_relative();
    if (sink.lastReportUs != 0 && nowUs - sink.lastReportUs < 100000) {
        return; // 10 次/秒节流
    }
    sink.lastPercent = percent;
    sink.lastReportUs = nowUs;
    invokeProgress(sink, percent);
}

static void reportProgressComplete(ProgressSink &sink) {
    if (!sink.enabled()) {
        return;
    }
    sink.lastPercent = 100;
    invokeProgress(sink, 100);
}

// ---------------------------------------------------------------------------
// 转封装核心：本地输入（TS / fMP4 / M3U8 清单+本地分片）-> MP4（stream copy）
// 输出先写 <output>.part，成功后原子 rename；失败/取消只删临时文件。
// ---------------------------------------------------------------------------

static RemuxResult writeMp4FromInput(const std::string &inputPath,
                                     const std::string &outputPath,
                                     const std::string &allowedInputRoot,
                                     ProgressSink &progress) {
    const std::string tempPath = outputPath + ".part";

    if (inputPath == outputPath || inputPath == tempPath) {
        return RemuxResult{kCodeInvalidArgument, "input path must differ from output path"};
    }
    const std::string canonicalInput = canonicalPath(inputPath);
    const std::string canonicalOutput = canonicalPath(outputPath);
    if (!canonicalInput.empty() && !canonicalOutput.empty() &&
        canonicalInput == canonicalOutput) {
        return RemuxResult{kCodeInvalidArgument, "input path must differ from output path"};
    }

    const bool looksLikePlaylist =
            endsWithCase(inputPath, ".m3u8") || endsWithCase(inputPath, ".m3u");
    if (looksLikePlaylist && !canonicalInput.empty()) {
        const std::string root = allowedInputRoot.empty()
                ? directoryOf(canonicalInput)
                : canonicalPath(allowedInputRoot);
        if (root.empty()) {
            return RemuxResult{kCodeInvalidArgument,
                               "allowed input root does not exist: " + allowedInputRoot};
        }
        std::set<std::string> visited;
        std::string scanError;
        if (!validatePlaylistFile(canonicalInput, root, 0, visited, scanError)) {
            return RemuxResult{kCodeInsecureInput, scanError};
        }
    }

    // 清理上次异常退出可能残留的临时文件（本库固定命名，可安全删除）
    std::remove(tempPath.c_str());
    //打开输入文件
    AVFormatContext *inputCtx = nullptr; //输入文件格式上下文
    AVFormatContext *outputCtx = nullptr;
    bool ioOpened = false;
    RemuxResult result{kCodeInternalError, ""};

    // --- 打开输入（带取消中断回调；协议白名单禁止网络；本地 M3U8 清单放宽 HLS demuxer 分片扩展名检查）---
    inputCtx = avformat_alloc_context();
    if (!inputCtx) {
        result.error = "could not allocate input context";
        goto end;
    }
    inputCtx->interrupt_callback.callback = &remuxInterruptCallback;
    inputCtx->interrupt_callback.opaque = nullptr;

    {
        // 协议白名单无条件生效：本库只处理本地文件，从 IO 层杜绝一切网络访问
        // （crypto 供本地 AES-128 密钥/分片解密）。白名单会随 interrupt_callback
        // 一并被 FFmpeg 传播到 HLS demuxer 打开分片/密钥的嵌套 IO 上。
        AVDictionary *openOpts = nullptr;
        av_dict_set(&openOpts, "protocol_whitelist", "file,crypto", 0);
        if (looksLikePlaylist) {
            // 本地清单场景：分片落盘时可能是任意扩展名，显式放宽 FFmpeg HLS demuxer
            // 的分片扩展名白名单与严格检查（allowed_extensions=ALL
            // + extension_picky=0 策略；6.1 中新旧两个 HLS demuxer 各消费其一，双保险）。
            // 非清单输入不传 HLS 选项，走默认探测。
            av_dict_set(&openOpts, "allowed_extensions", "ALL", 0);
            av_dict_set(&openOpts, "extension_picky", "0", 0);
        }
        // 用规范路径打开：demuxer 解析清单相对引用的基准目录与预扫描验证的目录一致
        // （realpath 已消解 ../、symlink 等非规范形式，避免"验证的文件"与"打开的文件"不同）
        const std::string &openPath = canonicalInput.empty() ? inputPath : canonicalInput;
        // avformat_open_input 接管并销毁 options 字典（成功时未消费项回填、失败时清空）
        int ret = avformat_open_input(&inputCtx, openPath.c_str(), nullptr, &openOpts);
        av_dict_free(&openOpts);
        if (ret == AVERROR_EXIT) {
            result.code = kCodeCancelled;
            result.error = "cancelled";
            goto end;
        }
        if (ret < 0) {
            result.code = kCodeInputError;
            result.error = "cannot open input file: " + avErrorString(ret);
            goto end;
        }
    }
    //获取流信息
    {
        int ret = avformat_find_stream_info(inputCtx, nullptr);
        if (ret == AVERROR_EXIT) {
            result.code = kCodeCancelled;
            result.error = "cancelled";
            goto end;
        }
        if (ret < 0) {
            result.code = kCodeInputError;
            result.error = "could not find stream information: " + avErrorString(ret);
            goto end;
        }
    }

    // --- 流筛选：只带视频/音频 best stream 进容器，避免数据流等导致 MP4 写头失败 ---
    {
        const int videoIdx = av_find_best_stream(inputCtx, AVMEDIA_TYPE_VIDEO, -1, -1, nullptr, 0);
        const int audioIdx = av_find_best_stream(
                inputCtx, AVMEDIA_TYPE_AUDIO, -1, videoIdx >= 0 ? videoIdx : -1, nullptr, 0);
        if (videoIdx < 0 && audioIdx < 0) {
            result.code = kCodeUnsupportedStream;
            result.error = "no audio or video stream found in input";
            goto end;
        }

        // --- 创建 MP4 输出上下文 ---
        if (avformat_alloc_output_context2(&outputCtx, nullptr, "mp4", outputPath.c_str()) < 0 ||
            !outputCtx) {
            result.error = "could not create mp4 output context";
            outputCtx = nullptr; // alloc 失败时内部已置空，此处兜底
            goto end;
        }

        std::vector<int> outStreamMap(inputCtx->nb_streams, -1);

        // --- 视频流：无条件 stream copy ---
        if (videoIdx >= 0) {
            AVStream *inStream = inputCtx->streams[videoIdx];
            AVStream *outStream = avformat_new_stream(outputCtx, nullptr);
            if (!outStream) {
                result.error = "failed allocating output video stream";
                goto end;
            }
            if (avcodec_parameters_copy(outStream->codecpar, inStream->codecpar) < 0) {
                result.error = "failed to copy video codec parameters";
                goto end;
            }
            // H.265 强制 hvc1（参数集放容器级，Apple 系播放器兼容性更好）；
            // 其余编码 tag 置 0，由 muxer 选择容器合规 tag。
            if (inStream->codecpar->codec_id == AV_CODEC_ID_HEVC) {
                outStream->codecpar->codec_tag = MKTAG('h', 'v', 'c', '1');
            } else {
                outStream->codecpar->codec_tag = 0;
            }
            outStreamMap[videoIdx] = outStream->index;
        }

        // --- 音频流：AAC 直拷；非 AAC 报错（本库不含转码管线，不产出不可播文件）---
        if (audioIdx >= 0) {
            const AVCodecParameters *audioPar = inputCtx->streams[audioIdx]->codecpar;
            if (audioPar->codec_id != AV_CODEC_ID_AAC) {
                result.code = kCodeUnsupportedStream;
                result.error = std::string("audio stream is ") +
                               avcodec_get_name(audioPar->codec_id) +
                               ", only AAC audio can be stream-copied into MP4 "
                               "(transcoding is not supported by this build)";
                goto end;
            }
            AVStream *inStream = inputCtx->streams[audioIdx];
            AVStream *outStream = avformat_new_stream(outputCtx, nullptr);
            if (!outStream) {
                result.error = "failed allocating output audio stream";
                goto end;
            }
            if (avcodec_parameters_copy(outStream->codecpar, inStream->codecpar) < 0) {
                result.error = "failed to copy audio codec parameters";
                goto end;
            }
            outStream->codecpar->codec_tag = 0;
            outStreamMap[audioIdx] = outStream->index;
        }

        // --- 打开输出临时文件 ---
        if (!(outputCtx->oformat->flags & AVFMT_NOFILE)) {
            int ret = avio_open(&outputCtx->pb, tempPath.c_str(), AVIO_FLAG_WRITE);
            if (ret < 0) {
                result.code = kCodeOutputError;
                result.error = "could not open output file: " + avErrorString(ret);
                goto end;
            }
            ioOpened = true;
        }

        // --- 写文件头：movflags +faststart；avoid_negative_ts 兜底 B 帧导致的负 DTS ---
        {
            AVDictionary *headerOpts = nullptr;
            av_dict_set(&headerOpts, "movflags", "+faststart", 0);
            av_dict_set(&headerOpts, "avoid_negative_ts", "make_zero", 0);
            int ret = avformat_write_header(outputCtx, &headerOpts);
            av_dict_free(&headerOpts);
            if (ret < 0) {
                result.code = kCodeMuxError;
                result.error = "error occurred when writing header: " + avErrorString(ret);
                goto end;
            }
        }

        // --- 时间戳归零偏移：TS 的 start_time 通常非 0，减去后输出从 0 附近开始 ---
        const int64_t startTime =
                (inputCtx->start_time != AV_NOPTS_VALUE && inputCtx->start_time > 0)
                ? inputCtx->start_time
                : 0;
        const int64_t durationUs = inputCtx->duration > 0 ? inputCtx->duration : 0;

        reportProgress(progress, 0);

        // --- 转封装核心循环 ---
        {
            AVPacket *pkt = av_packet_alloc();
            if (!pkt) {
                result.error = "could not allocate packet";
                goto end;
            }
            while (true) {
                if (isRemuxCancelled()) {
                    result.code = kCodeCancelled;
                    result.error = "cancelled";
                    break;
                }
                int ret = av_read_frame(inputCtx, pkt);
                if (ret == AVERROR_EOF) {
                    break;
                }
                if (ret == AVERROR_EXIT) {
                    result.code = kCodeCancelled;
                    result.error = "cancelled";
                    break;
                }
                if (ret < 0) {
                    result.code = kCodeInputError;
                    result.error = "error reading packet: " + avErrorString(ret);
                    break;
                }

                if (durationUs > 0) {
                    AVStream *progressStream = inputCtx->streams[pkt->stream_index];
                    const int64_t ts = pkt->dts != AV_NOPTS_VALUE ? pkt->dts : pkt->pts;
                    if (ts != AV_NOPTS_VALUE && progressStream->time_base.den > 0) {
                        const int64_t elapsedUs =
                                av_rescale_q(ts, progressStream->time_base, AV_TIME_BASE_Q) -
                                startTime;
                        if (elapsedUs > 0) {
                            reportProgress(
                                    progress,
                                    static_cast<int>(av_rescale(elapsedUs, 100, durationUs)));
                        }
                    }
                }

                const int outIdx = outStreamMap[pkt->stream_index];
                if (outIdx < 0) {
                    // 未选中的流（数据流/字幕等）直接丢弃
                    av_packet_unref(pkt);
                    continue;
                }

                AVStream *inStream = inputCtx->streams[pkt->stream_index];
                AVStream *outStream = outputCtx->streams[outIdx];
                const AVRational inTb = inStream->time_base;

                if (pkt->pts != AV_NOPTS_VALUE) {
                    pkt->pts -= av_rescale_q(startTime, AV_TIME_BASE_Q, inTb);
                }
                if (pkt->dts != AV_NOPTS_VALUE) {
                    pkt->dts -= av_rescale_q(startTime, AV_TIME_BASE_Q, inTb);
                }
                av_packet_rescale_ts(pkt, inTb, outStream->time_base);
                pkt->pos = -1;
                pkt->stream_index = outIdx;

                ret = av_interleaved_write_frame(outputCtx, pkt);
                // 成功时 pkt 被 muxer 消费置空，unref 是无害的；失败时由本侧负责释放
                av_packet_unref(pkt);
                if (ret < 0) {
                    result.code = kCodeMuxError;
                    result.error = "error muxing packet: " + avErrorString(ret);
                    break;
                }
            }
            av_packet_free(&pkt);
        }

        // --- 写文件尾（仅在未失败/未取消时）---
        if (!result.error.empty()) {
            goto end;
        }
        if (isRemuxCancelled()) {
            result.code = kCodeCancelled;
            result.error = "cancelled";
            goto end;
        }
        {
            int ret = av_write_trailer(outputCtx);
            if (ret < 0) {
                result.code = kCodeMuxError;
                result.error = "error writing trailer: " + avErrorString(ret);
                goto end;
            }
        }

        result.code = kCodeSuccess;
        result.error.clear();
    }

    end:
    if (outputCtx) {
        if (ioOpened && !(outputCtx->oformat->flags & AVFMT_NOFILE)) {
            avio_closep(&outputCtx->pb);
        }
        avformat_free_context(outputCtx);
    }
    avformat_close_input(&inputCtx);

    // 成功：临时文件原子替换目标；失败/取消：只删临时文件，不碰目标路径上的已有文件
    if (result.code == kCodeSuccess) {
        if (std::rename(tempPath.c_str(), outputPath.c_str()) != 0) {
            result.code = kCodeOutputError;
            result.error = "failed to move temporary output into place";
        }
    }
    if (result.code == kCodeSuccess) {
        LOGI("remux success: %s", outputPath.c_str());
        reportProgressComplete(progress);
    } else {
        std::remove(tempPath.c_str());
        LOGE("%s: %s",
             result.code == kCodeCancelled ? "remux cancelled" : "remux failed",
             result.error.c_str());
    }
    return result;
}

// ---------------------------------------------------------------------------
// 操作登记 / 执行 / 取消
// ---------------------------------------------------------------------------

static bool prepareRemuxOperation(const std::string &operationId) {
    if (operationId.empty()) {
        return false;
    }
    std::lock_guard<std::mutex> lock(gRemuxMutex);
    if (gRemuxOperations.find(operationId) != gRemuxOperations.end()) {
        return false; // operationId 已登记
    }
    gRemuxOperations[operationId] = std::make_shared<RemuxOperation>();
    return true;
}

// RAII：结束时注销操作、归还并发名额、释放输出路径占用
// （outputKey_ 为规范化后的冲突检测 key，与登记时使用的一致）
class RunningRemuxGuard {
public:
    RunningRemuxGuard(std::string operationId, std::string outputKey)
            : operationId_(std::move(operationId)), outputKey_(std::move(outputKey)) {}

    ~RunningRemuxGuard() {
        std::lock_guard<std::mutex> lock(gRemuxMutex);
        gRemuxOperations.erase(operationId_);
        if (gRunningRemuxCount > 0) {
            --gRunningRemuxCount;
        }
        gActiveOutputPaths.erase(outputKey_);
    }

private:
    std::string operationId_;
    std::string outputKey_;
};

static RemuxResult runRemuxOperation(const std::string &operationId,
                                     const std::string &inputPath,
                                     const std::string &outputPath,
                                     const std::string &allowedInputRoot,
                                     ProgressSink &progress) {
    // 锁外计算一次规范化 key，登记/互斥/注销三处使用同一值
    const std::string outputKey = outputConflictKey(outputPath);
    std::shared_ptr<RemuxOperation> op;
    {
        std::lock_guard<std::mutex> lock(gRemuxMutex);
        std::map<std::string, std::shared_ptr<RemuxOperation> >::iterator it =
                gRemuxOperations.find(operationId);
        if (it == gRemuxOperations.end()) {
            return RemuxResult{kCodeNotPrepared,
                               "operation '" + operationId + "' was not prepared"};
        }
        if (it->second->running.load()) {
            return RemuxResult{kCodeAlreadyRunning,
                               "operation '" + operationId + "' is already running"};
        }
        if (gRunningRemuxCount >= kMaxConcurrentRemuxes) {
            return RemuxResult{kCodeBusy,
                               "too many concurrent remux operations (limit " +
                               std::to_string(kMaxConcurrentRemuxes) + ")"};
        }
        if (gActiveOutputPaths.find(outputKey) != gActiveOutputPaths.end()) {
            return RemuxResult{kCodeAlreadyRunning,
                               "output path is already being written: " + outputPath};
        }
        it->second->running.store(true);
        ++gRunningRemuxCount;
        gActiveOutputPaths.insert(outputKey);
        op = it->second;
    }

    RunningRemuxGuard guard(operationId, outputKey);
    CurrentRemuxOperationScope scope(op);
    return writeMp4FromInput(inputPath, outputPath, allowedInputRoot, progress);
}

static bool cancelRemuxOperation(const std::string &operationId) {
    std::lock_guard<std::mutex> lock(gRemuxMutex);
    std::map<std::string, std::shared_ptr<RemuxOperation> >::iterator it =
            gRemuxOperations.find(operationId);
    if (it == gRemuxOperations.end()) {
        return false; // 不存在（可能已结束）
    }
    if (!it->second->running.load()) {
        // 已登记但尚未执行：直接注销，避免登记项泄漏
        gRemuxOperations.erase(it);
        return true;
    }
    it->second->cancelled.store(true);
    return true;
}

static void cancelAllRemuxOperations() {
    std::lock_guard<std::mutex> lock(gRemuxMutex);
    for (std::map<std::string, std::shared_ptr<RemuxOperation> >::iterator it =
            gRemuxOperations.begin(); it != gRemuxOperations.end();) {
        if (!it->second->running.load()) {
            it = gRemuxOperations.erase(it); // 未执行：直接注销
        } else {
            it->second->cancelled.store(true);
            ++it;
        }
    }
}

// ---------------------------------------------------------------------------
// JNI 边界
// ---------------------------------------------------------------------------

extern "C" JNIEXPORT jstring JNICALL
Java_com_louisgeek_myffmpeg_FFmpegUtil_getFFmpegVersion(JNIEnv *env, jclass clazz) {
    (void) clazz;
    return env->NewStringUTF(av_version_info());
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_louisgeek_myffmpeg_FFmpegUtil_prepareRemux(JNIEnv *env, jclass thiz, jstring operation_id) {
    (void) thiz;
    if (operation_id == nullptr) {
        return JNI_FALSE;
    }
    const char *id = env->GetStringUTFChars(operation_id, nullptr);
    bool ok = prepareRemuxOperation(id == nullptr ? "" : id);
    if (id != nullptr) env->ReleaseStringUTFChars(operation_id, id);
    return ok ? JNI_TRUE : JNI_FALSE;
}

static void setErrorOut(JNIEnv *env, jobjectArray errorOut, const std::string &message) {
    if (errorOut == nullptr || env->GetArrayLength(errorOut) < 1) {
        return;
    }
    jstring msg = env->NewStringUTF(message.c_str());
    if (msg == nullptr) {
        return;
    }
    env->SetObjectArrayElement(errorOut, 0, msg);
    env->DeleteLocalRef(msg);
}

extern "C" JNIEXPORT jint JNICALL
Java_com_louisgeek_myffmpeg_FFmpegUtil_executeRemuxNative(
        JNIEnv *env, jclass thiz, jstring operation_id, jstring input_path, jstring output_path,
        jstring allowed_input_root, jobject progress_listener, jobjectArray error_out) {
    (void) thiz;
    if (operation_id == nullptr || input_path == nullptr || output_path == nullptr) {
        setErrorOut(env, error_out, "operationId/inputPath/outputPath must not be null");
        return kCodeInvalidArgument;
    }

    const char *id = env->GetStringUTFChars(operation_id, nullptr);
    const char *input = env->GetStringUTFChars(input_path, nullptr);
    const char *output = env->GetStringUTFChars(output_path, nullptr);
    const char *root = allowed_input_root == nullptr
            ? nullptr
            : env->GetStringUTFChars(allowed_input_root, nullptr);

    if (id == nullptr || input == nullptr || output == nullptr ||
        (allowed_input_root != nullptr && root == nullptr)) {
        if (id != nullptr) env->ReleaseStringUTFChars(operation_id, id);
        if (input != nullptr) env->ReleaseStringUTFChars(input_path, input);
        if (output != nullptr) env->ReleaseStringUTFChars(output_path, output);
        if (root != nullptr) env->ReleaseStringUTFChars(allowed_input_root, root);
        setErrorOut(env, error_out, "GetStringUTFChars failed");
        return kCodeInternalError;
    }
    if (id[0] == '\0' || input[0] == '\0' || output[0] == '\0') {
        env->ReleaseStringUTFChars(operation_id, id);
        env->ReleaseStringUTFChars(input_path, input);
        env->ReleaseStringUTFChars(output_path, output);
        if (root != nullptr) env->ReleaseStringUTFChars(allowed_input_root, root);
        setErrorOut(env, error_out, "operationId/inputPath/outputPath must not be empty");
        return kCodeInvalidArgument;
    }

    ProgressSink progress;
    if (progress_listener != nullptr) {
        jclass listenerClass = env->GetObjectClass(progress_listener);
        if (listenerClass != nullptr) {
            progress.onProgress = env->GetMethodID(listenerClass, "onProgress", "(I)V");
            env->DeleteLocalRef(listenerClass);
        }
        if (env->ExceptionCheck()) {
            env->ExceptionClear();
            progress.onProgress = nullptr;
        }
        if (progress.onProgress != nullptr) {
            progress.env = env;
            progress.listener = progress_listener;
        }
    }

    const RemuxResult result = runRemuxOperation(
            id, input, output, root == nullptr ? std::string() : std::string(root), progress);

    env->ReleaseStringUTFChars(operation_id, id);
    env->ReleaseStringUTFChars(input_path, input);
    env->ReleaseStringUTFChars(output_path, output);
    if (root != nullptr) env->ReleaseStringUTFChars(allowed_input_root, root);

    setErrorOut(env, error_out, result.error);
    return result.code;
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_louisgeek_myffmpeg_FFmpegUtil_cancelRemux(JNIEnv *env, jclass thiz, jstring operation_id) {
    (void) thiz;
    if (operation_id == nullptr) {
        return JNI_FALSE;
    }
    const char *id = env->GetStringUTFChars(operation_id, nullptr);
    bool found = cancelRemuxOperation(id == nullptr ? "" : id);
    if (id != nullptr) env->ReleaseStringUTFChars(operation_id, id);
    return found ? JNI_TRUE : JNI_FALSE;
}

extern "C" JNIEXPORT void JNICALL
Java_com_louisgeek_myffmpeg_FFmpegUtil_cancelAllRemuxes(JNIEnv *env, jclass thiz) {
    (void) env;
    (void) thiz;
    cancelAllRemuxOperations();
}
