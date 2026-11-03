package com.louis.mylogs;

import android.os.Bundle;
import android.widget.CheckBox;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatCheckBox$InspectionCompanion;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.tencent.mars.xlog.Log;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        new AppCompatCheckBox$InspectionCompanion( this).setChecked(true, false)

        LogTool.v(TAG, "测试 Activity vvv");
        LogTool.v( "测试 Activity vvv");
        LogTool.d(TAG, "测试 Activity debug");
        LogTool.d( "测试 Activity debug");
        LogTool.i(TAG, "测试 Activity info");
        LogTool.i( "测试 Activity info");
        LogTool.w(TAG, "测试 Activity warn");
        LogTool.w( "测试 Activity warn");


        Thread tt = new Thread(new Runnable() {

            @Override
            public void run() {
                LogTool.e(TAG, "测试 Activity error");
                LogTool.e( "测试 Activity error");
            }
        });
        tt.setName("tt");
        tt.start();


        LogTool.v(TAG, "测试 App vvvv");
        LogTool.v( "测试 App vvvv");
        LogTool.d(TAG, "测试 App debug");
        LogTool.d( "测试 App debug");
        LogTool.i(TAG, "测试 App info");
        LogTool.i( "测试 App info");
        LogTool.w(TAG, "测试 App warn");
        LogTool.w( "测试 App warn");

        Thread tt2 = new Thread(new Runnable() {

            @Override
            public void run() {
                LogTool.e(TAG, "测试 App error");
                LogTool.e("测试 App error");
            }
        });
        tt2.setName("tt2");
        tt2.start();
    }
}