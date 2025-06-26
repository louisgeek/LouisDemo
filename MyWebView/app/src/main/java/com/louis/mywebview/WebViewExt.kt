package com.louis.mywebview

import android.graphics.Color
import android.view.View
import android.webkit.WebView

/**
 * 比如让 WebView 透明
 */
fun WebView.setBackgroundColorCompat(color: Int) {
    //this.setBackgroundResource(android.R.color.transparent); //无效
    //this.setBackground(new ColorDrawable(Color.TRANSPARENT)); //无效
    this.setBackgroundColor(color) //重要
    //this.setBackgroundColor(color); //二次调用未能验证，据说部分机型需要重复调用才生效
    if (color == Color.TRANSPARENT) {
        this.setBackgroundResource(android.R.color.transparent) //附带一下
        //
        this.background?.alpha = 0 //未能验证，据说影响部分机型
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null) //关闭硬件加速
    }
}