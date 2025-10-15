package com.louis.lg_archj.domain.usecase;

import com.louis.lg_archj.core.result.Result;
import com.louis.lg_archj.domain.model.News;
import com.louis.lg_archj.domain.repository.NewsRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class LoadNewsUseCase implements BaseUseCase<Void, CompletableFuture<Result<List<News>>>> {
    private final NewsRepository repository;

    public LoadNewsUseCase(NewsRepository repository) {
        this.repository = repository;
    }

    @Override
    public CompletableFuture<Result<List<News>>> invoke(Void params) {
        //UseCase 聚焦业务
        return repository.getData()
                .thenApply(result -> {
                    if (result.isSuccess()) {
                        Result.Success<List<News>> success = (Result.Success<List<News>>) result;
                        List<News> filteredList = success.getData().stream()
//                                .filter(news -> news.getTitle().contains("远程") || news.getTitle().contains("本地"))
                                .collect(Collectors.toList());
                        return new Result.Success<>(filteredList);
                    }
                    return result; // 错误继续传递
                });
    }

    @Override
    public void close() throws Exception {
        repository.close();
    }


}