package com.louisgeek.ffmpeg;

/**
 * FFmpeg 命令执行工具
 */
public class FFmpegUtil {
    static {
        System.loadLibrary("LG_FFmpeg");
    }
    public static native String getFFmpegVersion();

    /**
     * TS 转 MP4
     * @param inputPath  输入的 TS 文件路径 (例如: /sdcard/input.ts)
     * @param outputPath 输出的 MP4 文件路径 (例如: /sdcard/output.mp4)
     * @return 0 成功, 非 0 失败
     */
    public static native int convertTsToMp4(String inputPath, String outputPath);

}