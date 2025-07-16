package com.louis.myview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by louisgeek on 2016/5/13.
 */
public class MyRoundImageView extends androidx.appcompat.widget.AppCompatImageView {
    private Bitmap mBitmap;
    private Paint mPaint;
    private static final String TAG = "MyRoundImageView";

    public MyRoundImageView(Context context) {
        super(context);
        Log.d(TAG, "Context context");
        init();
    }

    public MyRoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG, "Context context, AttributeSet attrs");
        init();
    }

    public MyRoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.d(TAG, "Context context, AttributeSet attrs, int defStyleAttr");
        init();
    }

    //传入一个Bitmap对象
    public void setBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        // 给Paint加上抗锯齿标志。然后将Paint对象作为参数传给canvas的绘制方法。
        mPaint.setAntiAlias(true);// 抗锯尺
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mBitmap == null) {
            return;
        }
        Log.d(TAG, "onDraw:mBitmap" + mBitmap);
        Rect mRect = new Rect();
        mRect.set(0, 0, this.getWidth(), this.getHeight());
        canvas.save();

        //给Canvas加上抗锯齿标志。有些地方不能用paint的，就直接给canvas加抗锯齿，更方便。
        PaintFlagsDrawFilter pfdf = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG);
        canvas.setDrawFilter(pfdf);

        Path mPath = new Path();
        mPath.addCircle(this.getWidth() / 2, this.getWidth() / 2, this.getHeight() / 2, Path.Direction.CCW);
        canvas.clipPath(mPath, Region.Op.INTERSECT); //DIFFERENCE
        canvas.drawBitmap(mBitmap, null, mRect, mPaint);
        canvas.restore();
    }
}
