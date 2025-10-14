package com.louis.lg_archj.data.remote;

import android.util.Log;

import com.louis.lg_archj.data.remote.dto.NewsDto;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DefaultNewsRemoteDataSource implements NewsRemoteDataSource {
    private static final String TAG = "DefaultNewsRemoteDataSource";
    private final ExecutorService networkExecutor = Executors.newFixedThreadPool(3);

    @Override
    public CompletableFuture<List<NewsDto>> fetchData() {
        return CompletableFuture.supplyAsync(() -> {
            Log.d(TAG, "请求远程数据，线程: " + Thread.currentThread().getName());
            try {
                // 模拟网络延迟 - 随机3-5秒
                int delay = 3000 + (int) (Math.random() * 2001); // 3000-5000
                Thread.sleep(delay);

                // 模拟远程接口返回数据
                List<NewsDto> remoteNews = new ArrayList<>();
                int count = 5 + (int) (Math.random() * 16); // 5-20
                Log.d(TAG, "请求远程数据，count: " + count);
                for (int i = 1; i <= count; i++) {
                    int randomId = 1000 + (int) (Math.random() * 4001);
                    remoteNews.add(new NewsDto(String.valueOf(randomId), "News " + i + "（远程）" + randomId));
                }
                Log.d(TAG, "请求远程数据完成，数据量: " + remoteNews.size());
                return remoteNews;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("远程请求中断");
            }
        }, networkExecutor);
    }

    @Override
    public void close() throws Exception {
        Log.d(TAG, "关闭远程数据源");
        networkExecutor.shutdown();
    }
}