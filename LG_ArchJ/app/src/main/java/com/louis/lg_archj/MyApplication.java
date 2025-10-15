package com.louis.lg_archj;

import android.app.Application;

import com.louis.lg_archj.di.AppContainer;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化依赖注入容器
        AppContainer.getInstance().initialize(this);
    }

    @Override
    public void onTerminate() {
        try {
            AppContainer.getInstance().cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onTerminate();
    }
}