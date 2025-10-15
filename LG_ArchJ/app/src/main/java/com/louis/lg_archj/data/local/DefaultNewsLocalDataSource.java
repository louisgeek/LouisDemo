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



public class DefaultNewsLocalDataSource implements NewsLocalDataSource {
    private static final String TAG = "DefaultNewsLocalDataSource";
    private static final long MEMORY_CACHE_EXPIRE_TIME = 10 * MemoryCache.TIME_UNIT_SECOND;
    private static final long DISK_CACHE_EXPIRE_TIME = 1 * MemoryCache.TIME_UNIT_MINUTE;
    private static final String CACHE_KEY = "news_list";
    private final ExecutorService ioExecutor = Executors.newSingleThreadExecutor();
    private final MemoryCache memoryCache = MemoryCache.getInstance();
    private final NewsDao newsDao;

    public DefaultNewsLocalDataSource(Context context) {
        NewsDatabase database = NewsDatabase.getDatabase(context);
        this.newsDao = database.newsDao();
    }

    @Override
    public CompletableFuture<List<NewsEntity>> queryData() {
        return CompletableFuture.supplyAsync(() -> {
            Log.d(TAG, "查询本地数据，线程: " + Thread.currentThread().getName());

            // 优先从内存缓存获取
            @SuppressWarnings("unchecked")
            List<NewsEntity> cachedData = (List<NewsEntity>) memoryCache.get(CACHE_KEY);
            if (cachedData != null) {
                for (NewsEntity cachedDatum : cachedData) {
                    cachedDatum.setTitle(cachedDatum.getTitle().replace("（内存）", "").replace("（磁盘）", "").replace("（网络）", "") + "（内存）");
                }
                Log.d(TAG, "从内存缓存获取数据，数据量: " + cachedData.size());
                return new ArrayList<>(cachedData);
            }

            // 从数据库获取
            List<NewsEntity> dbData = newsDao.getAllNews();
            if (!dbData.isEmpty()) {
                // 检查磁盘缓存是否过期
                if (isDiskCacheExpired(dbData.get(0).getTimestamp())) {
                    Log.d(TAG, "磁盘缓存已过期，清空数据库");
                    newsDao.deleteAll();
                    return new ArrayList<>();
                }
                
                Log.d(TAG, "从数据库获取数据，数据量: " + dbData.size());
                memoryCache.put(CACHE_KEY, new ArrayList<>(dbData), MEMORY_CACHE_EXPIRE_TIME);
                for (NewsEntity cachedDatum : dbData) {
                    cachedDatum.setTitle(cachedDatum.getTitle().replace("（内存）", "").replace("（磁盘）", "").replace("（网络）", "") + "（磁盘）");
                }
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
            memoryCache.put(CACHE_KEY, new ArrayList<>(news), MEMORY_CACHE_EXPIRE_TIME);

            Log.d(TAG, "保存本地数据完成，新数据量: " + news.size());
        }, ioExecutor);
    }


    private boolean isDiskCacheExpired(long diskTimestamp) {
        return (System.currentTimeMillis() - diskTimestamp) > DISK_CACHE_EXPIRE_TIME;
    }

    @Override
    public void close() throws Exception {
        Log.d(TAG, "关闭本地数据源");
        ioExecutor.shutdown();
    }
}