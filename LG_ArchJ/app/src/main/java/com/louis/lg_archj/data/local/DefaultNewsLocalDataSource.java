package com.louis.lg_archj.data.local;

import android.util.Log;

import com.louis.lg_archj.data.local.entity.NewsEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class DefaultNewsLocalDataSource implements NewsLocalDataSource {
    private static final String TAG = "DefaultNewsLocalDataSource";
    private final ExecutorService ioExecutor = Executors.newSingleThreadExecutor();
    private final List<NewsEntity> localCache = new CopyOnWriteArrayList<>();

    @Override
    public CompletableFuture<List<NewsEntity>> queryData() {
        return CompletableFuture.supplyAsync(() -> {
                    Log.d(TAG, "查询本地数据，线程: " + Thread.currentThread().getName());
                    // 模拟本地查询延迟（200ms）
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "本地查询被中断", e);
                        Thread.currentThread().interrupt();
                    }
                    Log.d(TAG, "本地数据加载完成，数据量: " + localCache.size());
                    return new ArrayList<>(localCache).stream().collect(Collectors.toList()); // 返回缓存副本
                }, ioExecutor)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        Log.e(TAG, "加载本地数据失败", throwable);
                    } else {
                        Log.d(TAG, "加载本地数据成功，数据量: " + result.size());
                    }
                });
    }

    @Override
    public CompletableFuture<Void> saveData(List<NewsEntity> news) {
        return CompletableFuture.runAsync(() -> {
                    Log.d(TAG, "保存本地数据，线程: " + Thread.currentThread().getName());
                    // 模拟本地保存延迟（200ms）
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "本地保存被中断", e);
                        Thread.currentThread().interrupt();
                    }
                    localCache.clear();
                    localCache.addAll(news); // 更新本地缓存
                    Log.d(TAG, "本地数据保存完成，新数据量: " + localCache.size());
                }, ioExecutor)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        Log.e(TAG, "保存本地数据失败", throwable);
                    } else {
                        Log.d(TAG, "保存本地数据成功");
                    }
                });
    }

    @Override
    public void close() throws Exception {
        Log.d(TAG, "关闭本地数据源");
        ioExecutor.shutdown();
    }
}