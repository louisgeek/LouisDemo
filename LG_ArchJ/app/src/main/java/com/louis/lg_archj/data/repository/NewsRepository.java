package com.louis.lg_archj.data.repository;

import com.louis.lg_archj.ResourceCloseable;
import com.louis.lg_archj.data.local.NewsLocalDataSource;
import com.louis.lg_archj.domain.model.News;
import com.louis.lg_archj.data.remote.NewsRemoteDataSource;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class NewsRepository implements ResourceCloseable {
    private final NewsLocalDataSource localDataSource;
    private final NewsRemoteDataSource remoteDataSource;

    public NewsRepository(NewsLocalDataSource localDataSource, NewsRemoteDataSource remoteDataSource) {
        this.localDataSource = localDataSource;
        this.remoteDataSource = remoteDataSource;
    }

    public CompletableFuture<List<News>> getData() {
        //
        return localDataSource.loadData()
                .thenCompose(localData -> {
                    if (!localData.isEmpty()) {
                        //本地有数据：直接返回
                        return CompletableFuture.completedFuture(localData);
                    } else {
                        //本地无数据，查远程数据
                        return remoteDataSource.fetchData()
                                .thenCompose(remoteData ->
                                        // 缓存远程数据到本地，再返回数据
                                        localDataSource.saveData(remoteData).thenApply(v -> remoteData)
                                );
                    }
                })
                .exceptionally(ex -> {
                    // 异常处理：若远程失败，尝试返回本地缓存（即使为空）
                    return localDataSource.loadData().join(); //join 等待任务完成并返回结果
                });

    }


    @Override
    public void close() throws Exception {
        localDataSource.close();
        remoteDataSource.close();
    }
}