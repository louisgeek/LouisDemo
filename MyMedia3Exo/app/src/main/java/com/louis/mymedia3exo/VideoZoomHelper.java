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

public class VideoZoomHelper {
    private PlayerView playerView;
    private Matrix matrix = new Matrix();
    private ScaleGestureDetector scaleGestureDetector;
    private static final float MIN_SCALE = 0.5f;
    private static final float MAX_SCALE = 5.0F;
    private float curScale = 1.0f;
    private static final float SLOW_SCALE_FACTOR = 0.3f; // 控制缩放速度的系数
    private GestureDetector dragGestureDetector;

    public VideoZoomHelper(PlayerView playerView) {
        this.playerView = playerView;
        Context context = playerView.getContext();

        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(@NonNull ScaleGestureDetector detector) {
                float scaleFactor = detector.getScaleFactor(); //缩放因子 >1 放大，<1 缩小
                float slowFactor = 1 + (scaleFactor - 1) * SLOW_SCALE_FACTOR;
                curScale *= slowFactor;
                curScale = Math.max(MIN_SCALE, Math.min(curScale, MAX_SCALE)); //修正
                matrix.setScale(curScale, curScale, detector.getFocusX(), detector.getFocusY());
                applyTransform();
                return true;
            }
        });

        dragGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @OptIn(markerClass = UnstableApi.class)
            @Override
            public boolean onDown(@NonNull MotionEvent e) {
//                View videoSurfaceView = playerView.getVideoSurfaceView();
//                if (videoSurfaceView == null) {
//                    return false;
//                }
//                if (videoSurfaceView instanceof TextureView) {
//                    TextureView textureView = (TextureView) videoSurfaceView;
//                    lastMatrix.set(textureView.getMatrix());
//                }
                return super.onDown(e);
            }

            @Override
            public boolean onScroll(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
                float curScale = getScaleXFromMatrix(matrix);
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
                dealMatrixValues(matrix);
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
                if (!scaleGestureDetector.isInProgress()) {
                    if (event.getPointerCount() == 1) {
                        dragGestureDetector.onTouchEvent(event);
                    }
                }
                return true;
            }
        });
    }

    @OptIn(markerClass = UnstableApi.class)
    private void dealMatrixValues(Matrix matrix) {
        View videoSurfaceView = playerView.getVideoSurfaceView();
        if (videoSurfaceView == null) {
            return;
        }
        float[] matrixValues = new float[9];
        matrix.getValues(matrixValues);
        float curScale = matrixValues[Matrix.MSCALE_X];
        float transX = matrixValues[Matrix.MTRANS_X];
        float transY = matrixValues[Matrix.MTRANS_Y];
        // 获取TextureView的实际尺寸
        float viewWidth = videoSurfaceView.getWidth(); //733
        float viewHeight = videoSurfaceView.getHeight(); //550
        float scaledWidth = viewWidth * curScale; //1466
        float scaledHeight = viewHeight * curScale; //1100
        Log.e("TAG", "dealMatrixValues: viewWidth=" + viewWidth + " viewHeight=" + viewHeight);
        Log.e("TAG", "dealMatrixValues: scaledWidth=" + scaledWidth + " scaledHeight=" + scaledHeight + " curScale=" + curScale);
        // 计算最大允许平移范围（基于缩放后的视频尺寸）
        float minTransX = -(scaledWidth - viewWidth) - 20; // 左边界
        float maxTransX = 0 + 20; // 右边界
//                float maxTranslateX = Math.max(0, scaledWidth - textureView.getWidth()) / 2;
//        translateX = Math.max(-maxTranslateX, Math.min(translateX, maxTranslateX));
        float maxTransY = 0 + 20; // 下边界
//        Log.e("TAG", "dealMatrixValues: minTransY=" + minTransY + " maxTransY=" + maxTransY);
        float minTransY = -(scaledHeight - viewHeight) - 20; // 上边界

        //限制平移在范围内
        transX = Math.max(minTransX, Math.min(transX, maxTransX)); //修正
        transY = Math.max(minTransY, Math.min(transY, maxTransY)); //修正
        Log.e("TAG", "dealMatrixValues: minTransX=" + minTransX + " maxTransX=" + maxTransX + " transX=" + transX);
        //更新矩阵内的数据
        matrixValues[Matrix.MTRANS_X] = transX;
        matrixValues[Matrix.MTRANS_Y] = transY;
        matrix.setValues(matrixValues);
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
        }
    }

    private void dealTranslation() {
//        //

        /// /        TextureView textureView = getVideoSurfaceView();
        /// /        if (textureView == null) {
        /// /            return;
        /// /        }
        /// /        if (videoSize == null) {
        /// /            return;
        /// /        }
//        float scaledWidth = textureView.getWidth() * curScale;
//        float scaledHeight = textureView.getHeight() * curScale;
//        //限制 x 方向平移
//        float maxTranslateX = Math.max(0, scaledWidth - textureView.getWidth()) / 2;
//        translateX = Math.max(-maxTranslateX, Math.min(translateX, maxTranslateX));
//
//        //限制 y 方向平移
//        float maxTranslateY = Math.max(0, scaledHeight - textureView.getHeight()) / 2;
//        translateY = Math.max(-maxTranslateY, Math.min(translateY, maxTranslateY));
    }

    private float getScaleXFromMatrix(Matrix matrix) {
        float[] matrixValues = new float[9];
        matrix.getValues(matrixValues);
        return matrixValues[Matrix.MSCALE_X];
    }


}
