package com.louis.lg_archj.di;

import android.content.Context;

import com.louis.lg_archj.data.local.DefaultNewsLocalDataSource;
import com.louis.lg_archj.data.local.NewsLocalDataSource;
import com.louis.lg_archj.data.remote.DefaultNewsRemoteDataSource;
import com.louis.lg_archj.data.remote.NewsRemoteDataSource;
import com.louis.lg_archj.data.repository.DefaultNewsRepository;
import com.louis.lg_archj.domain.repository.NewsRepository;
import com.louis.lg_archj.domain.usecase.LoadNewsUseCase;

public class AppContainer {
    private static AppContainer instance;

    private NewsLocalDataSource localDataSource;
    private NewsRemoteDataSource remoteDataSource;
    private NewsRepository newsRepository;
    private LoadNewsUseCase loadNewsUseCase;

    private AppContainer() {
    }

    public static AppContainer getInstance() {
        if (instance == null) {
            instance = new AppContainer();
        }
        return instance;
    }

    public void initialize(Context context) {
        localDataSource = new DefaultNewsLocalDataSource(context);
        remoteDataSource = new DefaultNewsRemoteDataSource(NetworkConfig.provideWanAndroidApi());
        newsRepository = new DefaultNewsRepository(localDataSource, remoteDataSource);
        loadNewsUseCase = new LoadNewsUseCase(newsRepository);
    }

    public LoadNewsUseCase getLoadNewsUseCase() {
        return loadNewsUseCase;
    }

    public void cleanup() throws Exception {
        if (loadNewsUseCase != null) {
            loadNewsUseCase.close();
        }
    }
}