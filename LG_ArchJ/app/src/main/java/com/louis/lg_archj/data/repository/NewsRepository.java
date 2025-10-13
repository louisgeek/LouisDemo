package com.louis.lg_archj.data.repository;

import android.util.Log;

import com.louis.lg_archj.ResourceCloseable;
import com.louis.lg_archj.data.local.NewsLocalDataSource;
import com.louis.lg_archj.data.mapper.NewsMapper;
import com.louis.lg_archj.domain.model.News;
import com.louis.lg_archj.data.remote.NewsRemoteDataSource;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class NewsRepository implements ResourceCloseable {
    private static final String TAG = "NewsRepository";
    private final NewsLocalDataSource localDataSource;
    private final NewsRemoteDataSource remoteDataSource;

    public NewsRepository(NewsLocalDataSource localDataSource, NewsRemoteDataSource remoteDataSource) {
        this.localDataSource = localDataSource;
        this.remoteDataSource = remoteDataSource;
    }

    public CompletableFuture<List<News>> getData() {
        return localDataSource.queryData()
                .thenCompose(localData -> {
                    if (!localData.isEmpty()) {
                        //本地有数据：直接返回
                        return CompletableFuture.completedFuture(
                                localData.stream()
                                        .map(NewsMapper::entityToModel)
                                        .collect(Collectors.toList())
                        ).whenComplete((data, throwable) -> {
                            if (throwable != null) {
                                Log.e(TAG, "处理本地数据时发生错误", throwable);
                            } else {
                                Log.d(TAG, "本地数据处理完成，数据量: " + data.size());
                            }
                        });
                    } else {
                        //本地无数据，查远程数据
                        return remoteDataSource.fetchData()
                                .thenCompose(remoteData -> {
                                    Log.d(TAG, "远程数据获取完成，数据量: " + (remoteData != null ? remoteData.size() : 0));
                                    // 缓存远程数据到本地，再返回数据
                                    return localDataSource.saveData(
                                            remoteData.stream()
                                                    .map(NewsMapper::dtoToEntity)
                                                    .collect(Collectors.toList())
                                    ).thenApply(v -> {
                                        Log.d(TAG, "远程数据已保存到本地");
                                        return remoteData.stream()
                                                .map(NewsMapper::dtoToModel)
                                                .collect(Collectors.toList());
                                    });
                                }).whenComplete((data, throwable) -> {
                                    if (throwable != null) {
                                        Log.e(TAG, "处理远程数据时发生错误", throwable);
                                    } else {
                                        Log.d(TAG, "远程数据处理完成，数据量: " + data.size());
                                    }
                                });
                    }
                }).whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        Log.e(TAG, "获取数据过程中发生错误", throwable);
                    } else {
                        Log.d(TAG, "数据获取流程完成");
                    }
                });
    }


    @Override
    public void close() throws Exception {
        localDataSource.close();
        remoteDataSource.close();
    }
}