package com.louis.lg_archj.domain.repository;

import com.louis.lg_archj.ResourceCloseable;
import com.louis.lg_archj.core.result.Result;
import com.louis.lg_archj.domain.model.News;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface NewsRepository extends ResourceCloseable {
    CompletableFuture<Result<List<News>>> getData();
}