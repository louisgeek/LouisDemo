package com.louis.lg_archj.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.louis.lg_archj.core.result.Result;
import com.louis.lg_archj.data.local.MemoryCache;
import com.louis.lg_archj.data.local.NewsLocalDataSource;
import com.louis.lg_archj.data.mapper.NewsMapper;
import com.louis.lg_archj.data.remote.NewsRemoteDataSource;
import com.louis.lg_archj.domain.model.News;
import com.louis.lg_archj.domain.repository.NewsRepository;

import java.util.List;

import java.util.stream.Collectors;

public class OfflineFirstNewsRepository implements NewsRepository {

    private static final String CACHE_KEY = "news_list";
    private static final long CACHE_EXPIRE_TIME = 10 * MemoryCache.TIME_UNIT_SECOND;

    private final NewsLocalDataSource localDataSource;
    private final NewsRemoteDataSource remoteDataSource;
    private final MemoryCache memoryCache;

    public OfflineFirstNewsRepository(NewsLocalDataSource localDataSource,
                                      NewsRemoteDataSource remoteDataSource) {
        this.localDataSource = localDataSource;
        this.remoteDataSource = remoteDataSource;
        this.memoryCache = MemoryCache.getInstance();
    }

    @Override
    @SuppressWarnings("unchecked")
    public LiveData<Result<List<News>>> getData() {
        MutableLiveData<Result<List<News>>> newsLiveData = new MutableLiveData<>();

        // 内存缓存检查
        List<News> cachedData = (List<News>) memoryCache.get(CACHE_KEY);
        if (cachedData != null) {
            newsLiveData.setValue(new Result.Success<>(cachedData));
            return newsLiveData;
        }

        // 查询本地数据库，添加异常处理
        localDataSource.queryData()
                .thenAccept(localData -> {
                    if (localData != null && !localData.isEmpty()) {
                        List<News> newsList = localData.stream()
                                .map(NewsMapper::entityToModel)
                                .collect(Collectors.toList());
                        memoryCache.put(CACHE_KEY, newsList, CACHE_EXPIRE_TIME);
                        newsLiveData.postValue(new Result.Success<>(newsList));
                        return;
                    }
                    //网络请求
                    fetchFromNetwork(newsLiveData);
                })
                .exceptionally(throwable -> {
                    //网络请求
                    fetchFromNetwork(newsLiveData);
                    return null;
                });

        return newsLiveData;
    }

    private void fetchFromNetwork(MutableLiveData<Result<List<News>>> newsLiveData) {
        remoteDataSource.fetchData()
                .thenAccept(remoteData -> {
                    if (remoteData == null || remoteData.isEmpty()) {
                        newsLiveData.postValue(new Result.Success<>(List.of()));
                        return;
                    }

                    List<News> newsList = remoteData.stream()
                            .map(NewsMapper::dtoToModel)
                            .collect(Collectors.toList());

                    // 先缓存和返回数据
                    memoryCache.put(CACHE_KEY, newsList, CACHE_EXPIRE_TIME);
                    newsLiveData.postValue(new Result.Success<>(newsList));

                    // 异步保存到数据库
                    localDataSource.saveData(
                            newsList.stream()
                                    .map(NewsMapper::modelToEntity)
                                    .collect(Collectors.toList())
                    );
                })
                .exceptionally(throwable -> {
                    newsLiveData.postValue(new Result.Error<>("网络请求失败: " + throwable.getMessage()));
                    return null;
                });
    }

    @Override
    public void close() throws Exception {
        try {
            localDataSource.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            remoteDataSource.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}