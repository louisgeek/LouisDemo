package com.louis.lg_archj.data.repository;

import android.util.Log;

import com.louis.lg_archj.ResourceCloseable;
import com.louis.lg_archj.data.local.NewsLocalDataSource;
import com.louis.lg_archj.data.mapper.NewsMapper;
import com.louis.lg_archj.domain.model.News;
import com.louis.lg_archj.data.remote.NewsRemoteDataSource;

import java.util.ArrayList;
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
                    if (localData != null && !localData.isEmpty()) {
                        Log.e(TAG, "本地有数据：直接返回" + localData);
                        return CompletableFuture.completedFuture(
                                localData.stream()
                                        .map(NewsMapper::entityToModel)
                                        .collect(Collectors.toList())
                        );
                    } else {
                        //本地无数据，查远程数据
                        return remoteDataSource.fetchData()
                                .thenCompose(remoteData -> {
                                    if (remoteData == null || remoteData.isEmpty()) {
                                        return CompletableFuture.completedFuture(new ArrayList<>());
//                                        return CompletableFuture.completedFuture(Collections.<List<News>>emptyList());
//                                        return CompletableFuture.completedFuture(List.<List<News>>of());
                                    }
                                    Log.d(TAG, "远程数据获取完成，数据量: " + remoteData.size());
                                    // 缓存远程数据到本地，再返回数据
                                    return localDataSource.saveData(
                                            remoteData.stream()
                                                    .map(NewsMapper::dtoToEntity)
                                                    .peek(entity -> entity.setTitle(entity.getTitle().replace("（远程）", "") + "（本地）"))
                                                    .collect(Collectors.toList())
                                    ).thenApply(v -> {
                                        Log.d(TAG, "远程数据已保存到本地");
                                        return remoteData.stream()
                                                .map(NewsMapper::dtoToModel)
                                                .collect(Collectors.toList());
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