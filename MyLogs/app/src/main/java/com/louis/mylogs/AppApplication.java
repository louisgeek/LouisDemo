package com.louis.mylogs;

import android.app.Application;
import android.os.Environment;

//import com.tencent.mars.xlog.Log;
//import com.tencent.mars.xlog.Xlog;

public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initXLog(true);
    }

    private void initXLog(boolean isDebug) {
        System.loadLibrary("c++_shared");
        System.loadLibrary("marsxlog");
        
        String logPath = getExternalFilesDir("marsLogs").getPath();
        
//        Xlog.appenderOpen(Xlog.LEVEL_ALL, Xlog.AppednerModeAsync, "", logPath, "zApp", 0, "");
//        Xlog.setConsoleLogOpen(isDebug);
//
//        Log.setLogImp(new Xlog());
    }

    public static void initMarsXlog() {
        System.loadLibrary("c++_shared");
        System.loadLibrary("marsxlog");

        final String SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath();
        final String logPath = SDCARD + "/marssample/log";

// this is necessary, or may crash for SIGBUS
        final String cachePath = this.getFilesDir() + "/xlog"

//init xlog
        Xlog.XLogConfig logConfig = new Xlog.XLogConfig();
        logConfig.mode = Xlog.AppednerModeAsync;
        logConfig.logdir = logPath;
        logConfig.nameprefix = logFileName;
        logConfig.pubkey = "";
        logConfig.compressmode = Xlog.ZLIB_MODE;
        logConfig.compresslevel = 0;
        logConfig.cachedir = "";
        logConfig.cachedays = 0;
        if (BuildConfig.DEBUG) {
            logConfig.level = Xlog.LEVEL_VERBOSE;
            Xlog.setConsoleLogOpen(true);
        } else {
            logConfig.level = Xlog.LEVEL_INFO;
            Xlog.setConsoleLogOpen(false);
        }

        Log.setLogImp(new Xlog());
    }
    
    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.appenderClose();
    }
}