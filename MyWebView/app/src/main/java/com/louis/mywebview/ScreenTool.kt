package com.louisgeek.library.tool

import android.content.res.Resources

/**
 * Created by louisgeek on 2021/11/10.
 */
internal object ScreenTool {
    @JvmStatic
    val screenWidth = Resources.getSystem().displayMetrics.widthPixels

    //状态栏 + 标题栏 + 内容 + 导航栏
    @JvmStatic
    val screenHeight = Resources.getSystem().displayMetrics.heightPixels
}