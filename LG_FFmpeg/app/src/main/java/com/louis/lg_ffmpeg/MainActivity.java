package com.louis.lg_ffmpeg;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.louis.lg_ffmpeg.databinding.ActivityMainBinding;
import com.louisgeek.ffmpeg.FFmpegUtil;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Example of a call to a native method
        TextView tv = binding.sampleText;
        tv.setText("FFmpeg 版本：" + FFmpegUtil.getFFmpegVersion());
    }

}