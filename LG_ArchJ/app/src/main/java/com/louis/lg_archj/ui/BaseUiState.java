package com.louis.lg_archj.ui;


import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class BaseUiState<T> {
    //public static final int IDLE = 0;
    public static final int INITIAL = 0;
    public static final int LOADING = 1; //代表加载数据（通常由页面加载触发，可能是缓存优先）
    public static final int LOAD_SUCCESS = 2;
    public static final int LOAD_ERROR = 3;
    public static final int REFRESHING = 4; //代表刷新数据（通常由下拉刷新或者点击刷新按钮触发，可能是强制从网络获取，通常不去隐藏已有内容，并且刷新后可能需要恢复当前滚动位置）
    public static final int REFRESH_SUCCESS = 5;
    public static final int REFRESH_ERROR = 6;

    @IntDef({INITIAL, LOADING, LOAD_SUCCESS, LOAD_ERROR, REFRESHING, REFRESH_SUCCESS, REFRESH_ERROR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface UiState {
    }

    @UiState
    public final int uiState;
    public final T data;
    public final String error;

    protected BaseUiState(@UiState int uiState, T data, String error) {
        this.uiState = uiState;
        this.data = data;
        this.error = error;
    }
    
    public boolean isInitial() {
        return uiState == INITIAL;
    }

    public boolean isLoading() {
        return uiState == LOADING;
    }

    public boolean isLoadSuccess() {
        return uiState == LOAD_SUCCESS;
    }

    public boolean isLoadError() {
        return uiState == LOAD_ERROR;
    }

    public boolean isRefreshing() {
        return uiState == REFRESHING;
    }

    public boolean isRefreshSuccess() {
        return uiState == REFRESH_SUCCESS;
    }

    public boolean isRefreshError() {
        return uiState == REFRESH_ERROR;
    }
}