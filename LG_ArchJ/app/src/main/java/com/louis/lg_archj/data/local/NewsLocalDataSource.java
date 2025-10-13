package com.louis.lg_archj.data.local;

import com.louis.lg_archj.ResourceCloseable;
import com.louis.lg_archj.data.local.entity.NewsEntity;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface NewsLocalDataSource extends ResourceCloseable {
    CompletableFuture<List<NewsEntity>> queryData();

    CompletableFuture<Void> saveData(List<NewsEntity> data);
}