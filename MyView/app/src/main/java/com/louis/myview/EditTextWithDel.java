package com.louis.myview;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.EditText;

import androidx.core.content.ContextCompat;

/**
 * Created by louisgeek on 2016/5/13.
 */
public class EditTextWithDel extends androidx.appcompat.widget.AppCompatEditText {
    private final static String TAG = "EditTextWithDel";
    private Drawable imgClear;
    private Context mContext;

    public EditTextWithDel(Context context) {
        super(context);
        Log.d(TAG, "Context context");
        mContext = context;
        init();
    }

    public EditTextWithDel(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG, "Context context, AttributeSet attrs");
        mContext = context;
        init();

       /* //或者换种方式
        //这里构造方法也很重要，不加这个很多属性不能再XML里面定义
        this(context, attrs, android.R.attr.editTextStyle);*/
    }

    public EditTextWithDel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.d(TAG, "Context context, AttributeSet attrs, int defStyleAttr");
        mContext = context;
        init();
    }

    private void init() {
        //
        imgClear = ContextCompat.getDrawable(getContext(), R.drawable.cancel);

        //
        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setImgClearDrawable();
            }
        });

        //
        setImgClearDrawable();
    }

    private void setImgClearDrawable() {
        if (this.length() < 1) {
            this.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        } else {
            this.setCompoundDrawablesWithIntrinsicBounds(null, null, imgClear, null);
        }
    }

    /**
     * getX()是表示view相对于自身左上角的x坐标,而getRawX()是表示相对于屏幕左上角的x坐标值(注意:这个屏幕左上角是手机屏幕左上角,不管activity是否有titleBar或是否全屏幕)
     * event.getX() 获取相对应自身左上角的X坐标
     * event.getY() 获取相对应自身左上角的Y坐标
     * getWidth() 获取控件的宽度
     * getHeight() 获取控件的高度
     * getTotalPaddingRight() 获取Right图标左边缘到控件右边缘的距离
     * getPaddingRight() 获取Right图标右边缘到控件右边缘的距离
     */
    /* * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_UP) {
            float eventRawX = event.getRawX();
            float eventRawY = event.getRawY();
            Log.d(TAG, "eventRawX = " + eventRawX + "; eventRawY = " + eventRawY);
            //矩形
            Rect rect = new Rect();
            //getGlobalVisibleRect的作用是获取视图在屏幕坐标中的可视区域
            //getLocalVisibleRect的作用是获取视图本身可见的坐标区域，坐标以自己的左上角为原点（0，0）
            //left ： 矩形左边的X坐标
            // top:    矩形顶部的Y坐标
            //right :  矩形右边的X坐标
            // bottom： 矩形底部的Y坐标

            this.getGlobalVisibleRect(rect);
            Log.d(TAG, "rect.left = " + rect.left + "; rect.right = " + rect.right);
            rect.left = rect.right - 100;
            Log.d(TAG, "new rect.left = " + rect.left + ";rect.right = " + rect.right);
            if (rect.contains((int) eventRawX, (int) eventRawY)) {
                this.setText("");
            }
        }

        return super.onTouchEvent(event);
    }


}
