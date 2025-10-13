package com.louis.lg_archj.ui.news;

import com.louis.lg_archj.domain.model.News;
import com.louis.lg_archj.ui.BaseUiState;

import java.util.Collections;
import java.util.List;

public class NewsUiState extends BaseUiState<List<News>> {

    protected NewsUiState(@UiState int uiState, List<News> data, String error) {
        //禁止外部修改 list
        super(uiState, Collections.unmodifiableList(data == null ? Collections.emptyList() : data), error);
    }

    // 初始状态
    public static NewsUiState initial() {
        return new NewsUiState(INITIAL, null, null);
    }

    public static NewsUiState loading() {
        return new NewsUiState(LOADING, null, null);
    }

    public static NewsUiState loadSuccess(List<News> data) {
        return new NewsUiState(LOAD_SUCCESS, data, null);
    }

    public static NewsUiState loadError(String error) {
        return new NewsUiState(LOAD_ERROR, null, error);
    }

    public static NewsUiState refreshing(List<News> currentData) {
        return new NewsUiState(REFRESHING, currentData, null);
    }

    public static NewsUiState refreshSuccess(List<News> data) {
        return new NewsUiState(REFRESH_SUCCESS, data, null);
    }

    public static NewsUiState refreshError(List<News> currentData, String error) {
        return new NewsUiState(REFRESH_ERROR, currentData, error);
    }
}