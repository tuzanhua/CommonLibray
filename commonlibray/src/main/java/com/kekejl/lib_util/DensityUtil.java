package com.kekejl.lib_util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

/**
 * create by zqikai 20160525
 * 屏幕相关,像素相关的类
 */
public final class DensityUtil {

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int px2sp(Context context, float pxValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int getDialogW(Context aty) {
        DisplayMetrics dm = aty.getResources().getDisplayMetrics();
        int w = dm.widthPixels - 100;
        // int w = aty.getWindowManager().getDefaultDisplay().getWidth() - 100;
        return w;
    }


    public static int getScreenW(Context aty) {
        DisplayMetrics dm = aty.getResources().getDisplayMetrics();
        int w = dm.widthPixels;
        return w;
    }

    public static int getScreenH(Context aty) {
        DisplayMetrics dm = aty.getResources().getDisplayMetrics();
        int h = dm.heightPixels;

        return h;
    }

    /**
     * 获取顶部status bar 高度
     * @param mContext
     * @return
     */
    public static int getStatusBarHeight(Context mContext) {
        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen","android");
        int height = mContext.getResources().getDimensionPixelSize(resourceId);
        Log.v("dbw", "Status height:" + height);
        return height;
    }

    /**
     * 获取底部 navigation bar 高度
     * @param mContext
     * @return
     */
    public static int getNavigationBarHeight(Context mContext) {
        int resourceId = mContext.getResources().getIdentifier("navigation_bar_height","dimen", "android");
        int height = mContext.getResources().getDimensionPixelSize(resourceId);
        Log.v("dbw", "Navi height:" + height);
        return height;
    }

    /**
     * 获得状态栏的高度
     *
     * @param context
     * @return
     */
    public static int getStatusHeight(Context context) {

        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    /**
     * 获取当前屏幕截图，包含状态栏
     *
     * @param activity
     * @return
     */
    public static Bitmap snapShotWithStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        int width = getScreenW(activity);
        int height = getScreenH(activity);
        Bitmap bp;
        bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
        view.destroyDrawingCache();
        return bp;

    }

    /**
     * 获取当前屏幕截图，不包含状态栏
     *
     * @param activity
     * @return
     */
    public static Bitmap snapShotWithoutStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        int width = getScreenW(activity);
        int height = getScreenH(activity);
        Bitmap bp;
        bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height
                - statusBarHeight);
        view.destroyDrawingCache();
        return bp;
    }

    /**
     * 获取屏幕的密度
     *
     * @param context
     * @return
     */
    public static String getDensity(Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return String.valueOf(density);
    }
}