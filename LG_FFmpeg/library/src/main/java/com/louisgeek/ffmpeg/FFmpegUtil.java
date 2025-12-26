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
     * 将TS文件转换为MP4格式
     * @param inputPath TS文件路径
     * @param outputPath MP4输出路径
     * @return 0成功，-1失败
     */
    public static native int convertTsToMp4(String inputPath, String outputPath);

}