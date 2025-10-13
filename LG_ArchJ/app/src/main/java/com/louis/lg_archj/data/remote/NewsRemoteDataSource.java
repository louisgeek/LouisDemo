package com.louis.lg_archj.data.remote;


import com.louis.lg_archj.ResourceCloseable;
import com.louis.lg_archj.data.remote.dto.NewsDto;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface NewsRemoteDataSource extends ResourceCloseable {
    //CompletableFuture<List<NewsDto>> getData();
    CompletableFuture<List<NewsDto>> fetchData();
}