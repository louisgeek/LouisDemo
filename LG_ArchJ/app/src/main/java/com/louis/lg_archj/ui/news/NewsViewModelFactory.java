package com.louis.lg_archj.ui.news;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.louis.lg_archj.domain.usecase.LoadNewsUseCase;

public class NewsViewModelFactory implements ViewModelProvider.Factory {
    private final LoadNewsUseCase loadNewsUseCase;

    public NewsViewModelFactory(LoadNewsUseCase loadNewsUseCase) {
        this.loadNewsUseCase = loadNewsUseCase;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(NewsViewModel.class)) {
            return (T) new NewsViewModel(loadNewsUseCase);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}