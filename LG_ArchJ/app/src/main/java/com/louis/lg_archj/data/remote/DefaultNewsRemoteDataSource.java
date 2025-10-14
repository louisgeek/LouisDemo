package com.louis.lg_archj.data.remote;

import android.util.Log;

import com.louis.lg_archj.data.remote.api.WanAndroidApi;
import com.louis.lg_archj.data.remote.dto.NewsDto;
import com.louis.lg_archj.data.remote.dto.WanAndroidResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DefaultNewsRemoteDataSource implements NewsRemoteDataSource {
    private static final String TAG = "DefaultNewsRemoteDataSource";
    private static final String BASE_URL = "https://wanandroid.com/";
    private final ExecutorService networkExecutor = Executors.newFixedThreadPool(3);
    private final WanAndroidApi api;

    public DefaultNewsRemoteDataSource() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(WanAndroidApi.class);
    }

    @Override
    public CompletableFuture<List<NewsDto>> fetchData() {
        return CompletableFuture.supplyAsync(() -> {
            Log.d(TAG, "请求远程数据，线程: " + Thread.currentThread().getName());

            try {
                int pageIndex = 5 + (int) (Math.random() * 16); // 5-20
                Response<WanAndroidResponse> response = api.getArticleList(pageIndex).execute();

                if (!response.isSuccessful()) {
                    throw new RuntimeException("网络请求失败: " + response.code());
                }

                WanAndroidResponse apiResponse = response.body();
                if (apiResponse == null || apiResponse.getErrorCode() != 0) {
                    throw new RuntimeException("API错误: " + (apiResponse != null ? apiResponse.getErrorMsg() : "响应为空"));
                }

                List<NewsDto> newsList = new ArrayList<>();
                if (apiResponse.getData() != null && apiResponse.getData().getDatas() != null) {
                    for (WanAndroidResponse.WanAndroidData data : apiResponse.getData().getDatas()) {
                        newsList.add(new NewsDto(String.valueOf(data.getId()), data.getTitle()));
                    }
                }

                Log.d(TAG, "请求远程数据完成，数据量: " + newsList.size());
                return newsList;
            } catch (IOException e) {
                Log.e(TAG, "网络请求异常", e);
                throw new RuntimeException("网络请求失败: " + e.getMessage());
            }
        }, networkExecutor);
    }

    @Override
    public void close() throws Exception {
        Log.d(TAG, "关闭远程数据源");
        networkExecutor.shutdown();
    }
}