package com.louis.lg_archj.ui.news;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.Executor;


import com.louis.lg_archj.MainThreadExecutor;
import com.louis.lg_archj.data.local.NewsLocalDataSource;
import com.louis.lg_archj.data.local.DefaultNewsLocalDataSource;
import com.louis.lg_archj.data.remote.NewsRemoteDataSource;
import com.louis.lg_archj.data.remote.DefaultNewsRemoteDataSource;
import com.louis.lg_archj.data.repository.NewsRepository;
import com.louis.lg_archj.domain.usecase.LoadNewsUseCase;

public class NewsViewModel extends ViewModel {
    private static final String TAG = "NewsViewModel";
    private final LoadNewsUseCase loadNewsUseCase;
    private final MutableLiveData<NewsUiState> _uiState = new MutableLiveData<>(NewsUiState.initial());
    public LiveData<NewsUiState> uiState = _uiState;

    //主线程执行器
    private final Executor mainThreadExecutor = new MainThreadExecutor();

    public NewsViewModel() {
        NewsLocalDataSource localDataSource = new DefaultNewsLocalDataSource();
        NewsRemoteDataSource remoteDataSource = new DefaultNewsRemoteDataSource();
        NewsRepository repository = new NewsRepository(localDataSource, remoteDataSource);

        this.loadNewsUseCase = new LoadNewsUseCase(repository);
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
                .whenCompleteAsync((data, throwable) -> {
                    Log.e(TAG, "invoke 数2据 data " + data.size());
                    if (throwable != null) {
                        _uiState.setValue(NewsUiState.loadError(throwable.getMessage()));
                        return;
                    }
                    _uiState.setValue(NewsUiState.loadSuccess(data));
                }, mainThreadExecutor); //指定主线程执行器
    }

    // 执行刷新数据业务
    private void handleRefreshData() {
        // 保持当前数据并设置刷新状态
//        NewsUiState currentState = _uiState.getValue();
//        if (currentState != null && currentState.data != null) {
            _uiState.setValue(NewsUiState.refreshing(null));
//        }
        loadNewsUseCase.invoke(null)
                // 异步回调：在主线程处理结果
                .whenCompleteAsync((data, throwable) -> {
                    Log.e(TAG, "invoke 数据 data " + data.size());
                    if (throwable != null) {
                        // 保持现有数据并仅更新错误和刷新状态
                        NewsUiState _currentState = _uiState.getValue();
                        if (_currentState != null && _currentState.data != null) {
                            _uiState.setValue(NewsUiState.refreshError(_currentState.data, throwable.getMessage()));
                        }
                        return;
                    }
                    _uiState.setValue(NewsUiState.refreshSuccess(data));
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