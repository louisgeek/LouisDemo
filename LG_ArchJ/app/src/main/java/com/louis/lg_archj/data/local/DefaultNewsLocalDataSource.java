package com.louis.lg_archj.data.local;

import com.louis.lg_archj.domain.model.News;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DefaultNewsLocalDataSource implements NewsLocalDataSource {

    private final ExecutorService ioExecutor = Executors.newSingleThreadExecutor();
    private final List<News> localCache = new CopyOnWriteArrayList<>();

    @Override
    public CompletableFuture<List<News>> loadData() {
        return CompletableFuture.supplyAsync(() -> {
            // 模拟本地查询延迟（200ms）
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return new ArrayList<>(localCache); // 返回缓存副本
        }, ioExecutor);
    }

    @Override
    public CompletableFuture<Void> saveData(List<News> news) {
        return CompletableFuture.runAsync(() -> {
            // 模拟本地保存延迟（200ms）
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            localCache.clear();
            localCache.addAll(news); // 更新本地缓存
        }, ioExecutor);
    }

    @Override
    public void close() throws Exception {
        ioExecutor.shutdown();
    }
}