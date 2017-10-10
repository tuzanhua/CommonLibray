package com.kekejl.lib_util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;
import com.readystatesoftware.systembartint.SystemBarTintManager;


/**
 * Created by zqikai on 2015/10/31.
 */
public class StatusBarUtil {

    public static void setStatusBar(Activity activity) {
        initSystemBar(activity);
    }

    public static void setLoginStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true, activity);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(activity);
        tintManager.setStatusBarTintEnabled(true);
//        tintManager.setStatusBarTintResource(R.color.color_login);
    }

    public static void setStatusBar(Activity activity, int color) {
        initSystemBar(activity, color);
    }

    /**
     * 设置状态栏背景状态
     */
    private static void initSystemBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true, activity);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(activity);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.color_transparent);
    }

    private static void initSystemBar(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true, activity);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(activity);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(color);
//        tintManager.setNavigationBarTintEnabled(true);
//        tintManager.setNavigationBarTintResource(color);
    }

    @TargetApi(19)
    private static void setTranslucentStatus(boolean on, Activity activity) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

}
