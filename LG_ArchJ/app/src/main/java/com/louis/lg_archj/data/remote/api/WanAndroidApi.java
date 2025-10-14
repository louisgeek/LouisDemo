package com.louis.lg_archj.data.remote.api;

import com.louis.lg_archj.data.remote.dto.WanAndroidResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface WanAndroidApi {
    @GET("article/list/{page}/json")
    Call<WanAndroidResponse> getArticleList(@Path("page") int page);
}