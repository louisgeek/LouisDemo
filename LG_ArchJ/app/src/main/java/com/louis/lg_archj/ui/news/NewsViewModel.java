package com.louis.lg_archj.ui.news;

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
//            handleRefreshData();
        } else if (intent instanceof NewsUiIntent.SearchData) {
//            handleSearchData(((UiIntent.SearchData) intent).query);
        }
    }

    // 执行加载用户业务
    private void handleLoadData() {
        _uiState.setValue(NewsUiState.loading());

        loadNewsUseCase.execute(null)
                // 异步回调：在主线程处理结果
                .whenCompleteAsync((data, throwable) -> {
                    if (throwable != null) {
                        _uiState.setValue(NewsUiState.error(throwable.getMessage()));
                        return;
                    }
                    _uiState.setValue(NewsUiState.success(data));
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