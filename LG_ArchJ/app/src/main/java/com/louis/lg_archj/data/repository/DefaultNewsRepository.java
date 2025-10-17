package com.louis.lg_archj.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.louis.lg_archj.core.result.Result;
import com.louis.lg_archj.data.local.MemoryCache;
import com.louis.lg_archj.data.local.NewsLocalDataSource;
import com.louis.lg_archj.data.mapper.NewsMapper;
import com.louis.lg_archj.domain.model.News;
import com.louis.lg_archj.domain.repository.NewsRepository;
import com.louis.lg_archj.data.remote.NewsRemoteDataSource;

import java.util.ArrayList;
import java.util.List;

import java.util.stream.Collectors;

public class DefaultNewsRepository implements NewsRepository {
    private static final String TAG = "DefaultNewsRepository";
    private static final String CACHE_KEY = "news_list";
    private static final long CACHE_EXPIRE_TIME = 10 * MemoryCache.TIME_UNIT_SECOND;

    private final NewsLocalDataSource localDataSource;
    private final NewsRemoteDataSource remoteDataSource;
    private final MemoryCache memoryCache;

    public DefaultNewsRepository(NewsLocalDataSource localDataSource, NewsRemoteDataSource remoteDataSource) {
        this.localDataSource = localDataSource;
        this.remoteDataSource = remoteDataSource;
        this.memoryCache = MemoryCache.getInstance();
    }

    @Override
    @SuppressWarnings("unchecked")
    public LiveData<Result<List<News>>> getData() {
        MutableLiveData<Result<List<News>>> mutableLiveData = new MutableLiveData<>();
        //内存缓存
        List<News> cachedData = (List<News>) memoryCache.get(CACHE_KEY);
        if (cachedData != null) {
            Log.d(TAG, "从内存缓存获取数据，数据量: " + cachedData.size());
            mutableLiveData.setValue(new Result.Success<>(cachedData));
            //返回数据的同时继续网络请求
            fetchFromNetwork(mutableLiveData);
            return mutableLiveData;
        }
        //查数据库
        localDataSource.queryData()
                .thenAccept(localData -> {
                    if (localData != null && !localData.isEmpty()) {
                        List<News> newsList = localData.stream()
                                .map(NewsMapper::entityToModel)
                                .collect(Collectors.toList());
                        Log.d(TAG, "从数据库获取数据，数据量: " + newsList.size());
                        memoryCache.put(CACHE_KEY, newsList, CACHE_EXPIRE_TIME);
                        mutableLiveData.postValue(new Result.Success<>(newsList));
                        //返回数据的同时继续网络请求
                        fetchFromNetwork(mutableLiveData);
                    } else {
                        //本地数据为空，从网络获取
                        fetchFromNetwork(mutableLiveData);
                    }
                })
                .exceptionally(throwable -> {
                    //数据库查询失败，尝试网络请求
                    fetchFromNetwork(mutableLiveData);
                    return null;
                });
        return mutableLiveData;
    }

    private void fetchFromNetwork(MutableLiveData<Result<List<News>>> mutableLiveData) {
        remoteDataSource.fetchData()
                .thenAccept(remoteData -> {
                    if (remoteData == null || remoteData.isEmpty()) {
                        mutableLiveData.postValue(new Result.Success<>(List.of()));
                        return;
                    }
                    List<News> newsList = remoteData.stream()
                            .map(NewsMapper::dtoToModel)
                            .collect(Collectors.toList());
                    // 异步保存到数据库
                    localDataSource.saveData(newsList.stream()
                            .map(NewsMapper::modelToEntity)
                            .collect(Collectors.toList()));

                    Log.d(TAG, "网络数据获取成功，数据量: " + newsList.size());
                    //存到内存缓存
                    memoryCache.put(CACHE_KEY, new ArrayList<>(newsList), CACHE_EXPIRE_TIME);
                    mutableLiveData.postValue(new Result.Success<>(newsList));

                })
                .exceptionally(throwable -> {
                    Log.e(TAG, "网络请求失败", throwable);
                    mutableLiveData.postValue(new Result.Error<>("网络请求失败: " + throwable.getMessage()));
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