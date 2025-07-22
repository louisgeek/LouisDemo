package com.louis.mymedia3exo;

import android.content.Context;
import android.graphics.Matrix;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.TextureView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.ui.PlayerView;

public class VideoZoomManager {
    private static final String TAG = "VideoZoomManager";
    private PlayerView playerView;
    //
    private Matrix matrix = new Matrix();
    private ScaleGestureDetector scaleGestureDetector;
    private static final float MIN_SCALE = 0.5f;
    private static final float MAX_SCALE = 5.0F;
    private float curScale = 1.0f;
    private static final float SLOW_SCALE_FACTOR = 0.3f; // 控制缩放速度的系数
    private GestureDetector dragGestureDetector;

    public VideoZoomManager(PlayerView playerView) {
        this.playerView = playerView;
        Context context = playerView.getContext();

        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(@NonNull ScaleGestureDetector detector) {
                //当前距离/上一次距离
                float scaleFactor = detector.getScaleFactor(); //缩放因子 >1 放大，<1 缩小
                Log.e(TAG, "onScale: scaleFactor=" + scaleFactor);
                //
                curScale *= scaleFactor;
                curScale = Math.max(MIN_SCALE, Math.min(curScale, MAX_SCALE)); //修正
                Log.e(TAG, "onScale: curScale=" + curScale);
//                matrix.reset();
//                matrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
                matrix.postScale(curScale, curScale, detector.getFocusX(), detector.getFocusY());
                applyTransform();
                //返回 true 表示已处理事件，否则继续累积缩放因子
                return true;
            }
        });

        dragGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onScroll(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
                if (curScale <= MIN_SCALE) {
                    //未放大，不响应拖动
                    return false;
                }
//                mode = MODE_DRAG;
//
//                //distance 是上一次到当前的偏移
////                translateX -= distanceX;
////                translateY -= distanceY;
//
////                dealTranslation();
//                //
//                matrix.set(lastMatrix);
                matrix.postTranslate(-distanceX, -distanceY);
//                dealMatrixValues(matrix);
//
                applyTransform();
                return super.onScroll(e1, e2, distanceX, distanceY);
            }
        });

        //playerView.setOnTouchListener(null);
        playerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
//                } else if (event.getAction() == MotionEvent.ACTION_UP) {
//                    mode = MODE_NONE;
//                }
//                if (event.getPointerCount() >= 2) {
//                    mode = MODE_SCALE;
//                    scaleGestureDetector.onTouchEvent(event);
//                }
//                if (event.getPointerCount() == 1) {
//                    mode = MODE_DRAG;
//                    dragGestureDetector.onTouchEvent(event);
//                }
                scaleGestureDetector.onTouchEvent(event);
//                if (!scaleGestureDetector.isInProgress()) {
//                    if (event.getPointerCount() == 1) {
//                        dragGestureDetector.onTouchEvent(event);
//                    }
//                }
                return true;
            }
        });
    }



    @OptIn(markerClass = UnstableApi.class)
    private void applyTransform() {
        View videoSurfaceView = playerView.getVideoSurfaceView();
        if (videoSurfaceView == null) {
            return;
        }
        if (videoSurfaceView instanceof TextureView) {
            TextureView textureView = (TextureView) videoSurfaceView;
            textureView.setTransform(matrix);
            Log.e(TAG, "applyTransform: matrix=");
        }
    }



}
