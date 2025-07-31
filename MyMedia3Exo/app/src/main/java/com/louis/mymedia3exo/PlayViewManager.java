package com.louis.mymedia3exo;


import android.annotation.SuppressLint;
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


public class PlayViewManager {
    private static final String TAG = "PlayViewManager";
    private PlayerView playerView;
    //
    private Matrix matrix = new Matrix();
    //缩放
    private ScaleGestureDetector scaleGestureDetector;
    private static final float MIN_SCALE = 1.0f;
    private static final float MAX_SCALE = 10.0f;
    private float curScale = 1f;
    private PointF curFocusPoint = new PointF();
    //拖动
    private GestureDetector dragGestureDetector;
    private float curTransX;
    private float curTransY;

    public PlayViewManager(PlayerView playerView, OnZoomListener onZoomListener) {
        this.playerView = playerView;
        this.onZoomListener = onZoomListener;
        //
        Context context = playerView.getContext();

        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            /**
             * 当两个手指在屏幕上移动时持续调用
             */
            @Override
            public boolean onScale(@NonNull ScaleGestureDetector detector) {
                float scaleFactor = detector.getScaleFactor(); //当前帧的缩放因子（比如 1.05 表示放大 5%，0.95 表示缩小 5%）
                float newScale = curScale * scaleFactor; //累积
                newScale = Math.max(MIN_SCALE, Math.min(newScale, MAX_SCALE)); //修正（限制缩放比例）
                float realFactor = newScale / curScale; //除回去，相当于修正后的 scaleFactor（计算限制后的实际缩放因子）
                matrix.postScale(realFactor, realFactor, detector.getFocusX(), detector.getFocusY()); //焦点坐标（两个手指的中心点）
                curScale = newScale; //记录
                curFocusPoint.set(detector.getFocusX(), detector.getFocusY());
                if (onZoomListener != null) {
                    onZoomListener.onZoom(curScale);
                }
                applyTransform();
                return true;
            }

            @Override
            public void onScaleEnd(@NonNull ScaleGestureDetector detector) {
                super.onScaleEnd(detector);
//                fixBounds();
            }
        });

        dragGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

//            @Override
//            public boolean onDown(@NonNull MotionEvent e) {
//                return true;
//            }

            /**
             * 拖动手势中持续触发
             */
            @Override
            public boolean onScroll(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
//                if (curScale <= MIN_SCALE) { //不用 去掉
//                    //未放大，不处理拖动数据
//                    //return false;
//                    return true;
//                }
                //distanceX 和 distanceY 表示当前事件和上一个事件之间的移动距离（不是总距离）
                //distanceX 横向滑动距离，向右滑动为负数，x 值减小，向左滑动正数，x 值增加（preX - curX）
                //distanceX 纵向滑动距离，向下滑动负数，y 值减小，向上滑动正数，y 增加(preY - curY)
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
                    dx = viewWidth / 2 - rect.centerX();
                } else if (rect.left + dx > 0) {
                    dx = -rect.left;
                } else if (rect.right + dx < viewWidth) {
                    dx = viewWidth - rect.right;
                }

                // 水平方向限制
//                if (rect.left < 0) {
//                    dx = -rect.left;
//                }
//                if (rect.right > viewWidth) {
//                    dx = viewWidth - rect.right;
//                }

                if (rect.height() <= viewHeight) {
                    dy = viewHeight / 2 - rect.centerY();
                } else if (rect.top + dy > 0) {
                    dy = -rect.top;
                } else if (rect.bottom + dy < viewHeight) {
                    dy = viewHeight - rect.bottom;
                }
                // 垂直方向限制
//                if (rect.top < 0) {
//                   dy=  -rect.top;
//                }
//                if (rect.bottom > viewHeight) {
//                    dy = viewHeight - rect.bottom;
//                }

                matrix.postTranslate(dx, dy);
                //
                float[] matrixValues = new float[9];
                matrix.getValues(matrixValues);
                curTransX = matrixValues[Matrix.MTRANS_X];
                curTransY = matrixValues[Matrix.MTRANS_Y];
                //
                applyTransform();
                return true;
            }
        });

        playerView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scaleGestureDetector.onTouchEvent(event);
                dragGestureDetector.onTouchEvent(event);
                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    fixBounds();
                }
                return true;
            }
        });

        Player player = playerView.getPlayer();
        if (player != null) {
            player.addListener(new Player.Listener() {
                @Override
                public void onIsPlayingChanged(boolean isPlaying) {
                    if (isPlaying) { //init
                        matrix.setScale(curScale, curScale, curFocusPoint.x, curFocusPoint.y);
//                        matrix.postTranslate(curTransX, curTransY);
//                        matrix.reset();
//                        matrix.postScale(curScale, curScale, curFocusPoint.x, curFocusPoint.y);
                        //
                        float[] matrixValues = new float[9];
                        matrix.getValues(matrixValues);
                        matrixValues[Matrix.MTRANS_X] = curTransX;
                        matrixValues[Matrix.MTRANS_Y] = curTransY;
                        matrix.setValues(matrixValues);

                        applyTransform();
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
        //暂停处理
        textureView.postInvalidate();
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

    private void fixBounds() {
        float[] matrixValues = new float[9];
        matrix.getValues(matrixValues);
        float scale = matrixValues[Matrix.MSCALE_X];
        float transX = matrixValues[Matrix.MTRANS_X];
        float transY = matrixValues[Matrix.MTRANS_Y];

        View videoSurfaceView = getVideoSurfaceView();
        if (videoSurfaceView == null) {
            return;
        }
        float textureViewWidth = videoSurfaceView.getWidth();
        float textureViewHeight = videoSurfaceView.getHeight();

        float scaledWidth = textureViewWidth * scale;
        float scaledHeight = textureViewHeight * scale;

        float fixX;
        if (scaledWidth <= textureViewWidth) {
            fixX = (textureViewWidth - scaledWidth) / 2 - transX;
        } else if (transX > 0) {
            fixX = 0 - transX;
        } else if (scaledWidth + transX < textureViewWidth) {
            fixX = textureViewWidth - (scaledWidth + transX);
        } else {
            fixX = 0f;
        }

        float fixY;
        if (scaledHeight <= textureViewHeight) {
            fixY = (textureViewHeight - scaledHeight) / 2 - transY;
        } else if (transY > 0) {
            fixY = 0 - transY;
        } else if (scaledHeight + transY < textureViewHeight) {
            fixY = textureViewHeight - (scaledHeight + transY);
        } else {
            fixY = 0f;
        }

        matrix.postTranslate(fixX, fixY);
        applyTransform();
    }

    public void release() {
        onZoomListener = null;
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
        void onZoom(float scale);
    }
}
