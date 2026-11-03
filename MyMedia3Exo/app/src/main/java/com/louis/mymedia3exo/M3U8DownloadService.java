package com.louis.mymedia3exo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.arthenica.ffmpegkit.FFmpegKit;
import com.arthenica.ffmpegkit.FFmpegSession;
import com.arthenica.ffmpegkit.ReturnCode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class M3U8DownloadService extends Service {
    private static final String TAG = "M3U8DownloadService";
    
    private final IBinder binder = new LocalBinder();
    private ExecutorService executorService;
    private List<DownloadTask> downloadTasks;
    
    public class LocalBinder extends Binder {
        M3U8DownloadService getService() {
            return M3U8DownloadService.this;
        }
    }
    
    public static class DownloadTask {
        private String m3u8Url;
        private String outputFileName;
        private DownloadStatus status;
        private String errorMessage;
        private int progress;
        
        public DownloadTask(String m3u8Url, String outputFileName) {
            this.m3u8Url = m3u8Url;
            this.outputFileName = outputFileName;
            this.status = DownloadStatus.PENDING;
            this.progress = 0;
        }
        
        // Getters and setters
        public String getM3u8Url() { return m3u8Url; }
        public String getOutputFileName() { return outputFileName; }
        public DownloadStatus getStatus() { return status; }
        public void setStatus(DownloadStatus status) { this.status = status; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        public int getProgress() { return progress; }
        public void setProgress(int progress) { this.progress = progress; }
    }
    
    public enum DownloadStatus {
        PENDING,
        DOWNLOADING,
        COMPLETED,
        FAILED
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        executorService = Executors.newFixedThreadPool(3); // 同时处理3个下载任务
        downloadTasks = new ArrayList<>();
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
    
    /**
     * 添加下载任务
     * @param m3u8Url m3u8链接
     * @param outputFileName 输出文件名
     */
    public void addDownloadTask(String m3u8Url, String outputFileName) {
        DownloadTask task = new DownloadTask(m3u8Url, outputFileName);
        downloadTasks.add(task);
        processDownloadTask(task);
    }
    
    /**
     * 处理单个下载任务
     * @param task 下载任务
     */
    private void processDownloadTask(DownloadTask task) {
        executorService.execute(() -> {
            task.setStatus(DownloadStatus.DOWNLOADING);
            
            // 确保输出目录存在
            File outputDir = new File(getExternalFilesDir(null), "downloads");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            
            String outputPath = new File(outputDir, task.getOutputFileName()).getAbsolutePath();
            
            // 构建FFmpeg命令 - 使用更健壮的方法
            String command = buildFFmpegCommand(task.getM3u8Url(), outputPath);
            
            Log.d(TAG, "Executing FFmpeg command: " + command);
            
            try {
                FFmpegSession session = FFmpegKit.execute(command);
                
                if (ReturnCode.isSuccess(session.getReturnCode())) {
                    task.setStatus(DownloadStatus.COMPLETED);
                    Log.d(TAG, "Download completed: " + task.getOutputFileName());
                } else {
                    // 第一次失败，尝试备用命令
                    Log.w(TAG, "Copy command failed, trying re-encode for: " + task.getOutputFileName());
                    Log.w(TAG, "Error details: " + session.getFailStackTrace());
                    String fallbackCommand = buildFallbackFFmpegCommand(task.getM3u8Url(), outputPath);
                    Log.d(TAG, "Executing fallback FFmpeg command: " + fallbackCommand);
                    
                    FFmpegSession fallbackSession = FFmpegKit.execute(fallbackCommand);
                    
                    if (ReturnCode.isSuccess(fallbackSession.getReturnCode())) {
                        task.setStatus(DownloadStatus.COMPLETED);
                        Log.d(TAG, "Download completed with fallback: " + task.getOutputFileName());
                    } else {
                        task.setStatus(DownloadStatus.FAILED);
                        String primaryError = session.getFailStackTrace();
                        String fallbackError = fallbackSession.getFailStackTrace();
                        task.setErrorMessage("Copy failed: " + (primaryError != null ? primaryError : "Unknown error") + 
                                           "\nRe-encode failed: " + (fallbackError != null ? fallbackError : "Unknown error"));
                        Log.e(TAG, "Both copy and re-encode failed for: " + task.getOutputFileName());
                        Log.e(TAG, "Primary error: " + session.getFailStackTrace());
                        Log.e(TAG, "Fallback error: " + fallbackSession.getFailStackTrace());
                    }
                }
            } catch (Exception e) {
                task.setStatus(DownloadStatus.FAILED);
                task.setErrorMessage(e.getMessage());
                Log.e(TAG, "Download exception: " + e.getMessage(), e);
            }
        });
    }
    
    /**
     * 构建FFmpeg命令
     * @param inputUrl 输入URL
     * @param outputPath 输出路径
     * @return FFmpeg命令字符串
     */
    private String buildFFmpegCommand(String inputUrl, String outputPath) {
        // min版本简化命令
        return String.format(
            "-i \"%s\" -c copy \"%s\"",
            inputUrl, outputPath
        );
    }
    
    /**
     * 构建备用FFmpeg命令（当第一个命令失败时使用）
     * @param inputUrl 输入URL
     * @param outputPath 输出路径
     * @return 备用FFmpeg命令字符串
     */
    private String buildFallbackFFmpegCommand(String inputUrl, String outputPath) {
        // min版本备用方案：使用基础参数
        return String.format(
            "-i \"%s\" -c:v copy -c:a copy \"%s\"",
            inputUrl, outputPath
        );
    }
    
    /**
     * 获取所有下载任务
     * @return 下载任务列表
     */
    public List<DownloadTask> getDownloadTasks() {
        return new ArrayList<>(downloadTasks);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}