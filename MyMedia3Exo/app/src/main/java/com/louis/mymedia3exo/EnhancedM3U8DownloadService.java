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

public class EnhancedM3U8DownloadService extends Service {
    private static final String TAG = "EnhancedM3U8Download";
    
    private final IBinder binder = new LocalBinder();
    private ExecutorService executorService;
    private List<DownloadTask> downloadTasks;
    
    public class LocalBinder extends Binder {
        EnhancedM3U8DownloadService getService() {
            return EnhancedM3U8DownloadService.this;
        }
    }
    
    public static class DownloadTask {
        private String m3u8Url;
        private String outputFileName;
        private DownloadStatus status;
        private String errorMessage;
        private int progress;
        private String audioCodec;
        
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
        public String getAudioCodec() { return audioCodec; }
        public void setAudioCodec(String audioCodec) { this.audioCodec = audioCodec; }
    }
    
    public enum DownloadStatus {
        PENDING,
        ANALYZING,
        DOWNLOADING,
        COMPLETED,
        FAILED
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        executorService = Executors.newFixedThreadPool(3);
        downloadTasks = new ArrayList<>();
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
    
    public void addDownloadTask(String m3u8Url, String outputFileName) {
        DownloadTask task = new DownloadTask(m3u8Url, outputFileName);
        downloadTasks.add(task);
        processDownloadTask(task);
    }
    
    private void processDownloadTask(DownloadTask task) {
        executorService.execute(() -> {
            task.setStatus(DownloadStatus.ANALYZING);
            
            File outputDir = new File(getExternalFilesDir(null), "downloads");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            
            String outputPath = new File(outputDir, task.getOutputFileName()).getAbsolutePath();
            
            try {
                // 分析媒体信息
                String audioCodec = analyzeMediaInfo(task.getM3u8Url());
                task.setAudioCodec(audioCodec);
                
                task.setStatus(DownloadStatus.DOWNLOADING);
                
                // 根据音频编解码器选择合适的命令
                String command = buildOptimalCommand(task.getM3u8Url(), outputPath, audioCodec);
                Log.d(TAG, "Executing FFmpeg command: " + command);
                
                FFmpegSession session = FFmpegKit.execute(command);
                
                if (ReturnCode.isSuccess(session.getReturnCode())) {
                    task.setStatus(DownloadStatus.COMPLETED);
                    Log.d(TAG, "Download completed: " + task.getOutputFileName());
                } else {
                    // 尝试通用的备用命令
                    Log.w(TAG, "Primary command failed, trying universal fallback");
                    String fallbackCommand = buildUniversalFallbackCommand(task.getM3u8Url(), outputPath);
                    
                    FFmpegSession fallbackSession = FFmpegKit.execute(fallbackCommand);
                    
                    if (ReturnCode.isSuccess(fallbackSession.getReturnCode())) {
                        task.setStatus(DownloadStatus.COMPLETED);
                        Log.d(TAG, "Download completed with fallback: " + task.getOutputFileName());
                    } else {
                        task.setStatus(DownloadStatus.FAILED);
                        task.setErrorMessage("Primary: " + session.getFailStackTrace() + 
                                           "\nFallback: " + fallbackSession.getFailStackTrace());
                        Log.e(TAG, "All attempts failed for: " + task.getOutputFileName());
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
     * min版本不支持FFprobe，返回默认值
     */
    private String analyzeMediaInfo(String url) {
        Log.d(TAG, "Using min version - skipping media analysis");
        return "unknown";
    }
    
    /**
     * min版本简化命令
     */
    private String buildOptimalCommand(String inputUrl, String outputPath, String audioCodec) {
        return String.format(
            "-i \"%s\" -c copy \"%s\"",
            inputUrl, outputPath
        );
    }
    
    /**
     * min版本备用命令
     */
    private String buildUniversalFallbackCommand(String inputUrl, String outputPath) {
        return String.format(
            "-i \"%s\" -c copy \"%s\"",
            inputUrl, outputPath
        );
    }
    
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