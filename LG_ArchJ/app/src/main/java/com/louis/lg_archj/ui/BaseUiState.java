package com.louis.lg_archj.ui;

public abstract class BaseUiState<T> {
    public final boolean loading;
    public final T data;
    public final String error;

    protected BaseUiState(boolean loading, T data, String error) {
        this.loading = loading;
        this.data = data;
        this.error = error;
    }
}

