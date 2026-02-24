package com.louis.myffmpegkit.download;


public interface DownloadCallback {
    void onTaskStart(DownloadTask task);

    void onTaskProgress(DownloadTask task, float progress);

    void onTaskComplete(DownloadTask task);

    void onTaskFailed(DownloadTask task, String errorMsg);

    void onAllTasksComplete(int total, int success, int failed);

}