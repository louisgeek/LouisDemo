package com.louis.myffmpegkit;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.louis.myffmpegkit.download.DownloadCallback;
import com.louis.myffmpegkit.download.DownloadTask;
import com.louis.myffmpegkit.download.VideoDownloader;

import java.io.File;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 */
public class MainActivity extends AppCompatActivity {
    private Handler mainHandler;
    private VideoDownloader videoDownloader;
    //
    private EditText etUrls;
    private Button btnDownload;
    private TextView tvLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainHandler = new Handler(Looper.getMainLooper());
        videoDownloader = new VideoDownloader(1);

        initViews();
        setupClickListeners();
    }

    /**
     * 初始化视图组件
     */
    private void initViews() {
        etUrls = findViewById(R.id.etUrls);
        btnDownload = findViewById(R.id.btnDownload);
        tvLog = findViewById(R.id.tvLog);

        etUrls.setText("http://44.222.168.138:3000/hls/hls-noaudio/WorkingWithPhotos.m3u8\n" +
                "http://44.222.168.138:3000/hls/hls-mp3-128/WorkingWithPhotos.m3u8\n");
    }

    private void setupClickListeners() {
        btnDownload.setOnClickListener(v -> {
            String urlsText = etUrls.getText().toString().trim();
            if (urlsText.isEmpty()) {
                logMessage("请输入M3U8链接");
                return;
            }
            String[] urlArr = urlsText.split("\n");
//            List<String> urlList = Arrays.asList(urlArr);
            List<String> urlList = Arrays.stream(urlArr).collect(Collectors.toList());
            startBatchDownload(urlList);
        });
    }

    private void startBatchDownload(List<String> urls) {
        if (urls.size() > 10) {
            logMessage("警告：同时下载超过10个文件可能影响性能");
        }

        btnDownload.setEnabled(false);
        btnDownload.setText("下载中...");
        tvLog.setText("");

        File outputDir = new File(getExternalFilesDir(Environment.DIRECTORY_MOVIES), "downloads");
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            logMessage("错误：无法创建输出目录");
            resetDownloadButton();
            return;
        }

        logMessage("开始批量下载 " + urls.size() + " 个文件");
        logMessage("输出目录: " + outputDir.getAbsolutePath());
        logMessage("---");

        List<DownloadTask> urlTaskList = new ArrayList<>();
        for (int i = 0; i < urls.size(); i++) {
            String urlString = urls.get(i).trim();
            String filePath = outputDir + File.separator + "video_" + (i + 1) + ".mp4";
            String headers = "";
            urlTaskList.add(new DownloadTask(i + 1, urlString, filePath, headers));
        }

        videoDownloader.download(urlTaskList, new DownloadCallback() {
            @Override
            public void onTaskStart(DownloadTask task) {
                mainHandler.post(() -> logMessage("[任务 " + task.getTaskId() + "] 开始下载"));
            }

            @Override
            public void onTaskProgress(DownloadTask task, float progress) {
                mainHandler.post(() -> logMessage("[任务 " + task.getTaskId() + "] " + progress));
            }

            @Override
            public void onTaskComplete(DownloadTask task) {
                mainHandler.post(() -> logMessage("✓ [任务 " + task.getTaskId() + "] 下载完成"));
            }

            @Override
            public void onTaskFailed(DownloadTask task, String error) {
                mainHandler.post(() -> logMessage("✗ [任务 " + task.getTaskId() + "] 下载失败: " + error));
                Log.e("TAG", "onTaskFailed: [任务 " + task.getTaskId() + "] 下载失败: " + error);
            }

            @Override
            public void onAllTasksComplete(int total, int success, int failed) {
                mainHandler.post(() -> {
                    logMessage("---");
                    logMessage("所有下载任务完成 (" + success + "/" + total + ")");
                    if (failed > 0) {
                        logMessage("失败任务数: " + failed);
                    }
                    resetDownloadButton();
                });
            }
        });
    }

    private void resetDownloadButton() {
        btnDownload.setEnabled(true);
        btnDownload.setText("开始批量下载");
    }

    private void logMessage(String message) {
        tvLog.append(message);
        tvLog.append("\n");
        // 自动滚动到底部
        tvLog.post(() -> {
            int scrollAmount = tvLog.getLayout().getLineTop(tvLog.getLineCount()) - tvLog.getHeight();
            if (scrollAmount > 0) {
                tvLog.scrollTo(0, scrollAmount);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        new Thread(() -> {
            videoDownloader.cancelAllTasks();
            videoDownloader.release();
        }).start();
        mainHandler.removeCallbacksAndMessages(null);
    }
}