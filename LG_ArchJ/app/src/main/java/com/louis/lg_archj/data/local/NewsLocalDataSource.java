package com.louis.lg_archj.data.local;

import com.louis.lg_archj.ResourceCloseable;
import com.louis.lg_archj.domain.model.News;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface NewsLocalDataSource extends ResourceCloseable {
    CompletableFuture<List<News>> loadData();

    CompletableFuture<Void> saveData(List<News> news);
}