package com.louis.lg_archj.domain.usecase;

import com.louis.lg_archj.ResourceCloseable;
import com.louis.lg_archj.data.repository.NewsRepository;
import com.louis.lg_archj.domain.model.News;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class LoadNewsUseCase implements BaseUseCase<Void, CompletableFuture<List<News>>> {
    private final NewsRepository repository;

    public LoadNewsUseCase(NewsRepository repository) {
        this.repository = repository;
    }

    @Override
    public CompletableFuture<List<News>> invoke(Void params) {
        //UseCase 聚焦业务
        return repository.getData()
                .thenApply(list -> {
                    // 最终返回前可统一处理数据（如过滤）
                    return list.stream()
                            .filter(user -> user.getTitle().contains("远程") || user.getTitle().contains("本地"))
                            .collect(Collectors.toList());
                });
    }

    @Override
    public void close() throws Exception {
        repository.close();
    }


}