package com.louis.mystatusbar;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;
import android.view.View;
import android.view.Window;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class EdgeToEdgeHelper {
    private static final String TAG = "EdgeToEdgeHelper";

    /**
     * 部分页面有特殊背景，比如登录页面背景全黑，那么需要在 EdgeToEdge.enable(this); 以后设置 EdgeToEdgeHelper.setAppearanceLightBars(getWindow(),true);
     *
     * @param window
     * @param isDarkBackground
     */
    public static void setAppearanceLightBars(Window window, boolean isDarkBackground) {
        if (window != null) {
            WindowInsetsControllerCompat insetsController = WindowCompat.getInsetsController(window, window.getDecorView());
            Log.e(TAG, "setAppearanceLightBars: isDarkBackground=" + isDarkBackground);
            if (isDarkBackground) {
                //useAppearanceLightBars = false 时，显示浅色图标/文字（适合深色背景）
                insetsController.setAppearanceLightStatusBars(false);
                insetsController.setAppearanceLightNavigationBars(false);
            } else {
                //useAppearanceLightBars = true 时，显示深色图标/文字（适合浅色背景）
                insetsController.setAppearanceLightStatusBars(true);
                insetsController.setAppearanceLightNavigationBars(true);
            }
        }
    }

    public static boolean isNightMode(Context context) {
        return (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES;
    }

    public static int getStatusBarHeight(View view) {
        WindowInsetsCompat insets = ViewCompat.getRootWindowInsets(view);
        return insets != null ? insets.getInsets(WindowInsetsCompat.Type.statusBars()).top : 0;
    }

    public static int getNavigationBarHeight(View view) {
        WindowInsetsCompat insets = ViewCompat.getRootWindowInsets(view);
        return insets != null ? insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom : 0;
    }

}
