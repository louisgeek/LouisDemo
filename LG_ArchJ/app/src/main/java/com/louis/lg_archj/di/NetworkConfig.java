package com.louis.lg_archj.di;

import com.louis.lg_archj.data.remote.api.WanAndroidApi;

import okhttp3.OkHttpClient;
//import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class NetworkConfig {
    private static final String BASE_URL = "https://wanandroid.com/";
    private static final int TIMEOUT_SECONDS = 30;

    public static OkHttpClient provideOkHttpClient() {
//        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
//                .addInterceptor(logging)
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build();
    }

    private static Retrofit retrofit;
    private static WanAndroidApi wanAndroidApi;

    public static Retrofit provideRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(provideOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static WanAndroidApi provideWanAndroidApi() {
        if (wanAndroidApi == null) {
            wanAndroidApi = provideRetrofit().create(WanAndroidApi.class);
        }
        return wanAndroidApi;
    }
}