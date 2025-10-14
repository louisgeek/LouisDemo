package com.louis.lg_archj.data.local;

import android.content.Context;
import android.util.Log;

import com.louis.lg_archj.data.local.database.NewsDatabase;
import com.louis.lg_archj.data.local.dao.NewsDao;
import com.louis.lg_archj.data.local.entity.NewsEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.collection.LruCache;

public class DefaultNewsLocalDataSource implements NewsLocalDataSource {
    private static final String TAG = "DefaultNewsLocalDataSource";
    private static final long CACHE_EXPIRE_TIME = 1 * 10 * 1000;
    private static final String CACHE_KEY = "news_list";
    private final ExecutorService ioExecutor = Executors.newSingleThreadExecutor();
    private final LruCache<String, List<NewsEntity>> memoryCache = new LruCache<>(1);
    private volatile long cacheTimestamp = 0;
    private final NewsDao newsDao;

    public DefaultNewsLocalDataSource(Context context) {
        NewsDatabase database = NewsDatabase.getDatabase(context);
        this.newsDao = database.newsDao();
    }

    @Override
    public CompletableFuture<List<NewsEntity>> queryData() {
        return CompletableFuture.supplyAsync(() -> {
            Log.d(TAG, "查询本地数据，线程: " + Thread.currentThread().getName());

            // 检查内存缓存是否过期
            if (isCacheExpired()) {
                Log.d(TAG, "内存缓存已过期，清空内存缓存");
                memoryCache.evictAll();
                cacheTimestamp = 0;
            }

            // 优先从内存缓存获取
            List<NewsEntity> cachedData = memoryCache.get(CACHE_KEY);
            if (cachedData != null) {
                Log.d(TAG, "从内存缓存获取数据，数据量: " + cachedData.size());
                return new ArrayList<>(cachedData);
            }

            // 从数据库获取
            List<NewsEntity> dbData = newsDao.getAllNews();
            if (!dbData.isEmpty()) {
                Log.d(TAG, "从数据库获取数据，数据量: " + dbData.size());
                memoryCache.put(CACHE_KEY, new ArrayList<>(dbData));
                cacheTimestamp = System.currentTimeMillis();
                return new ArrayList<>(dbData);
            }

            Log.d(TAG, "本地无数据");
            return new ArrayList<>();
        }, ioExecutor);
    }

    @Override
    public CompletableFuture<Void> saveData(List<NewsEntity> news) {
        return CompletableFuture.runAsync(() -> {
            Log.d(TAG, "保存本地数据，线程: " + Thread.currentThread().getName());

            // 保存到数据库
            newsDao.deleteAll();
            newsDao.insertAll(news);

            // 更新内存缓存
            memoryCache.put(CACHE_KEY, new ArrayList<>(news));
            cacheTimestamp = System.currentTimeMillis();

            Log.d(TAG, "保存本地数据完成，新数据量: " + news.size());
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