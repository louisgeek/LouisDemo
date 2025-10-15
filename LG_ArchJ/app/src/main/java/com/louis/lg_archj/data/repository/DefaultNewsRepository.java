package com.louis.lg_archj.data.repository;

import android.util.Log;

import com.louis.lg_archj.core.result.Result;
import com.louis.lg_archj.data.local.MemoryCache;
import com.louis.lg_archj.data.local.NewsLocalDataSource;
import com.louis.lg_archj.data.mapper.NewsMapper;
import com.louis.lg_archj.domain.model.News;
import com.louis.lg_archj.domain.repository.NewsRepository;
import com.louis.lg_archj.data.remote.NewsRemoteDataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class DefaultNewsRepository implements NewsRepository {
    private static final String TAG = "DefaultNewsRepository";
    private static final String CACHE_KEY = "news_list";
    private static final long MEMORY_CACHE_EXPIRE_TIME = 10 * MemoryCache.TIME_UNIT_SECOND;

    private final NewsLocalDataSource localDataSource;
    private final NewsRemoteDataSource remoteDataSource;
    private final MemoryCache memoryCache;

    public DefaultNewsRepository(NewsLocalDataSource localDataSource, NewsRemoteDataSource remoteDataSource) {
        this.localDataSource = localDataSource;
        this.remoteDataSource = remoteDataSource;
        this.memoryCache = MemoryCache.getInstance();
    }

    @Override
    @SuppressWarnings("unchecked")
    public CompletableFuture<Result<List<News>>> getData() {
        // 先检查内存缓存
        List<News> cachedData = (List<News>) memoryCache.get(CACHE_KEY);
        if (cachedData != null) {
            Log.d(TAG, "从内存缓存获取数据，数据量: " + cachedData.size());
            return CompletableFuture.completedFuture(new Result.Success<>(new ArrayList<>(cachedData)));
        }

        return localDataSource.queryData()
                .thenCompose(localData -> {
                    if (localData != null && !localData.isEmpty()) {
                        Log.e(TAG, "本地有数据：直接返回" + localData);
                        List<News> newsList = localData.stream()
                                .map(NewsMapper::entityToModel)
                                .collect(Collectors.toList());
                        // 缓存到内存
                        memoryCache.put(CACHE_KEY, new ArrayList<>(newsList), MEMORY_CACHE_EXPIRE_TIME);
                        return CompletableFuture.completedFuture(new Result.Success<>(newsList));
                    } else {
                        //本地无数据，查远程数据
                        return remoteDataSource.fetchData()
                                .thenCompose(remoteData -> {
                                    if (remoteData == null || remoteData.isEmpty()) {
                                        return CompletableFuture.completedFuture(new Result.Success<>(new ArrayList<>()));
                                    }
                                    Log.d(TAG, "远程数据获取完成，数据量: " + remoteData.size());

                                    List<News> newsList = remoteData.stream()
                                            .map(NewsMapper::dtoToModel)
                                            .collect(Collectors.toList());

                                    // 缓存远程数据到本地，再返回数据
                                    return localDataSource.saveData(
                                            newsList.stream()
                                                    .map(NewsMapper::modelToEntity)
                                                    .peek(entity -> entity.setTitle(entity.getTitle()))
                                                    .collect(Collectors.toList())
                                    ).thenApply(v -> {
                                        Log.d(TAG, "远程数据已保存到本地");

                                        // 缓存到内存
                                        memoryCache.put(CACHE_KEY, new ArrayList<>(newsList), MEMORY_CACHE_EXPIRE_TIME);
                                        return new Result.Success<>(newsList);
                                    });
                                });
                    }
                });
    }


    @Override
    public void close() throws Exception {
        localDataSource.close();
        remoteDataSource.close();
    }
}