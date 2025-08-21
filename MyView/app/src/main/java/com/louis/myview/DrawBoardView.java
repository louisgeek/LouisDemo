package com.louis.myview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

/**
 * 在大多数情况下，CLEAR 的擦除操作会比 SRC 的填充操作更快，尤其是在硬件加速下，因为它只需要清空像素，不涉及颜色混合
 * 但如果只是想用纯色覆盖整个画布（比如清空为白色），SRC 模式也很高效，且更直观，所以橡皮擦功能建议用 CLEAR，而清空画布建议用 SRC（配合纯色）
 * 有时候硬件加速会导致橡皮擦变为黑色而不是透明，所以可能需要在橡皮擦模式下设置 setLayerType(View.LAYER_TYPE_SOFTWARE, null) 禁用硬件加速来解决黑色问题
 */
public class DrawBoardView extends View {
    private static final int DEFAULT_DRAW_WIDTH = 8;

    private static final int DEFAULT_DRAW_COLOR = Color.RED;
    private static final int DEFAULT_ERASER_WIDTH = 40;

    //private static final int DEFAULT_ERASER_COLOR = Color.TRANSPARENT; //有问题
    private static final int DEFAULT_ERASER_COLOR = Color.WHITE;

    private final Paint drawPaint = new Paint();
    private final Paint eraserPaint = new Paint();
    private Paint currentPaint;
    private Path currentPath;
    private boolean eraserMode = false;
    private int drawWidth = DEFAULT_DRAW_WIDTH;
    private int drawColor = DEFAULT_DRAW_COLOR;
    private int eraserWidth = DEFAULT_ERASER_WIDTH;

    private Bitmap cacheBitmap;
    private Canvas cacheCanvas;
    private int cacheWidth = 0;
    private int cacheHeight = 0;

    public DrawBoardView(Context context) {
        this(context, null);
    }

    public DrawBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaints();
        setBackgroundColor(DEFAULT_ERASER_COLOR);
    }

    private void initPaints() {
        drawPaint.setAntiAlias(true);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        drawPaint.setStrokeWidth(drawWidth);
        drawPaint.setColor(drawColor);

        eraserPaint.setAntiAlias(true);
        eraserPaint.setStyle(Paint.Style.STROKE);
        eraserPaint.setStrokeJoin(Paint.Join.ROUND);
        eraserPaint.setStrokeCap(Paint.Cap.ROUND);
        eraserPaint.setStrokeWidth(eraserWidth);

        eraserPaint.setColor(DEFAULT_ERASER_COLOR);
        eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
    }

    public void setDrawWidth(int width) {
        this.drawWidth = width;
        drawPaint.setStrokeWidth(width);
    }

    public void setDrawColor(int color) {
        this.drawColor = color;
        drawPaint.setColor(color);
    }

    public void setEraserWidth(int width) {
        this.eraserWidth = width;
        eraserPaint.setStrokeWidth(width);
    }

    public void setEraserMode(boolean eraser) {
        this.eraserMode = eraser;
    }

    public boolean isEraserMode() {
        return eraserMode;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0 && (w != cacheWidth || h != cacheHeight)) {
            cacheWidth = w;
            cacheHeight = h;
            if (cacheBitmap != null && !cacheBitmap.isRecycled()) {
                cacheBitmap.recycle();
            }
            cacheBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            cacheCanvas = new Canvas(cacheBitmap);
            cacheCanvas.drawColor(DEFAULT_ERASER_COLOR);
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if (cacheBitmap != null && !cacheBitmap.isRecycled()) {
            canvas.drawBitmap(cacheBitmap, 0, 0, null);
        }
        if (currentPath != null && currentPaint != null) {
            canvas.drawPath(currentPath, currentPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentPath = new Path();
                currentPath.moveTo(x, y);
                if (eraserMode) {
                    currentPaint = eraserPaint;
                } else {
                    drawPaint.setColor(drawColor);
                    currentPaint = drawPaint;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (currentPath != null) {
                    currentPath.lineTo(x, y);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (currentPath != null && currentPaint != null && cacheCanvas != null) {
                    cacheCanvas.drawPath(currentPath, currentPaint);
                    currentPath = null;
                    currentPaint = null;
                    invalidate();
                }
                break;
        }
        return true;
    }

    public void clearCanvas() {
        if (cacheCanvas != null && cacheBitmap != null && !cacheBitmap.isRecycled()) {
            cacheCanvas.drawColor(DEFAULT_ERASER_COLOR, PorterDuff.Mode.SRC);
            invalidate();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (cacheBitmap != null && !cacheBitmap.isRecycled()) {
            cacheBitmap.recycle();
            cacheBitmap = null;
            cacheCanvas = null;
        }
    }
}
