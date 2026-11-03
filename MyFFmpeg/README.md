# MyFFmpeg

Android 本地媒体转封装库（remux）：TS / fMP4 / 本地 HLS（M3U8 目录包）→ MP4，纯 stream copy 不转码。基于预编译 FFmpeg 6.1（`Lavf60.16.100`）动态库，Java + JNI（C++14）实现。

## 模块结构

| 模块 | 说明 |
|------|------|
| `library` | 转封装库 `com.louisgeek.myffmpeg`，对外 API 为 `FFmpegUtil` |
| `app` | Demo `com.louisgeek.myffmpegdemo`，模拟「下载模块已落盘、转换直接读目录」的生产调用形态 |

ABI：`arm64-v8a`、`armeabi-v7a`；minSdk 24；NDK r27 + CMake，16KB page size 已开启。

## 支持矩阵

- **输入**：本地 TS、fMP4、本地 M3U8/M3U 清单及分片（分片扩展名不限，`allowed_extensions=ALL` + `extension_picky=0`）。不支持远程 URL。
- **输出**：MP4（`movflags +faststart`，moov 前置；H.265 强制 `hvc1` tag）。
- **视频**：任意编码 stream copy；**音频**：仅 AAC 可直拷，非 AAC 返回 `CODE_UNSUPPORTED_STREAM`（本库不含转码管线，不产出不可播文件）。
- **字幕/数据流**：丢弃。
- **加密 HLS（AES-128）**：密钥为本地文件时理论上可解（协议白名单保留 `crypto`），未经实测。

## API

```java
String opId = "task:42";           // 调用方自定义唯一标识
FFmpegUtil.prepareRemux(opId);     // 登记操作（重复登记返回 false）

RemuxResult result = FFmpegUtil.executeRemux(
        opId,
        "/data/.../downloads/index.m3u8",   // 输入：清单或单文件的真实路径（不支持 content://）
        "/data/.../downloads/out.mp4",      // 输出
        "/data/.../downloads",              // 可选：M3U8 引用允许根目录，null = 清单所在目录
        percent -> { ... });                // 可选：进度回调 0-100，约 10 次/秒

if (result.code == RemuxResult.CODE_SUCCESS) { ... }

FFmpegUtil.cancelRemux(opId);      // 取消单个（任意线程，不阻塞）
FFmpegUtil.cancelAllRemuxes();     // 页面/插件释放时取消全部
```

### operationId 生命周期

- 执行结束（成功/失败/取消）后登记项自动注销，重试须重新 `prepareRemux`；
- `CODE_BUSY` / `CODE_ALREADY_RUNNING` 不注销登记项，可用原 opId 直接重试；
- `cancelRemux` 对尚未执行的操作直接注销。

### 线程与并发

- `executeRemux` 在调用线程同步执行，必须在后台线程调用；
- 全局并发上限 2（超出返回 `CODE_BUSY`）；同一输出路径（规范化后）不允许并行写入；
- 取消在下一个包边界或 FFmpeg IO 中断点生效，通常 100ms 内。

### 输出安全

- 先写 `<output>.part` 临时文件，成功后原子 rename；失败/取消只删临时文件，**不会破坏目标路径上的已有文件**；
- 时间戳统一减 `start_time` 归零 + `avoid_negative_ts make_zero` 兜底。

## 本地 HLS 安全契约

打开输入前会对清单做**递归预扫描**（含子清单，深度限 4，防循环引用），不满足即返回 `CODE_INSECURE_INPUT`：

1. 所有引用（分片/子清单/`EXT-X-KEY`/`EXT-X-MAP` 等 `URI="..."` 属性）必须位于 allowedInputRoot 目录树内；
2. 不允许任何远程 URI；**协议白名单 `file,crypto` 无条件生效**，网络访问在 FFmpeg IO 层被封死（白名单会传播到 HLS 嵌套打开的分片/密钥 IO）；
3. URI 不允许携带 query/fragment（`?`/`#`）；
4. 含媒体分片的清单必须带 `#EXT-X-ENDLIST`——无 ENDLIST 视为 live/半截清单拒绝，否则 HLS demuxer 会当作直播源反复重读清单永不返回。

扫描验证与 demuxer 打开使用同一 realpath 规范化路径，`../`、symlink 等写法不会造成「验证的文件 ≠ 实际打开的文件」。

## 构建

```bash
./gradlew :library:assembleDebug                 # 双 ABI 编译
./gradlew :library:assembleDebugAndroidTest      # 契约测试 APK
./gradlew :library:connectedDebugAndroidTest    # 真机契约测试（11 例，无需媒体素材）
```

预编译 FFmpeg .so 位于 `library/src/main/libs/<abi>/lib/`，以 IMPORTED 目标链接，不参与源码编译。构建日志中 "Unable to strip"（预编译 .so）与 "Deprecated Gradle features" 为既有噪声。

## 已知边界

- 远程 URL（http/https/hls）不支持：无网络上下文与权限假设，且协议白名单在 IO 层禁止；
- 非 AAC 音轨直接报错而非转码（FFmpeg 6.1 音频转码链路不在本库范围）；
- 输出为普通 MP4，未开 fragmented MP4（`+frag_keyframe+empty_moov`）；
- 探测绕过面（无后缀清单）已由协议白名单兜底：即使 FFmpeg 把非 `.m3u8` 文件 probe 成 HLS，也开不了任何网络 IO。
