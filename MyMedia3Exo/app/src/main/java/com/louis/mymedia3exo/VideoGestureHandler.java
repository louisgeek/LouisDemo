package com.louis.mymedia3exo;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.Size;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.TextureView;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.annotation.OptIn;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.ui.PlayerView;

public class VideoGestureHandler implements
        ScaleGestureDetector.OnScaleGestureListener,
        GestureDetector.OnGestureListener {

    // 缩放参数
    private static final float MIN_SCALE = 1.0f;
    private static final float MAX_SCALE = 5.0f;
    private float currentScale = MIN_SCALE;
    private final PointF scaleCenter = new PointF();

    // 平移参数
    private float transX = 0f;
    private float transY = 0f;
    private final PointF touchStart = new PointF();

    // 矩阵与状态
    private final Matrix transformMatrix = new Matrix();
    private final Matrix savedMatrix = new Matrix();
    private final float[] matrixValues = new float[9];
    private boolean isScaling = false;
    private boolean isDragging = false;
    private final float touchSlop;

    // 视图与检测器
    private final PlayerView playerView;
    private final ScaleGestureDetector scaleDetector;
    private final GestureDetector gestureDetector;

    public VideoGestureHandler(PlayerView playerView) {
        this.playerView = playerView;
        this.touchSlop = ViewConfiguration.get(playerView.getContext()).getScaledTouchSlop();
        this.scaleDetector = new ScaleGestureDetector(playerView.getContext(), this);
        this.gestureDetector = new GestureDetector(playerView.getContext(), this);

        playerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scaleDetector.onTouchEvent(event);
                gestureDetector.onTouchEvent(event);

                // 重置手势状态
                if (event.getAction() == MotionEvent.ACTION_UP ||
                        event.getAction() == MotionEvent.ACTION_CANCEL) {
                    isScaling = false;
                    isDragging = false;
                }
                return true;
            }
        });
    }


    // ------------------- 缩放手势处理 -------------------
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        if (!isScaling) return false;

        // 计算新缩放值并限制范围
        float newScale = currentScale * detector.getScaleFactor();
        newScale = Math.max(MIN_SCALE, Math.min(newScale, MAX_SCALE));

        if (newScale != currentScale) {
            float scaleDiff = newScale / currentScale;
            currentScale = newScale;

            // 基于缩放中心更新矩阵
            transformMatrix.set(savedMatrix);
            transformMatrix.postScale(scaleDiff, scaleDiff, scaleCenter.x, scaleCenter.y);

            // 同步平移值并修正边界
            updateTranslationFromMatrix();
            correctTranslationBounds();

            applyTransform();
        }
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        savedMatrix.set(transformMatrix);
        scaleCenter.set(detector.getFocusX(), detector.getFocusY());
        isScaling = true;
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
    }

    // ------------------- 平移手势处理 -------------------
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        // 缩放中或未达到滑动阈值时不处理平移
        if (isScaling) return false;
        if (!isDragging) {
            float dx = Math.abs(e2.getX() - touchStart.x);
            float dy = Math.abs(e2.getY() - touchStart.y);
            if (dx < touchSlop && dy < touchSlop) return false;
            isDragging = true;
        }

        // 仅在缩放超过1倍时允许平移
        if (currentScale <= MIN_SCALE) return false;

        // 计算平移增量（除以缩放倍数，使操作更自然）
        float deltaX = -distanceX / currentScale;
        float deltaY = -distanceY / currentScale;

        // 应用边界限制
        Size videoSize = new Size(getVideoSurfaceView().getWidth(), getVideoSurfaceView().getHeight());
        Size viewSize = new Size(playerView.getWidth(), playerView.getHeight());
        transX = clampX(transX + deltaX, videoSize, viewSize);
        transY = clampY(transY + deltaY, videoSize, viewSize);

        // 更新矩阵并应用变换
        updateMatrixFromTranslation();
        return true;
    }

    // ------------------- 边界计算（核心逻辑） -------------------

    /**
     * 限制X轴平移范围
     */
    private float clampX(float transX, Size videoSize, Size viewSize) {
        if (videoSize.getWidth() == 0 || videoSize.getHeight() == 0) return 0;

        // 计算视频与视图的宽高比
        float videoAspect = (float) videoSize.getWidth() / videoSize.getHeight();
        float viewAspect = (float) viewSize.getWidth() / viewSize.getHeight();

        // 计算视频内容在视图中的实际显示宽度（排除黑边）
        float contentWidth = (videoAspect >= viewAspect)
                ? viewSize.getHeight() * videoAspect  // 视频更宽，高度充满视图
                : viewSize.getWidth();                // 视频更高，宽度充满视图

        // 缩放后的内容宽度与最大允许平移距离
        float scaledWidth = contentWidth * currentScale;
        float maxTrans = (scaledWidth - viewSize.getWidth()) / 2f;

        return Math.max(-maxTrans, Math.min(transX, maxTrans));
    }

    /**
     * 限制Y轴平移范围
     */
    private float clampY(float transY, Size videoSize, Size viewSize) {
        if (videoSize.getWidth() == 0 || videoSize.getHeight() == 0) return 0;

        float videoAspect = (float) videoSize.getWidth() / videoSize.getHeight();
        float viewAspect = (float) viewSize.getWidth() / viewSize.getHeight();

        // 计算视频内容在视图中的实际显示高度（排除黑边）
        float contentHeight = (videoAspect >= viewAspect)
                ? viewSize.getHeight()                // 视频更宽，高度充满视图
                : viewSize.getWidth() / videoAspect;  // 视频更高，宽度充满视图

        // 缩放后的内容高度与最大允许平移距离
        float scaledHeight = contentHeight * currentScale;
        float maxTrans = (scaledHeight - viewSize.getHeight()) / 2f;

        return Math.max(-maxTrans, Math.min(transY, maxTrans));
    }

    /**
     * 缩放后修正平移边界
     */
    private void correctTranslationBounds() {
        Size videoSize = new Size(getVideoSurfaceView().getWidth(), getVideoSurfaceView().getHeight());
        Size viewSize = new Size(playerView.getWidth(), playerView.getHeight());
        transX = clampX(transX, videoSize, viewSize);
        transY = clampY(transY, videoSize, viewSize);
    }

    // ------------------- 矩阵与平移值同步 -------------------
    private void updateTranslationFromMatrix() {
        transformMatrix.getValues(matrixValues);
        transX = matrixValues[Matrix.MTRANS_X];
        transY = matrixValues[Matrix.MTRANS_Y];
    }

    private void updateMatrixFromTranslation() {
        transformMatrix.set(savedMatrix);
        transformMatrix.postTranslate(transX, transY);
        applyTransform();
    }

    // ------------------- 其他手势回调 -------------------
    @Override
    public boolean onDown(MotionEvent e) {
        touchStart.set(e.getX(), e.getY());
        savedMatrix.set(transformMatrix);
        return true; // 必须返回true以接收后续事件
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // 单击事件：可用于显示/隐藏控制器
        playerView.performClick();
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float vx, float vy) {
        return false;
    }

    // ------------------- 状态保存与恢复 -------------------
    public void saveState(Matrix outMatrix, float[] outScale) {
        outMatrix.set(transformMatrix);
        outScale[0] = currentScale;
    }

    public void restoreState(Matrix savedMatrix, float savedScale) {
        transformMatrix.set(savedMatrix);
        currentScale = savedScale;
        updateTranslationFromMatrix();
        applyTransform();
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

    private void applyTransform() {
        TextureView textureView = getVideoSurfaceView();
        if (textureView == null) {
            return;
        }
        textureView.setTransform(transformMatrix);
        //支持暂停时处理
//        textureView.postInvalidate();
    }
}
