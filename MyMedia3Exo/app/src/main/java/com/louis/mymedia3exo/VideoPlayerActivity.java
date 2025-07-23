package com.louis.mymedia3exo;

import android.os.Bundle;
import android.graphics.Matrix;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

public class VideoPlayerActivity extends AppCompatActivity {
    private static final String STATE_MATRIX = "state_matrix";
    private static final String STATE_SCALE = "state_scale";

    private PlayerView playerView;
    private ExoPlayer player;
    private VideoGestureHandler2 gestureHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_player_port);

        // 初始化播放器视图
        playerView = findViewById(R.id.playerView);

        // 初始化手势处理器
        gestureHandler = new VideoGestureHandler2(playerView);
        playerView.setOnTouchListener((v, event) -> gestureHandler.onTouchEvent(event));

        // 初始化ExoPlayer
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        // 设置播放地址 - 替换为实际视频URL
        String videoUrl = "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4";
        MediaItem mediaItem = MediaItem.fromUri(videoUrl);
        player.setMediaItem(mediaItem);
        player.prepare();

        // 恢复状态（如屏幕旋转后）
        if (savedInstanceState != null) {
            float[] matrixValues = savedInstanceState.getFloatArray(STATE_MATRIX);
            float scale = savedInstanceState.getFloat(STATE_SCALE, 1.0f);

            if (matrixValues != null) {
                Matrix savedMatrix = new Matrix();
                savedMatrix.setValues(matrixValues);
                gestureHandler.restoreState(savedMatrix, scale);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (player != null) {
            player.play();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.pause();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // 保存缩放和平移状态
        Matrix currentMatrix = new Matrix();
        float[] scaleValue = new float[1];
        gestureHandler.saveState(currentMatrix, scaleValue);

        float[] matrixValues = new float[9];
        currentMatrix.getValues(matrixValues);
        outState.putFloatArray(STATE_MATRIX, matrixValues);
        outState.putFloat(STATE_SCALE, scaleValue[0]);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放播放器资源
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
    