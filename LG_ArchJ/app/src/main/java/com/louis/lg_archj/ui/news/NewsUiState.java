package com.louis.lg_archj.ui.news;


import com.louis.lg_archj.domain.model.News;
import com.louis.lg_archj.ui.BaseUiState;

import java.util.Collections;
import java.util.List;

public class NewsUiState extends BaseUiState<List<News>> {
    protected NewsUiState(boolean loading, List<News> data, String error) {
        //禁止外部修改 list
        super(loading, Collections.unmodifiableList(data == null ? Collections.emptyList() : data), error);
    }

    // 初始状态
    public static NewsUiState initial() {
        return new NewsUiState(false, null, null);
    }

    public static NewsUiState loading() {
        return new NewsUiState(true, null, null);
    }

    public static NewsUiState success(List<News> data) {
        return new NewsUiState(false, data, null);
    }

    public static NewsUiState error(String error) {
        return new NewsUiState(false, null, error);
    }

}