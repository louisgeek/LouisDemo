package com.louis.lg_archj.data.repository;

import com.louis.lg_archj.core.result.Result;
import com.louis.lg_archj.data.local.MemoryCache;
import com.louis.lg_archj.data.local.NewsLocalDataSource;
import com.louis.lg_archj.data.mapper.NewsMapper;
import com.louis.lg_archj.data.remote.NewsRemoteDataSource;
import com.louis.lg_archj.domain.model.News;
import com.louis.lg_archj.domain.repository.NewsRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class OfflineFirstNewsRepository implements NewsRepository {
    private static final String CACHE_KEY = "news_data";
    private static final long CACHE_EXPIRE_TIME = 10 * MemoryCache.TIME_UNIT_SECOND;

    private final NewsLocalDataSource localDataSource;
    private final NewsRemoteDataSource remoteDataSource;
    private final MemoryCache memoryCache;

    public OfflineFirstNewsRepository(NewsLocalDataSource localDataSource,
                                      NewsRemoteDataSource remoteDataSource) {
        this.localDataSource = localDataSource;
        this.remoteDataSource = remoteDataSource;
        this.memoryCache = MemoryCache.getInstance();
    }

    @Override
    @SuppressWarnings("unchecked")
    public CompletableFuture<Result<List<News>>> getData() {
        // 1. 先检查内存缓存
        List<News> cachedData = (List<News>) memoryCache.get(CACHE_KEY);
        if (cachedData != null) {
            return CompletableFuture.completedFuture(new Result.Success<>(cachedData));
        }

        // 2. 检查本地数据库
        return localDataSource.queryData()
                .thenCompose(localData -> {
                    if (localData != null && !localData.isEmpty()) {
                        List<News> newsList = localData.stream()
                                .map(NewsMapper::entityToModel)
                                .collect(Collectors.toList());
                        // 缓存到内存
                        memoryCache.put(CACHE_KEY, newsList, CACHE_EXPIRE_TIME);
                        return CompletableFuture.completedFuture(new Result.Success<>(newsList));
                    }

                    // 3. 从网络获取数据
                    return fetchFromNetwork();
                });
    }

    private CompletableFuture<Result<List<News>>> fetchFromNetwork() {
        return remoteDataSource.fetchData()
                .thenCompose(remoteData -> {
                    if (remoteData == null || remoteData.isEmpty()) {
                        return CompletableFuture.completedFuture(new Result.Success<>(List.of()));
                    }

                    List<News> newsList = remoteData.stream()
                            .map(NewsMapper::dtoToModel)
                            .collect(Collectors.toList());

                    // 保存到本地数据库
                    return localDataSource.saveData(
                            newsList.stream()
                                    .map(NewsMapper::modelToEntity)
                                    .collect(Collectors.toList())
                    ).thenApply(v -> {
                        // 缓存到内存
                        memoryCache.put(CACHE_KEY, newsList, CACHE_EXPIRE_TIME);
                        return new Result.Success<>(newsList);
                    });
                });
    }

    @Override
    public void close() throws Exception {
        localDataSource.close();
        remoteDataSource.close();
    }
}