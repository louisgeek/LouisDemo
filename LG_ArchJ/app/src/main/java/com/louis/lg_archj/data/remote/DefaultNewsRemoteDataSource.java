package com.louis.lg_archj.data.remote;

import com.louis.lg_archj.data.remote.dto.NewsDto;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DefaultNewsRemoteDataSource implements NewsRemoteDataSource {
    private final ExecutorService networkExecutor = Executors.newFixedThreadPool(3);

    @Override
    public CompletableFuture<List<NewsDto>> fetchData() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 模拟网络延迟（1秒）
                Thread.sleep(1000);
                // 模拟远程接口返回数据
                List<NewsDto> remoteNews = new ArrayList<>();
                remoteNews.add(new NewsDto("1", "Alice（远程）"));
                remoteNews.add(new NewsDto("2", "Bob（远程）"));
                remoteNews.add(new NewsDto("3", "Charlie（远程）"));
                return remoteNews;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("远程请求中断");
            }
        }, networkExecutor);
    }

    @Override
    public void close() throws Exception {
        networkExecutor.shutdown();
    }
}