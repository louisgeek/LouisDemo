package com.louis.myffmpegkit.download;

import androidx.annotation.IntDef;

public class DownloadTask {
    private final int taskId;
    private final String taskUrl;
    private final String outputPath;
    private final String headers;

    @TaskStatus
    private volatile int taskStatus = TaskStatus.STATUS_PENDING;
    private String errorMsg;

    public DownloadTask(int taskId, String taskUrl, String outputPath, String headers) {
        this.taskId = taskId;
        this.taskUrl = taskUrl;
        this.outputPath = outputPath;
        this.headers = headers;
    }

    public int getTaskId() {
        return taskId;
    }

    public String getTaskUrl() {
        return taskUrl;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public String getHeaders() {
        return headers;
    }

    public int getTaskStatus() {
        return taskStatus;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @IntDef({
            TaskStatus.STATUS_PENDING,
            TaskStatus.STATUS_DOWNLOADING,
            TaskStatus.STATUS_COMPLETED,
            TaskStatus.STATUS_FAILED
    })
    public @interface TaskStatus {
        int STATUS_PENDING = 0;
        int STATUS_DOWNLOADING = 1;
        int STATUS_COMPLETED = 2;
        int STATUS_FAILED = 3;
    }
}