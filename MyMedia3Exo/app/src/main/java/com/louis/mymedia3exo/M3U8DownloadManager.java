package com.louis.mymedia3exo;

import android.content.Context;
import android.util.Log;

import com.arthenica.ffmpegkit.FFmpegKit;
import com.arthenica.ffmpegkit.FFmpegSession;
import com.arthenica.ffmpegkit.Level;
import com.arthenica.ffmpegkit.LogCallback;
import com.arthenica.ffmpegkit.SessionState;
import com.arthenica.ffmpegkit.Statistics;
import com.arthenica.ffmpegkit.StatisticsCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class M3U8DownloadManager {
    private static final String TAG = "M3U8DownloadManager";
    private static M3U8DownloadManager instance;
    
    private Context context;
    private ExecutorService executorService;
    private List<DownloadTask> downloadTasks;
    private OnDownloadListener downloadListener;
    
    public static synchronized M3U8DownloadManager getInstance(Context context) {
        if (instance == null) {
            instance = new M3U8DownloadManager(context.getApplicationContext());
        }
        return instance;
    }
    
    private M3U8DownloadManager(Context context) {
        this.context = context;
        this.executorService = Executors.newFixedThreadPool(3);
        this.downloadTasks = new ArrayList<>();
    }
    
    public static class DownloadTask {
        private String taskId;
        private String m3u8Url;
        private String outputFileName;
        private DownloadStatus status;
        private String errorMessage;
        private int progress;
        private long totalSize;
        private long downloadedSize;
        private Future<?> future;
        
        public DownloadTask(String taskId, String m3u8Url, String outputFileName) {
            this.taskId = taskId;
            this.m3u8Url = m3u8Url;
            this.outputFileName = outputFileName;
            this.status = DownloadStatus.PENDING;
            this.progress = 0;
        }
        
        // Getters and setters
        public String getTaskId() { return taskId; }
        public String getM3u8Url() { return m3u8Url; }
        public String getOutputFileName() { return outputFileName; }
        public DownloadStatus getStatus() { return status; }
        public void setStatus(DownloadStatus status) { this.status = status; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        public int getProgress() { return progress; }
        public void setProgress(int progress) { this.progress = progress; }
        public long getTotalSize() { return totalSize; }
        public void setTotalSize(long totalSize) { this.totalSize = totalSize; }
        public long getDownloadedSize() { return downloadedSize; }
        public void setDownloadedSize(long downloadedSize) { this.downloadedSize = downloadedSize; }
        public Future<?> getFuture() { return future; }
        public void setFuture(Future<?> future) { this.future = future; }
    }
    
    public enum DownloadStatus {
        PENDING,
        DOWNLOADING,
        PAUSED,
        COMPLETED,
        FAILED,
        CANCELLED
    }
    
    public interface OnDownloadListener {
        void onDownloadStarted(DownloadTask task);
        void onDownloadProgress(DownloadTask task, int progress, long downloadedSize, long totalSize);
        void onDownloadCompleted(DownloadTask task);
        void onDownloadFailed(DownloadTask task, String error);
        void onDownloadCancelled(DownloadTask task);
    }
    
    public void setOnDownloadListener(OnDownloadListener listener) {
        this.downloadListener = listener;
    }
    
    /**
     * 添加下载任务
     * @param m3u8Url m3u8链接
     * @param outputFileName 输出文件名
     * @return 任务ID
     */
    public String addDownloadTask(String m3u8Url, String outputFileName) {
        String taskId = String.valueOf(System.currentTimeMillis());
        DownloadTask task = new DownloadTask(taskId, m3u8Url, outputFileName);
        downloadTasks.add(task);
        processDownloadTask(task);
        return taskId;
    }
    
    /**
     * 处理单个下载任务
     * @param task 下载任务
     */
    private void processDownloadTask(DownloadTask task) {
        Future<?> future = executorService.submit(() -> {
            task.setStatus(DownloadStatus.DOWNLOADING);
            if (downloadListener != null) {
                downloadListener.onDownloadStarted(task);
            }
            
            // 确保输出目录存在
            File outputDir = new File(context.getExternalFilesDir(null), "downloads");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            
            String outputPath = new File(outputDir, task.getOutputFileName()).getAbsolutePath();
            
            // 构建FFmpeg命令，使用更高级的参数确保兼容性和质量
            String command = buildFFmpegCommand(task.getM3u8Url(), outputPath);
            
            Log.d(TAG, "Executing FFmpeg command: " + command);
            
            try {
                FFmpegSession session = FFmpegKit.executeAsync(
                    command, null);
                
                if (session.getState() == SessionState.FAILED) {
                    task.setStatus(DownloadStatus.FAILED);
                    task.setErrorMessage(session.getFailStackTrace());
                    if (downloadListener != null) {
                        downloadListener.onDownloadFailed(task, session.getFailStackTrace());
                    }
                    Log.e(TAG, "Download failed: " + task.getOutputFileName() + 
                          ", error: " + session.getFailStackTrace());
                } else if (session.getState() == SessionState.COMPLETED) {
                    task.setStatus(DownloadStatus.COMPLETED);
                    if (downloadListener != null) {
                        downloadListener.onDownloadCompleted(task);
                    }
                    Log.d(TAG, "Download completed: " + task.getOutputFileName());
                }
            } catch (Exception e) {
                task.setStatus(DownloadStatus.FAILED);
                task.setErrorMessage(e.getMessage());
                if (downloadListener != null) {
                    downloadListener.onDownloadFailed(task, e.getMessage());
                }
                Log.e(TAG, "Download exception: " + e.getMessage(), e);
            }
        });
        
        task.setFuture(future);
    }
    
    /**
     * 构建FFmpeg命令
     * @param inputUrl 输入URL
     * @param outputPath 输出路径
     * @return FFmpeg命令
     */
    private String buildFFmpegCommand(String inputUrl, String outputPath) {
        // 使用推荐的参数来处理m3u8流并转换为mp4
        return String.format(
            "-i \"%s\" " +
            "-c:v libx264 " +             // 视频编码为H.264
            "-c:a aac " +                 // 音频编码为AAC
            "-bsf:a aac_adtstoasc " +     // 音频比特流过滤器
            "-movflags +faststart " +     // 优化MP4以支持快速启动
            "\"%s\"", 
            inputUrl, 
            outputPath
        );
    }
    
    /**
     * 更新进度信息
     * @param task 下载任务
     * @param statistics FFmpeg统计信息
     */
    private void updateProgress(DownloadTask task, Statistics statistics) {
        // 这里可以根据statistics中的信息更新进度
        // 由于m3u8流的特殊性，我们使用时间基准来估算进度
//        long timeInMilliseconds = statistics.getTime();
//        if (timeInMilliseconds > 0) {
//            // 注意：对于流媒体，我们无法准确获取总大小，只能根据已处理时间来显示进度
//            int progress = (int) (timeInMilliseconds / 1000); // 简化处理
//            task.setProgress(progress);
//
//            if (downloadListener != null) {
//                downloadListener.onDownloadProgress(task, progress, 0, 0);
//            }
//        }
    }
    
    /**
     * 暂停下载任务
     * @param taskId 任务ID
     */
    public void pauseDownload(String taskId) {
        for (DownloadTask task : downloadTasks) {
            if (task.getTaskId().equals(taskId) && 
                task.getStatus() == DownloadStatus.DOWNLOADING) {
                task.setStatus(DownloadStatus.PAUSED);
                if (task.getFuture() != null) {
                    task.getFuture().cancel(true);
                }
                if (downloadListener != null) {
                    downloadListener.onDownloadProgress(task, task.getProgress(), 
                                                       task.getDownloadedSize(), task.getTotalSize());
                }
                break;
            }
        }
    }
    
    /**
     * 取消下载任务
     * @param taskId 任务ID
     */
    public void cancelDownload(String taskId) {
        for (DownloadTask task : downloadTasks) {
            if (task.getTaskId().equals(taskId)) {
                task.setStatus(DownloadStatus.CANCELLED);
                if (task.getFuture() != null) {
                    task.getFuture().cancel(true);
                }
                if (downloadListener != null) {
                    downloadListener.onDownloadCancelled(task);
                }
                break;
            }
        }
    }
    
    /**
     * 获取所有下载任务
     * @return 下载任务列表
     */
    public List<DownloadTask> getDownloadTasks() {
        return new ArrayList<>(downloadTasks);
    }
    
    /**
     * 根据URL获取文件名
     * @param url URL
     * @return 建议的文件名
     */
    public static String getFileNameFromUrl(String url) {
        try {
            Pattern pattern = Pattern.compile(".*/([^/?]+)\\.m3u8.*");
            Matcher matcher = pattern.matcher(url);
            if (matcher.matches()) {
                return matcher.group(1) + ".mp4";
            }
        } catch (Exception e) {
            Log.e(TAG, "Error extracting filename from URL", e);
        }
        return "video_" + System.currentTimeMillis() + ".mp4";
    }
    
    public void destroy() {
        if (executorService != null) {
            executorService.shutdown();
        }
        instance = null;
    }
}