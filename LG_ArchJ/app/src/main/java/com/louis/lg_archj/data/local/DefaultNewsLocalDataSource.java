package com.louis.lg_archj.data.local;

import android.util.Log;

import com.louis.lg_archj.data.local.entity.NewsEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DefaultNewsLocalDataSource implements NewsLocalDataSource {
    private static final String TAG = "DefaultNewsLocalDataSource";
    private static final long CACHE_EXPIRE_TIME = 1 * 10 * 1000;
    private final ExecutorService ioExecutor = Executors.newSingleThreadExecutor();
    private final List<NewsEntity> localCache = new CopyOnWriteArrayList<>();
    private volatile long cacheTimestamp = 0;

    @Override
    public CompletableFuture<List<NewsEntity>> queryData() {
        return CompletableFuture.supplyAsync(() -> {
            Log.d(TAG, "查询本地数据，线程: " + Thread.currentThread().getName());

            // 检查缓存是否过期
            if (isCacheExpired()) {
                Log.d(TAG, "缓存已过期，清空缓存");
                localCache.clear();
                cacheTimestamp = 0;
            }
            
            // 模拟本地查询延迟
            try {
                int delay = 1000 + (int) (Math.random() * 2001);
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            Log.d(TAG, "查询本地数据完成，数据量: " + localCache.size());
            return new ArrayList<>(localCache); // 返回缓存副本
        }, ioExecutor);
    }

    @Override
    public CompletableFuture<Void> saveData(List<NewsEntity> news) {
        return CompletableFuture.runAsync(() -> {
            Log.d(TAG, "保存本地数据，线程: " + Thread.currentThread().getName());
            // 模拟本地保存延迟（200ms）
            try {
                int delay = 500 + (int) (Math.random() * 501);
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            localCache.clear();
            localCache.addAll(news); // 更新本地缓存
            cacheTimestamp = System.currentTimeMillis(); // 更新缓存时间戳
            Log.d(TAG, "保存本地数据完成，新数据量: " + localCache.size());
        }, ioExecutor);
    }

    private boolean isCacheExpired() {
        return cacheTimestamp == 0 || (System.currentTimeMillis() - cacheTimestamp) > CACHE_EXPIRE_TIME;
    }

    @Override
    public void close() throws Exception {
        Log.d(TAG, "关闭本地数据源");
        ioExecutor.shutdown();
    }
}