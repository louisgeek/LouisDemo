package com.louis.lg_archj.ui.news;

import android.util.Log;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.Executor;


import com.louis.lg_archj.MainThreadExecutor;
import com.louis.lg_archj.core.result.Result;
import com.louis.lg_archj.domain.model.News;
import com.louis.lg_archj.domain.usecase.LoadNewsUseCase;

import java.util.List;

public class NewsViewModel extends ViewModel {
    private static final String TAG = "NewsViewModel";
    private final LoadNewsUseCase loadNewsUseCase;
    private final MutableLiveData<NewsUiState> _uiState = new MutableLiveData<>(NewsUiState.initial());
    public LiveData<NewsUiState> uiState = _uiState;

    //主线程执行器
    private final Executor mainThreadExecutor = new MainThreadExecutor();

    public NewsViewModel(LoadNewsUseCase loadNewsUseCase) {
        this.loadNewsUseCase = loadNewsUseCase;
    }

    //处理用户意图
    public void processIntent(NewsUiIntent intent) {
        if (intent instanceof NewsUiIntent.LoadData) {
            handleLoadData();
        } else if (intent instanceof NewsUiIntent.RefreshData) {
            handleRefreshData();
        } else if (intent instanceof NewsUiIntent.SearchData) {
//            handleSearchData(((UiIntent.SearchData) intent).query);
        }
    }

    // 执行加载用户业务
    private void handleLoadData() {
        _uiState.setValue(NewsUiState.loading());

        loadNewsUseCase.invoke(null)
                // 异步回调：在主线程处理结果
                .whenCompleteAsync((result, throwable) -> {
                    if (throwable != null) {
                        _uiState.setValue(NewsUiState.loadError("加载失败，请稍后重试"));
                        return;
                    }
                    if (result.isSuccess()) {
                        Result.Success<List<News>> success = (Result.Success<List<News>>) result;
                        _uiState.setValue(NewsUiState.loadSuccess(success.getData()));
                    } else {
                        Result.Error<List<News>> error = (Result.Error<List<News>>) result;
                        _uiState.setValue(NewsUiState.loadError(error.getMessage()));
                    }
                }, mainThreadExecutor); //指定主线程执行器
    }

    // 执行刷新数据业务
    private void handleRefreshData() {
        // 保持当前数据并设置刷新状态
        NewsUiState currentState = _uiState.getValue();
        if (currentState != null && currentState.data != null) {
            _uiState.setValue(NewsUiState.refreshing(currentState.data));
        }
        loadNewsUseCase.invoke(null)
                // 异步回调：在主线程处理结果
                .whenCompleteAsync((result, throwable) -> {
                    if (throwable != null) {
                        NewsUiState _currentState = _uiState.getValue();
                        if (_currentState != null && _currentState.data != null) {
                            _uiState.setValue(NewsUiState.refreshError(_currentState.data, "刷新失败，请稍后重试"));
                        }
                        return;
                    }

                    NewsUiState _currentState = _uiState.getValue();
                    List<News> currentData = _currentState != null ? _currentState.data : null;
                    if (result.isSuccess()) {
                        Result.Success<List<News>> success = (Result.Success<List<News>>) result;
                        _uiState.setValue(NewsUiState.refreshSuccess(success.getData()));
                    } else {
                        Result.Error<List<News>> error = (Result.Error<List<News>>) result;
                        _uiState.setValue(NewsUiState.refreshError(currentData, error.getMessage()));
                    }
                }, mainThreadExecutor); //指定主线程执行器
    }

    @Override
    protected void onCleared() {
        try {
            loadNewsUseCase.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        super.onCleared();
    }
}