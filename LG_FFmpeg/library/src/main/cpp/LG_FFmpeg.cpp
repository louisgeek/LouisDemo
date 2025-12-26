#include <jni.h>
#include <string>
#include <android/log.h>
#include <unistd.h>

#define LOG_TAG "LG_FFmpeg"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C" {
#include <libavformat/avformat.h>
#include <libavcodec/avcodec.h>
#include <libavutil/avutil.h>
#include <libswscale/swscale.h>
#include <libswresample/swresample.h>
#include <libavfilter/avfilter.h>
}



extern "C" JNIEXPORT jstring JNICALL
Java_com_louisgeek_ffmpeg_FFmpegUtil_getFFmpegVersion(JNIEnv *env, jclass clazz) {
    return env->NewStringUTF(av_version_info());
}

extern "C" JNIEXPORT jint JNICALL
Java_com_louisgeek_ffmpeg_FFmpegUtil_convertTsToMp4(JNIEnv *env, jclass thiz,
                                                    jstring inputPath, jstring outputPath) {
    const char *input = env->GetStringUTFChars(inputPath, nullptr);
    const char *output = env->GetStringUTFChars(outputPath, nullptr);

    //输入文件格式上下文
    AVFormatContext *inputCtx = nullptr;
    //打开输入文件
    if (avformat_open_input(&inputCtx, input, nullptr, nullptr) != 0) {
        LOGE("无法打开输入文件");
        return -1;
    }
    if (avformat_find_stream_info(inputCtx, nullptr) < 0) {
        LOGE("无法获取流信息");
        return -1;
    }
    //输出文件格式上下文
    AVFormatContext *outputCtx = nullptr;
    // 创建输出上下文
    avformat_alloc_output_context2(&outputCtx, nullptr, "mp4", output);
    if (!outputCtx) {
        LOGE("无法创建输出上下文");
        return AVERROR_UNKNOWN;
    }
    //复制流到输出容器
    for (int i = 0; i < inputCtx->nb_streams; i++) {
        AVStream *in_stream = inputCtx->streams[i];
        AVStream *out_stream = avformat_new_stream(outputCtx, nullptr);
        if (!out_stream) {
            LOGE("无法创建新流");
            return AVERROR_UNKNOWN;
        }
        int ret = avcodec_parameters_copy(out_stream->codecpar, in_stream->codecpar);
        if (ret < 0) {
            LOGE("Failed to copy codec parameters");
            return AVERROR_UNKNOWN;
        }
        out_stream->codecpar->codec_tag = 0;
//        out_stream->time_base = in_stream->time_base;
    }

    // 打开输出文件
    if (!(outputCtx->oformat->flags & AVFMT_NOFILE)) {
        if (avio_open(&outputCtx->pb, output, AVIO_FLAG_WRITE) < 0) {
            LOGE("无法打开输出文件");
            return AVERROR_UNKNOWN;
        }
    }

    // 写入文件头
    if (avformat_write_header(outputCtx, NULL) < 0) {
        LOGE("写入头信息失败");
        return AVERROR_UNKNOWN;
    }

    //读取并写入数据包 转换数据包
    AVPacket packet;
    while (av_read_frame(inputCtx, &packet) >= 0) {
        AVStream *in_stream = inputCtx->streams[packet.stream_index];
        AVStream *out_stream = outputCtx->streams[packet.stream_index];

        // 转换时间戳
        av_packet_rescale_ts(&packet, in_stream->time_base, out_stream->time_base);
        packet.pos = -1;

        if (av_interleaved_write_frame(outputCtx, &packet) < 0) {
            LOGE("写入帧失败");
            break;
        }
        av_packet_unref(&packet);
    }

    // 写入文件尾
    av_write_trailer(outputCtx);

    // 清理资源
    avformat_close_input(&inputCtx);
    if (outputCtx && !(outputCtx->oformat->flags & AVFMT_NOFILE)) {
        avio_closep(&outputCtx->pb);
    }
    avformat_free_context(outputCtx);

    env->ReleaseStringUTFChars(inputPath, input);
    env->ReleaseStringUTFChars(outputPath, output);

    return 0; // 成功
}
