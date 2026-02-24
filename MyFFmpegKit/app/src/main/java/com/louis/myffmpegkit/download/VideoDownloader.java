package com.louis.myffmpegkit.download;

import android.util.Log;

import androidx.annotation.WorkerThread;

import com.arthenica.ffmpegkit.FFmpegKit;
import com.arthenica.ffmpegkit.FFmpegKitConfig;
import com.arthenica.ffmpegkit.FFmpegSession;
import com.arthenica.ffmpegkit.Level;
import com.arthenica.ffmpegkit.ReturnCode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class VideoDownloader {
    private static final String TAG = "VideoDownloader";
    private static final int DEFAULT_THREAD_POOL_SIZE = 3;

    private final ExecutorService executor;
    private final Semaphore semaphore;
    private final AtomicBoolean isCancelled = new AtomicBoolean(false);
    private final CopyOnWriteArrayList<Future<?>> futureList = new CopyOnWriteArrayList<>();
    private final AtomicInteger completedTasks = new AtomicInteger(0);
    private final AtomicInteger successTasks = new AtomicInteger(0);
    private final AtomicInteger failedTasks = new AtomicInteger(0);
    private final OkHttpClient okHttpClient;

    public VideoDownloader() {
        this(DEFAULT_THREAD_POOL_SIZE);
    }

    public VideoDownloader(int maxConcurrent) {
        //日志
        FFmpegKitConfig.setLogLevel(Level.AV_LOG_ERROR);

        this.executor = Executors.newFixedThreadPool(maxConcurrent, r -> {
            Thread t = new Thread(r, "VideoDownloader-" + System.currentTimeMillis());
            t.setDaemon(false);
            return t;
        });
        this.semaphore = new Semaphore(maxConcurrent);

        this.okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    public void download(List<DownloadTask> taskUrlList, DownloadCallback callback) {
        if (taskUrlList == null || taskUrlList.isEmpty() || callback == null) {
            throw new IllegalArgumentException("下载参数不能为空");
        }
        if (executor.isShutdown()) {
            throw new IllegalStateException("线程已关闭");
        }
        //init
        isCancelled.set(false);
        futureList.clear();

        completedTasks.set(0);
        successTasks.set(0);
        failedTasks.set(0);

        for (DownloadTask downloadTask : taskUrlList) {
            Future<?> future = executor.submit(new Runnable() {
                @Override
                public void run() {
                    //执行下载
                    VideoDownloader.this.download(downloadTask, callback, taskUrlList.size());
                }
            });
            futureList.add(future);
        }

    }

    private void download(DownloadTask downloadTask, DownloadCallback callback, int totalTasks) {
        boolean acquired = false;
        String tempDir = null;
        //整体进度
        AtomicReference<Float> progress = new AtomicReference<>(0.0f);
        try {
            if (isCancelled.get() || Thread.currentThread().isInterrupted()) {
                Log.d(TAG, " checkCancelled: 取消下载 taskUrl=" + downloadTask.getTaskUrl());
                return;
            }
            acquired = semaphore.tryAcquire(10, TimeUnit.SECONDS);
            if (!acquired) {
                throw new RuntimeException("tryAcquire timeout");
            }
            if (isCancelled.get() || Thread.currentThread().isInterrupted()) {
                Log.d(TAG, " checkCancelled: 取消下载 taskUrl=" + downloadTask.getTaskUrl());
                return;
            }
            //标记下载中--开始下载
            downloadTask.setTaskStatus(DownloadTask.TaskStatus.STATUS_DOWNLOADING);
            callback.onTaskStart(downloadTask);
            //==================== step01 下载 .m3u8 文件 start ====================
            // http://68.79.33.76:8888/event/play.m3u8?eventId=1981667601725710341&force=true
            int taskId = downloadTask.getTaskId();
            String taskUrl = downloadTask.getTaskUrl(); //m3u8 url
            Log.d(TAG, " download: 开始下载 taskUrl=" + taskUrl);
            String headers = downloadTask.getHeaders();
            // /storage/emulated/0/Android/data/com.puwell.petv/files/record/luogan@puwell.com/-508289222/20251027/406a8eff7c38_406a8eff7c38_20251027093127000.mp4
            String outputPath = downloadTask.getOutputPath(); //mp4 输出的存放路径
            // /storage/emulated/0/Android/data/com.puwell.petv/files/record/luogan@puwell.com/-508289222/20251027/406a8eff7c38_406a8eff7c38_20251027093127000_temp
            tempDir = outputPath.replace(".mp4", "_temp"); //临时存放目录（比如存放 .m3u8 文件和 .ts 文件）
            File tempDirFile = new File(tempDir);
            boolean mkdirs = tempDirFile.mkdirs();
            //下载 M3U8 文件
            // /storage/emulated/0/Android/data/com.puwell.petv/files/record/luogan@puwell.com/-508289222/20251027/406a8eff7c38_406a8eff7c38_20251027093127000_temp/video.m3u8
            String m3u8Path = tempDir + "/video.m3u8";
            downloadFile(taskUrl, m3u8Path, headers, downProgress -> {
                //step01进度占0.1
                progress.set(0.1f * downProgress);
                callback.onTaskProgress(downloadTask, progress.get());
                Log.d(TAG, " zzff step01 taskId=" + taskId + " downProgress=" + downProgress + " 整体进度=" + progress);
            });
            Log.d(TAG, " downloadFile m3u8Path File 成功: " + m3u8Path);
            //---------------------- step01 下载 .m3u8 文件 end -----------------------
            if (isCancelled.get() || Thread.currentThread().isInterrupted()) {
                Log.d(TAG, " checkCancelled: 取消下载 taskUrl=" + downloadTask.getTaskUrl());
                cleanupFiles(tempDir);
                return;
            }
            //==================== step02 下载所有 TS 文件 start ====================
            //解析 M3U8 并下载所有 TS 文件
            List<String> remoteTsUrls = parseM3u8(m3u8Path, taskUrl);
            List<String> localTsPaths = downloadTsFiles(remoteTsUrls, tempDir, headers, downProgress -> {
                //step02进度占0.8
                progress.set(0.1f + 0.8f * downProgress);
                callback.onTaskProgress(downloadTask, progress.get());
//                XLog.e(" zzff step02 taskId=" + taskId + " downProgress=" + downProgress + " 整体进度=" + progress);
            });
            rewriteM3u8(m3u8Path, localTsPaths); //把本地路径覆写到 M3U8 文件中
            Log.d(TAG, " downloadTsFiles TS File 成功: " + m3u8Path);
            //---------------------- step02 下载所有 ts 文件 end ----------------------
            if (isCancelled.get() || Thread.currentThread().isInterrupted()) {
                Log.d(TAG, " checkCancelled: 取消下载 taskUrl=" + downloadTask.getTaskUrl());
                cleanupFiles(tempDir);
                return;
            }
            //=============== step03 合并 TS 文件转换为 MP4 文件 start ===============
            File m3u8File = new File(m3u8Path);
            if (!m3u8File.exists()) {
                Log.e(TAG, "M3U8 file not found: m3u8Path=" + m3u8Path);
                return;
            }
            Log.d(TAG, " downloadTsFiles file FFmpeg: " + m3u8Path);
            //合并所有 TS 文件转换为 MP4
            mergeToMp4(m3u8Path, outputPath);
            //step03进度占0.1
            float downProgress = 1.0f;
            progress.set(0.9f + 0.1f * downProgress);
            callback.onTaskProgress(downloadTask, progress.get());
            Log.d(TAG, " zzff step03  taskId=" + taskId + " downProgress=" + downProgress + " 整体进度=" + progress);
            //---------------- step03 合并 TS 文件转换为 MP4 文件 end ------------------
            if (isCancelled.get() || Thread.currentThread().isInterrupted()) {
                Log.d(TAG, " checkCancelled: 取消下载 taskUrl=" + downloadTask.getTaskUrl());
                cleanupFiles(tempDir, outputPath);
                return;
            }
            //最终校验文件是否存在且有数据
            File outputFile = new File(outputPath);
            if (outputFile.exists() && outputFile.length() > 0) {
                //整体完成
                successTasks.incrementAndGet();
                downloadTask.setTaskStatus(DownloadTask.TaskStatus.STATUS_COMPLETED);
                callback.onTaskComplete(downloadTask);
                //删除临时目录
                cleanupFiles(tempDir);
            } else {
                throw new RuntimeException("转换后的 MP4 文件为空或不存在");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); //重设中断标志
            handleTaskFailed(downloadTask, callback, "线程取消", tempDir);
        } catch (Exception e) {
            handleTaskFailed(downloadTask, callback, e.getMessage(), tempDir);
        } finally {
            if (acquired) {
                semaphore.release();
            }
            int completed = completedTasks.incrementAndGet();
            if (completed == totalTasks) {
                callback.onAllTasksComplete(totalTasks, successTasks.get(), failedTasks.get());
            }
        }
    }


    private void downloadFile(String url, String outputPath, String headers, Consumer<Float> callback) throws IOException {
        Request.Builder requestBuilder = new Request.Builder().url(url);
        if (headers != null && !headers.trim().isEmpty()) {
            String[] headerPairs = headers.split("\n");
            for (String headerPair : headerPairs) {
                String[] keyValue = headerPair.split(":", 2);
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim();
                    String value = keyValue[1].trim();
                    requestBuilder.header(key, value);
                }
            }
        }
        Request request = requestBuilder.build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                Log.d(TAG, " downloadFile: 下载失败 code=" + response.code() + " message=" + response.message());
                throw new IOException("下载失败: " + response.code() + " " + response.message());
            }
            File outputFile = new File(outputPath);
            File parentDir = outputFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                boolean mkdirs = parentDir.mkdirs();
            }
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                return;
            }
            long contentLength = responseBody.contentLength();
            long totalBytesRead = 0;
            float lastProgress = -1f;
            try (InputStream inputStream = responseBody.byteStream();
                 FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    if (isCancelled.get() || Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException("下载被取消");
                    }
                    outputStream.write(buffer, 0, bytesRead);
                    //进度处理
                    totalBytesRead += bytesRead;
                    if (callback != null && contentLength > 0) {
                        //文件下载进度
                        float downProgress = (float) totalBytesRead / contentLength;
                        if (Math.abs(downProgress - lastProgress) > 0.01f) {
                            callback.accept(downProgress);
                            lastProgress = downProgress;
                        }
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private List<String> downloadTsFiles(List<String> remoteTsUrls, String tempDir, String headers, Consumer<Float> callback) throws IOException, InterruptedException {
        List<String> localTsPaths = new ArrayList<>();
        int totalFiles = remoteTsUrls.size();
        for (int i = 0; i < remoteTsUrls.size(); i++) {
            if (isCancelled.get() || Thread.currentThread().isInterrupted()) {
                throw new InterruptedException("下载被取消");
            }
            String tsUrl = remoteTsUrls.get(i);
            String tsPath = tempDir + "/segment_" + String.format("%04d", i) + ".ts";

            final int currentIndex = i;
            downloadFile(tsUrl, tsPath, headers, new Consumer<Float>() {
                @Override
                public void accept(Float fileProgress) {
                    //fileProgress 0.0~1.0
                    //currentIndex 0, 1, 2
                    float allProgress = (currentIndex + fileProgress) / totalFiles;
                    //allProgress 0.0~1.0
                    callback.accept(allProgress);
                }
            });
            localTsPaths.add("segment_" + String.format("%04d", i) + ".ts");
        }
        return localTsPaths;
    }


    private List<String> parseM3u8(String m3u8Path, String baseUrl) throws IOException {
        List<String> tsUrls = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(m3u8Path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    if (!line.startsWith("http")) {
                        String base = baseUrl.substring(0, baseUrl.lastIndexOf('/') + 1);
                        line = base + line;
                    }
                    tsUrls.add(line);
                }
            }
        }
        return tsUrls;
    }

    private void rewriteM3u8(String m3u8Path, List<String> localTsPaths) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(m3u8Path))) {
            String line;
            int tsIndex = 0;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty() && !line.startsWith("#")) {
                    if (tsIndex < localTsPaths.size()) {
                        lines.add(localTsPaths.get(tsIndex));
                        tsIndex++;
                    }
                } else {
                    lines.add(line);
                }
            }
        }
        try (PrintWriter writer = new PrintWriter(new FileWriter(m3u8Path))) {
            for (String line : lines) {
                writer.println(line);
            }
        }
    }

    private void mergeToMp4(String m3u8Uri, String outputPath) throws Exception {
//        implementation files('libs/pwffmpeg-release-1.0.0.aar')
        //FFmpegUtil.convertTsToMp4(m3u8Uri, outputPath);
        String command = DownloadUtils.buildFFmpegCommand(m3u8Uri, outputPath, null);
//        String command = String.format("-f hls -i \"%s\" -c copy \"%s\"", m3u8Uri, outputPath);
        Log.d(TAG, "download: command=" + command);
        FFmpegSession ffmpegSession = FFmpegKit.execute(command);

//            FFmpegSession ffmpegSession2 = FFmpegKit.executeAsync(command, session -> {
////                videoDuration =  session.getDuration();
//            }, log -> {
//                //日志输出
//            }, statistics -> {
//                if (statistics != null) {
//                    double currentTimeMs = statistics.getTime();
//                    long totalTimeMs = statistics.getSize(); // 或使用其他字段获取总时长
//
//                    if (totalTimeMs > 0) {
//                        int progress = (int) ((currentTimeMs * 100) / totalTimeMs);
//                        progress = Math.min(progress, 100);
////                        callback.onTaskProgress(downloadTask, "进度: " + progress + "%");
//                       Log.d(TAG," mergeToMp4: 进度: " + progress + "%");
//                    }
//                }
//            });
        if (!ReturnCode.isSuccess(ffmpegSession.getReturnCode())) {
            String error = ffmpegSession.getFailStackTrace();
            if (error == null || error.trim().isEmpty()) {
                error = "FFmpeg 合并转换失败";
            }
            throw new RuntimeException(error);
        }
    }

    private void cleanupFiles(String... filePaths) {
        for (String filePath : filePaths) {
            if (filePath != null) {
                try {
                    deleteFilesOrDirs(filePath);
                } catch (Exception e) {
                    // 忽略清理错误
                }
            }
        }
    }

    /**
     * 递归删除
     * @param path
     */
    private void deleteFilesOrDirs(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File fi : files) {
                    deleteFilesOrDirs(fi.getAbsolutePath());
                }
            }
        }
        file.delete();
    }

    public void cancelAllTasks() {
        isCancelled.set(true);  //设置取消标志

        // 取消所有已提交但未完成的任务
        for (Future<?> future : futureList) {
            if (!future.isDone() && !future.isCancelled()) {
                future.cancel(true); //true 会尝试中断正在执行的线程
            }
        }
        futureList.clear();

        FFmpegKit.cancel(); //取消所有正在执行的会话
    }


    @WorkerThread
    public void release() {
        executorShutdown();
    }

    private void executorShutdown() {
        if (executor.isShutdown()) {
            return;
        }
        executor.shutdown(); //触发关闭，停止接收新任务，等待已提交任务完成
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow(); //超时后强制关闭
            }
        } catch (InterruptedException e) {
            executor.shutdownNow(); //强制关闭
            Thread.currentThread().interrupt(); //重设中断标志
        }
    }

    private void handleTaskFailed(DownloadTask downloadTask, DownloadCallback callback, String errorMsg, String... filePaths) {
        //更新失败状态
        downloadTask.setTaskStatus(DownloadTask.TaskStatus.STATUS_FAILED);
        downloadTask.setErrorMsg(errorMsg);
        callback.onTaskFailed(downloadTask, errorMsg);
        failedTasks.incrementAndGet();
        cleanupFiles(filePaths);
    }
}