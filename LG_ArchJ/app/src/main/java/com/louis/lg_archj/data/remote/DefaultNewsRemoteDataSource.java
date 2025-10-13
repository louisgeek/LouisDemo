package com.louis.lg_archj.data.remote;

import android.util.Log;

import com.louis.lg_archj.data.remote.dto.NewsDto;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DefaultNewsRemoteDataSource implements NewsRemoteDataSource {
    private static final String TAG = "DefaultNewsRemoteDataSource";
    private final ExecutorService networkExecutor = Executors.newFixedThreadPool(3);

    @Override
    public CompletableFuture<List<NewsDto>> fetchData() {
        return CompletableFuture.supplyAsync(() -> {
                    Log.d(TAG, "执行远程请求，线程: " + Thread.currentThread().getName());
                    try {
                        // 模拟网络延迟（1秒）
                        Thread.sleep(1000);
                        // 模拟远程接口返回数据
                        List<NewsDto> remoteNews = new ArrayList<>();
                        remoteNews.add(new NewsDto("1", "Alice（远程）"));
                        remoteNews.add(new NewsDto("2", "Bob（远程）"));
                        remoteNews.add(new NewsDto("3", "Charlie（远程）"));
                        Log.d(TAG, "远程数据获取完成，数据量: " + remoteNews.size());
                        return remoteNews;
                    } catch (InterruptedException e) {
                        Log.e(TAG, "远程请求中断", e);
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("远程请求中断");
                    }
                }, networkExecutor)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        Log.e(TAG, "获取远程数据失败", throwable);
                    } else {
                        Log.d(TAG, "获取远程数据成功，数据量: " + (result != null ? result.size() : 0));
                    }
                });
    }

    @Override
    public void close() throws Exception {
        Log.d(TAG, "关闭远程数据源");
        networkExecutor.shutdown();
    }
}