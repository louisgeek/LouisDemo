package com.louisgeek.myffmpeg;

/**
 * 转封装进度回调。
 *
 * <p>回调在调用 {@link FFmpegUtil#executeRemux} 的线程上同步执行，频率上限约 10 次/秒；
 * 0–99 表示进行中，100 表示输出已成功落盘（失败/取消不会回调 100）。
 * 请勿在回调里做耗时操作。
 */
public interface ProgressListener {
    void onProgress(int percent);
}
