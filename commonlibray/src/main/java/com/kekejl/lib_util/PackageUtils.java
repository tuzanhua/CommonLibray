package com.kekejl.lib_util;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.List;

/**
 * author：tzh on 2017/1/4 16:40
 * contact information: 188****5816
 * company :可可家里(北京)信息技术有限公司
 * describe:
 * modify instructions:
 */
public class PackageUtils {
    /**
     * getPackageName:得到应用包名. <br/>
     *
     * @return
     */
    public static String getPackageName(Context mContext) {
        return mContext.getPackageName();
    }

    /**
     * getVersionName:得到版本名称. <br/>
     *
     * @return
     */
    public static String getVersionName(Context mContext) {
        try {
            PackageManager manager = mContext.getPackageManager();
            PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "1.0";
    }

    /**
     * getVersionCode:得到版本号. <br/>
     *
     * @return
     */
    public static int getVersionCode(Context mContext) {

        try {
            PackageManager manager = mContext.getPackageManager();
            PackageInfo info = manager.getPackageInfo(mContext.getPackageName(),
                    0);
            int version = info.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * 获得当前进程号
     *
     * @param mContext
     * @return
     */
    public static String getCurProcessName(Context mContext) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    /**
     * 判断当前应用是否在前端运行
     *
     * @param mContext
     * @return
     */
    public static boolean isAppOnForeground(Context mContext) {
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (topActivity.getPackageName().equals(mContext.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取应用程序的名称
     *
     * @return
     */
    public static String getApplicationName(Context context) {
        PackageManager packageManager;
        ApplicationInfo applicationInfo;
        String applicationName = "大算车险";
        try {
            packageManager = context.getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            applicationName =
                    (String) packageManager.getApplicationLabel(applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NullPointerException e) {
        }

        return applicationName;
    }

}
