package com.louis.mymedia3exo;


import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.TextureView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.ui.PlayerView;


public class PlayViewManager3 {
    private static final String TAG = "PlayViewManager";
    private PlayerView playerView;
    //
    private Matrix matrix = new Matrix();
    //缩放
    private ScaleGestureDetector scaleGestureDetector;
    private static final float MIN_SCALE = 1.0f;
    private static final float MAX_SCALE = 10.0f;
    private float curScale;
    private PointF curFocusPoint;
    //拖动
    private GestureDetector dragGestureDetector;
    private float curTransX;
    private float curTransY;

    public PlayViewManager3(PlayerView playerView, float scale, PointF focusPoint, OnZoomListener onZoomListener) {
        this.playerView = playerView;
        curScale = scale;
        curFocusPoint = focusPoint;
        this.onZoomListener = onZoomListener;
        //
        Context context = playerView.getContext();

        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(@NonNull ScaleGestureDetector detector) {
                float scaleFactor = detector.getScaleFactor(); //缩放因子大于 1 放大，小于 1 缩小
                Log.i(TAG, "tempLog scaleFactor=" + scaleFactor); //1.02~0.98
                float newScale = curScale * scaleFactor; //累积 1.0~10.0
                newScale = Math.max(MIN_SCALE, Math.min(newScale, MAX_SCALE)); //修正
                float realFactor = newScale / curScale; //除回去，相当于修正后的 scaleFactor
                matrix.postScale(realFactor, realFactor, curFocusPoint.x, curFocusPoint.y);
//                matrix.postScale(realFactor, realFactor, detector.getFocusX(), detector.getFocusY());
                curScale = newScale; //记录
                curFocusPoint.set(detector.getFocusX(), detector.getFocusY());
                if (onZoomListener != null) {
                    onZoomListener.onZoom(curScale, curFocusPoint);
                }
                applyTransform();
                return true;
            }
        });

        dragGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onScroll(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
//                if (curScale <= MIN_SCALE) {
//                    //未放大，不响应拖动
//                    return false;
//                }
                //distanceX 横向滑动距离，负值表示右滑 distanceY 纵向滑动距离，负值表示下滑
                float dx = -distanceX; //distanceX 右滑 -4~-2   左滑 2~4
                float dy = -distanceY;

                float viewWidth = playerView.getWidth();
                float viewHeight = playerView.getHeight();

                View videoSurfaceView = getVideoSurfaceView();
                if (videoSurfaceView == null) {
                    return false;
                }
                float textureViewWidth = videoSurfaceView.getWidth();
                float textureViewHeight = videoSurfaceView.getHeight();
                Log.e(TAG, "onScroll: viewWidth=" + viewWidth);
                Log.e(TAG, "onScroll: viewHeight=" + viewHeight);
                Log.e(TAG, "onScroll: textureViewWidth=" + textureViewWidth);
                Log.e(TAG, "onScroll: textureViewHeight=" + textureViewHeight);

                RectF rect = new RectF(0f, 0f, textureViewWidth, textureViewHeight);
                matrix.mapRect(rect);


                if (rect.width() <= viewWidth) {
                    curTransX = viewWidth / 2 - rect.centerX();
                } else if (rect.left + dx > 0) {
                    curTransX = -rect.left;
                } else if (rect.right + dx < viewWidth) {
                    curTransX = viewWidth - rect.right;
                } else {
                    curTransX = dx;
                }

                if (rect.height() <= viewHeight) {
                    curTransY = viewHeight / 2 - rect.centerY();
                } else if (rect.top + dy > 0) {
                    curTransY = -rect.top;
                } else if (rect.bottom + dy < viewHeight) {
                    curTransY = viewHeight - rect.bottom;
                } else {
                    curTransY = dy;
                }
//                curTransX = dx;
//                curTransY = dy;
                //
                matrix.postTranslate(curTransX, curTransY);
                //
                applyTransform();
                return true;
            }
        });

        playerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scaleGestureDetector.onTouchEvent(event);
                dragGestureDetector.onTouchEvent(event);
                return true;
            }
        });

        Player player = playerView.getPlayer();
        if (player != null) {
            player.addListener(new Player.Listener() {
                @Override
                public void onIsPlayingChanged(boolean isPlaying) {
                    Log.e(TAG, "onIsPlayingChanged: isPlaying=" + isPlaying);
                    if (isPlaying) {
                        //init
//                        applyTransform();
                    }
                }
            });
        }
    }


    private void applyTransform() {
        TextureView textureView = getVideoSurfaceView();
        if (textureView == null) {
            return;
        }
        textureView.setTransform(matrix);
        //支持暂停时处理
//        textureView.postInvalidate();
    }

    @OptIn(markerClass = UnstableApi.class)
    private TextureView getVideoSurfaceView() {
        View videoSurfaceView = playerView.getVideoSurfaceView();
        if (videoSurfaceView == null) {
            return null;
        }
        if (videoSurfaceView instanceof TextureView) {
            return (TextureView) videoSurfaceView;
        }
        return null;
    }

    private void dealTrans() {
        View videoSurfaceView = getVideoSurfaceView();
        if (videoSurfaceView == null) {
            return;
        }
//        float[] matrixValues = new float[9];
//        matrix.getValues(matrixValues);
//        float curScale2 = matrixValues[Matrix.MSCALE_X];
//        float transX = matrixValues[Matrix.MTRANS_X];
//        float transY = matrixValues[Matrix.MTRANS_Y];
//        if (videoSize == null) {
//            return;
//        }
        float viewWidth = videoSurfaceView.getWidth(); //733
        float viewHeight = videoSurfaceView.getHeight(); //550
        float scaledWidth = viewWidth * curScale; //1466
        float scaledHeight = viewHeight * curScale; //1100
//        Log.i(TAG, "tempLogss transX=" + transX + " curTransX=" + curTransX*curScale2); //
        //1080  618
        //scaledWidth - textureView.getWidth() = 2160-1080

        //scaledWidth=2160.0 scaledHeight=1216.0
//        dealMatrixValues(matrix); //curTransX=629.59015 curTransY=350.72638    curTransX=-475.07538 curTransY=344.25842
//        dealMatrixValues(matrix); //curTransX=587.9243 curTransY=-270.69052    curTransX=-480.07574 curTransY=-231.69052

        //scaledWidth=3240.0 scaledHeight=1236.0
//        dealMatrixValues(matrix); //curTransX=922.3865 curTransY=571.9302   curTransX=-1227.383 curTransY=545.7706
//        dealMatrixValues(matrix); //curTransX=917.24414 curTransY=-715.4701   curTransX=-1247.2715 curTransY=-668.59973

        //限制 x 方向平移
        float maxTransX = (scaledWidth - viewWidth) / 2f;
        float minTransX = -maxTransX;
//        float maxTransX = (curScale - 1) * textureView.getWidth() / 2;
//        float maxTransX = Math.max(0, scaledWidth - textureView.getWidth()) / 2;
        curTransX = Math.max(minTransX, Math.min(curTransX, maxTransX));

        // 最大允许平移距离 = (缩放后宽度 - 视图宽度) / 2
        Log.i(TAG, "tempLog maxTransX=" + maxTransX + " minTransX=" + minTransX + " curTransX=" + curTransX); //
        //限制 y 方向平移
        float maxTransY = 0f;
        float minTransY = -(scaledHeight - viewHeight);
//        float maxTransY = (curScale - 1) * viewHeight / 2;
//        float maxTransY = Math.max(0, scaledHeight - textureView.getHeight()) / 2;
        curTransY = Math.max(minTransY, Math.min(curTransY, maxTransY));
    }

    private void dealMatrixValues() {
        View videoSurfaceView = getVideoSurfaceView();
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

//        curTransX = transX;
//        curTransY = transY;
    }

    private float getScaleXFromMatrix(Matrix matrix) {
        float[] matrixValues = new float[9];
        matrix.getValues(matrixValues);
        return matrixValues[Matrix.MSCALE_X];
    }

    private float getTransXFromMatrix(Matrix matrix) {
        float[] matrixValues = new float[9];
        matrix.getValues(matrixValues);
        return matrixValues[Matrix.MTRANS_X];
    }

    private float getTransYFromMatrix(Matrix matrix) {
        float[] matrixValues = new float[9];
        matrix.getValues(matrixValues);
        return matrixValues[Matrix.MTRANS_Y];
    }

    private OnZoomListener onZoomListener;

    public interface OnZoomListener {
        void onZoom(float scale, PointF focusPoint);
    }
}
