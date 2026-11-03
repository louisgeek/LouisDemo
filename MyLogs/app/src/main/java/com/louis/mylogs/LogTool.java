package com.louis.mylogs;

import com.elvishew.xlog.XLog;

public class LogTool {
    public static void v(String tag, String msg) {
        XLog.v(" "+tag + ":" + msg);
    }

    public static void d(String tag, String msg) {
        XLog.d(" "+tag + ":" + msg);
    }

    public static void i(String tag, String msg) {
        XLog.i(" "+tag + ":" + msg);
    }

    public static void w(String tag, String msg) {
        XLog.w(" "+tag + ":" + msg);
    }

    public static void e(String tag, String msg) {
        XLog.e(" "+tag + ":" + msg);
    }

    public static void v(String msg) {
        XLog.v(":" + msg);
    }

    public static void d(String msg) {
        XLog.d(":" + msg);
    }

    public static void i(String msg) {
        XLog.i(":" + msg);
    }

    public static void w(String msg) {
        XLog.w(":" + msg);
    }

    public static void e(String msg) {
        XLog.e(":" + msg);
    }
}
