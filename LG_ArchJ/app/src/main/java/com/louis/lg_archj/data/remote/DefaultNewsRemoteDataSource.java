package com.louis.lg_archj.data.remote;

import android.util.Log;

import com.louis.lg_archj.data.remote.dto.NewsDto;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public class DefaultNewsRemoteDataSource implements NewsRemoteDataSource {
    private static final String TAG = "DefaultNewsRemoteDataSource";
    private final ExecutorService networkExecutor = Executors.newFixedThreadPool(3);

    @Override
    public CompletableFuture<List<NewsDto>> fetchData() {
        return CompletableFuture.supplyAsync(() -> {
            Log.d(TAG, "请求远程数据，线程: " + Thread.currentThread().getName());
                    try {
                        // 模拟网络延迟
                        Thread.sleep(5000);
                        // 模拟远程接口返回数据
                        List<NewsDto> remoteNews = new ArrayList<>();
                        remoteNews.add(new NewsDto("1", "Alice（远程）"));
                        remoteNews.add(new NewsDto("2", "Bob（远程）"));
                        remoteNews.add(new NewsDto("3", "Charlie（远程）"));
                        int count = ThreadLocalRandom.current().nextInt(3, 8); // 3-7条数据
                        for (int i = 1; i <= count; i++) {
                            remoteNews.add(new NewsDto(String.valueOf(i), "News " + i + "（远程）"));
                        }
                        Log.d(TAG, "请求远程数据完成，数据量: " + remoteNews.size());
                        return remoteNews;
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("远程请求中断");
                    }
        }, networkExecutor);
    }

    @Override
    public void close() throws Exception {
        Log.d(TAG, "关闭远程数据源");
        networkExecutor.shutdown();
    }
}