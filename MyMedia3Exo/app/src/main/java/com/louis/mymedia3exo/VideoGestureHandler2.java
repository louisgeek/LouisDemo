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

/**
 * 处理视频播放器的缩放和平移手势
 */
public class VideoGestureHandler2 implements
        ScaleGestureDetector.OnScaleGestureListener,
        GestureDetector.OnGestureListener {

    // 缩放参数
    private static final float MIN_SCALE = 1.0f;    // 最小缩放比例
    private static final float MAX_SCALE = 5.0f;    // 最大缩放比例
    private float currentScale = 1.0f;              // 当前缩放比例
    private final PointF scaleCenter = new PointF();// 缩放中心点

    // 平移参数
    private float transX = 0f;                      // X轴平移量
    private float transY = 0f;                      // Y轴平移量
    private final PointF touchStart = new PointF(); // 触摸起点

    // 矩阵相关
    private final Matrix transformMatrix = new Matrix();
    private final Matrix savedMatrix = new Matrix();
    private final float[] matrixValues = new float[9];

    // 状态控制
    private boolean isScaling = false;              // 是否正在缩放
    private boolean isDragging = false;             // 是否正在拖动
    private final float touchSlop;                  // 滑动触发阈值

    // 手势检测器
    private final ScaleGestureDetector scaleDetector;
    private final GestureDetector gestureDetector;

    // 播放器视图
    private final PlayerView playerView;

    public VideoGestureHandler2(PlayerView playerView) {
        this.playerView = playerView;
        this.scaleDetector = new ScaleGestureDetector(playerView.getContext(), this);
        this.gestureDetector = new GestureDetector(playerView.getContext(), this);
        this.touchSlop = ViewConfiguration.get(playerView.getContext()).getScaledTouchSlop();
    }

    /**
     * 处理触摸事件，分发给相应的手势检测器
     */
    public boolean onTouchEvent(MotionEvent event) {
        // 优先处理缩放手势
        boolean scaleHandled = scaleDetector.onTouchEvent(event);
        // 处理其他手势
        boolean gestureHandled = gestureDetector.onTouchEvent(event);

        // 处理手势结束
        if (event.getAction() == MotionEvent.ACTION_UP ||
                event.getAction() == MotionEvent.ACTION_CANCEL) {
            isScaling = false;
            isDragging = false;
        }

        return scaleHandled || gestureHandled;
    }

    // ------------------- 缩放手势处理 -------------------

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        if (!isScaling) return false;

        // 计算新的缩放比例
        float newScale = currentScale * detector.getScaleFactor();
        newScale = Math.max(MIN_SCALE, Math.min(newScale, MAX_SCALE));

        if (newScale != currentScale) {
            float scaleDiff = newScale / currentScale;
            currentScale = newScale;

            // 基于缩放中心更新矩阵
            transformMatrix.set(savedMatrix);
            transformMatrix.postScale(scaleDiff, scaleDiff, scaleCenter.x, scaleCenter.y);

            // 更新平移值并修正边界
            updateTranslationFromMatrix();
            correctTranslationBounds();

            // 应用变换
            applyTransform();
        }
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        // 保存当前状态
        savedMatrix.set(transformMatrix);
        scaleCenter.set(detector.getFocusX(), detector.getFocusY());
        isScaling = true;
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        // 缩放结束时确保在边界内
        correctTranslationBounds();
    }

    // ------------------- 平移手势处理 -------------------

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        // 缩放中或未达到滑动阈值时不处理平移
        if (isScaling) return false;

        if (!isDragging) {
            // 检查是否达到滑动触发阈值
            float dx = Math.abs(e2.getX() - touchStart.x);
            float dy = Math.abs(e2.getY() - touchStart.y);
            if (dx < touchSlop && dy < touchSlop) {
                return false;
            }
            isDragging = true;
        }

        // 只有缩放超过1倍时才允许平移
        if (currentScale <= MIN_SCALE) return false;

        // 计算平移增量（除以缩放倍数，使平移速度与缩放匹配）
        float deltaX = -distanceX / currentScale;
        float deltaY = -distanceY / currentScale;

        // 计算新的平移位置
        float newTransX = transX + deltaX;
        float newTransY = transY + deltaY;

        // 应用边界限制
        newTransX = clampTranslationX(newTransX);
        newTransY = clampTranslationY(newTransY);

        // 更新平移值并应用矩阵变换
        transX = newTransX;
        transY = newTransY;
        updateMatrixFromTranslation();

        return true;
    }

    // ------------------- 边界限制处理 -------------------

    /**
     * 限制X轴平移范围
     */
    private float clampTranslationX(float transX) {
        Size videoSize = new Size(getVideoSurfaceView().getWidth(), getVideoSurfaceView().getHeight());
        Size viewSize = new Size(playerView.getWidth(), playerView.getHeight());

        // 视频尺寸未知时不限制
        if (videoSize.getWidth() == 0 || videoSize.getHeight() == 0) {
            return 0;
        }

        // 计算视频和视图的宽高比
        float videoAspect = (float) videoSize.getWidth() / videoSize.getHeight();
        float viewAspect = (float) viewSize.getWidth() / viewSize.getHeight();

        // 计算视频内容在视图中的实际显示宽度（扣除黑边）
        float contentWidth = (videoAspect >= viewAspect)
                ? viewSize.getHeight() * videoAspect
                : viewSize.getWidth();

        // 计算缩放后的内容宽度和最大允许平移距离
        float scaledContentWidth = contentWidth * currentScale;
        float maxTrans = (scaledContentWidth - viewSize.getWidth()) / 2f;

        // 限制平移范围
        return Math.max(-maxTrans, Math.min(transX, maxTrans));
    }

    /**
     * 限制Y轴平移范围
     */
    private float clampTranslationY(float transY) {
        Size videoSize = new Size(getVideoSurfaceView().getWidth(), getVideoSurfaceView().getHeight());
        Size viewSize = new Size(playerView.getWidth(), playerView.getHeight());

        // 视频尺寸未知时不限制
        if (videoSize.getWidth() == 0 || videoSize.getHeight() == 0) {
            return 0;
        }

        // 计算视频和视图的宽高比
        float videoAspect = (float) videoSize.getWidth() / videoSize.getHeight();
        float viewAspect = (float) viewSize.getWidth() / viewSize.getHeight();

        // 计算视频内容在视图中的实际显示高度（扣除黑边）
        float contentHeight = (videoAspect < viewAspect)
                ? viewSize.getWidth() / videoAspect
                : viewSize.getHeight();

        // 计算缩放后的内容高度和最大允许平移距离
        float scaledContentHeight = contentHeight * currentScale;
        float maxTrans = (scaledContentHeight - viewSize.getHeight()) / 2f;

        // 限制平移范围
        return Math.max(-maxTrans, Math.min(transY, maxTrans));
    }

    /**
     * 修正平移边界，确保内容不超出视图
     */
    private void correctTranslationBounds() {
        transX = clampTranslationX(transX);
        transY = clampTranslationY(transY);
        updateMatrixFromTranslation();
    }

    // ------------------- 矩阵与平移值同步 -------------------

    /**
     * 从矩阵中更新平移值
     */
    private void updateTranslationFromMatrix() {
        transformMatrix.getValues(matrixValues);
        transX = matrixValues[Matrix.MTRANS_X];
        transY = matrixValues[Matrix.MTRANS_Y];
    }

    /**
     * 根据当前平移值更新矩阵
     */
    private void updateMatrixFromTranslation() {
        transformMatrix.set(savedMatrix);
        transformMatrix.postTranslate(transX, transY);
        applyTransform();
    }

    // ------------------- 其他手势处理 -------------------

    @Override
    public boolean onDown(MotionEvent e) {
        // 记录触摸起点
        touchStart.set(e.getX(), e.getY());
        // 保存当前矩阵状态
        savedMatrix.set(transformMatrix);
        return true; // 必须返回true才能接收后续手势
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // 显示按下状态（可根据需要实现）
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // 单击事件 - 切换控制器显示状态
        playerView.performClick();
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        // 长按事件（可根据需要实现）
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        // 快速滑动事件（此处不处理）
        return false;
    }

    // ------------------- 状态保存与恢复 -------------------

    /**
     * 保存当前缩放和平移状态
     */
    public void saveState(Matrix outMatrix, float[] outScale) {
        outMatrix.set(transformMatrix);
        outScale[0] = currentScale;
    }

    /**
     * 恢复缩放和平移状态
     */
    public void restoreState(Matrix savedMatrix, float savedScale) {
        transformMatrix.set(savedMatrix);
        currentScale = savedScale;
        updateTranslationFromMatrix();
        applyTransform();
    }

    /**
     * 重置缩放和平移状态
     */
    public void reset() {
        currentScale = MIN_SCALE;
        transX = 0f;
        transY = 0f;
        transformMatrix.reset();
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
    