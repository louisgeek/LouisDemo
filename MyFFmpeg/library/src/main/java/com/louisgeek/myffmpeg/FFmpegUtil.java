package com.louisgeek.myffmpeg;

/**
 * FFmpeg 本地转封装工具（TS / fMP4 / 本地 HLS -> MP4，stream copy）。
 *
 * <h3>支持矩阵</h3>
 * <ul>
 *   <li>输入：本地 TS、fMP4、本地 M3U8/M3U 清单及其本地分片；不支持远程 URL。</li>
 *   <li>输出：MP4（+faststart，moov 前置）。</li>
 *   <li>视频：任意编码 stream copy，不转码；H.265 写 hvc1 tag。</li>
 *   <li>音频：仅 AAC 可直拷，其他编码返回 {@link RemuxResult#CODE_UNSUPPORTED_STREAM}。</li>
 *   <li>字幕/数据流：丢弃。</li>
 * </ul>
 *
 * <h3>线程与并发</h3>
 * <ul>
 *   <li>{@link #executeRemux} 在调用线程同步执行，必须在后台线程调用。</li>
 *   <li>全局最多 2 个并发转封装；同一输出路径不允许并行写入，超限返回
 *       {@link RemuxResult#CODE_BUSY}。</li>
 *   <li>取消在下一个包边界或 FFmpeg IO 中断点生效，通常 100ms 内；
 *       单次阻塞 IO 期间不保证立即返回。</li>
 * </ul>
 *
 * <h3>输出安全</h3>
 * <ul>
 *   <li>先写 {@code <output>.part} 临时文件，成功后原子 rename 到目标路径；
 *       失败/取消只删除临时文件，不会删除目标路径上已有的文件。</li>
 *   <li>本地 M3U8 会预扫描全部引用（分片/子清单/KEY/MAP），要求都位于
 *       allowedInputRoot 内（未指定时默认为清单所在目录），且不允许远程 URI、
 *       不允许 URI 携带 query/fragment；含媒体分片的清单必须带 #EXT-X-ENDLIST
 *       （本地 VOD 包），否则视为 live/半截清单并拒绝。</li>
 *   <li>native 层设置 FFmpeg 协议白名单（file,crypto），从 IO 层禁止一切网络访问；
 *       crypto 仅用于本地 AES-128 密钥/分片解密。</li>
 * </ul>
 */
public final class FFmpegUtil {
    static {
        System.loadLibrary("myffmpeg");
    }

    private FFmpegUtil() {
    }

    public static native String getFFmpegVersion();

    /**
     * 登记一个转封装操作。
     *
     * <h3>operationId 生命周期</h3>
     * <ul>
     *   <li>登记项在 {@code executeRemux} 返回（无论成功/失败/取消）后自动注销；
     *       对同一 operationId 再次执行必须重新 prepare。</li>
     *   <li>返回 {@link RemuxResult#CODE_BUSY} / {@link RemuxResult#CODE_ALREADY_RUNNING}
     *       时不会注销登记项，可用原 operationId 直接重试。</li>
     *   <li>{@link #cancelRemux} 对尚未执行的操作会直接注销登记项。</li>
     * </ul>
     *
     * @param operationId 操作唯一标识（调用方自定义，如 taskId:fileIndex）
     * @return true 登记成功；false 表示该 operationId 为空或已存在
     */
    public static native boolean prepareRemux(String operationId);

    /**
     * 执行转封装（需先 {@link #prepareRemux}）。M3U8 引用默认限制在清单所在目录
     * （等价于 5 参重载的 allowedInputRoot 传 null）；需要放宽引用范围时改用
     * {@link #executeRemux(String, String, String, String, ProgressListener)}
     * 显式指定根目录。
     *
     * @see #executeRemux(String, String, String, String, ProgressListener)
     */
    public static RemuxResult executeRemux(String operationId, String inputPath, String outputPath) {
        return executeRemux(operationId, inputPath, outputPath, null, null);
    }

    /**
     * 执行转封装：本地输入（TS / fMP4 / 本地 M3U8 清单及其分片）-> MP4。
     *
     * @param operationId     操作唯一标识
     * @param inputPath       输入文件路径（本地真实路径，不接受 content:// 等 Uri）
     * @param outputPath      MP4 输出路径
     * @param allowedInputRoot 本地 M3U8 引用文件的允许根目录；null 时默认为清单所在目录。
     *                         清单引用的分片/子清单/KEY/MAP 必须都在该目录树内，否则返回
     *                         {@link RemuxResult#CODE_INSECURE_INPUT}。非清单输入忽略此参数
     * @param listener        进度回调，可为 null
     * @return 结构化结果，见 {@link RemuxResult}
     */
    public static RemuxResult executeRemux(String operationId, String inputPath, String outputPath,
                                           String allowedInputRoot, ProgressListener listener) {
        if (operationId == null || operationId.isEmpty()
                || inputPath == null || inputPath.isEmpty()
                || outputPath == null || outputPath.isEmpty()) {
            return new RemuxResult(RemuxResult.CODE_INVALID_ARGUMENT,
                    "operationId/inputPath/outputPath must not be empty");
        }
        final String[] error = {""};
        final int code = executeRemuxNative(operationId, inputPath, outputPath,
                allowedInputRoot, listener, error);
        return new RemuxResult(code, error[0]);
    }

    private static native int executeRemuxNative(String operationId, String inputPath,
                                                 String outputPath, String allowedInputRoot,
                                                 ProgressListener listener, String[] errorOut);

    /**
     * 请求取消指定操作（可在任意线程调用，不会阻塞）。
     *
     * <p>未执行的操作会被直接注销；执行中的操作只置取消标志，
     * 在下一个包边界/IO 中断点生效。
     *
     * @param operationId 操作唯一标识
     * @return true 找到并已取消/注销；false 表示操作不存在（可能已结束）
     */
    public static native boolean cancelRemux(String operationId);

    /**
     * 取消所有进行中的转封装操作（页面/插件释放时调用，防止 native 层残留执行）。
     */
    public static native void cancelAllRemuxes();
}
