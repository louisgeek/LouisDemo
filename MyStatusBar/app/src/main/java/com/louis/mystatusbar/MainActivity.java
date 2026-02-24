package com.louis.mystatusbar;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowInsetsController;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.internal.EdgeToEdgeUtils;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //内部处理 WindowCompat.setDecorFitsSystemWindows(window, false); 用于替代 fitsSystemWindows=false、设置 statusBarColor 和 navigationBarColor、处理 LightStatusBars 和 LightNavigationBars
        EdgeToEdge.enable(this);
        EdgeToEdgeHelper.setAppearanceLightBars(getWindow(), true);
        setContentView(R.layout.activity_main);
        //配合 EdgeToEdge#enable 使用
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            //获取 systemBars 包含状态栏、导航栏的高度/宽度
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int statusBarHeight = systemBars.top;
            int navigationBarHeight = systemBars.bottom;
            Log.d(TAG, "状态栏高度: " + statusBarHeight + " 导航栏高度: " + navigationBarHeight);
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


}