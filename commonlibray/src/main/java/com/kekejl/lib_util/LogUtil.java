package com.kekejl.lib_util;

import android.os.Environment;
import android.text.TextUtils;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * author：tzh on 2017/1/4 15:36
 * contact information: 188****5816
 * company :可可家里(北京)信息技术有限公司
 * describe:
 * modify instructions:
 */
public class LogUtil {

    private static final String TAG = "LogUtil";
    /**
     * 初始化logger
     *
     * @param isDebug
     * @param tag
     */
    public static void init(boolean isDebug, String tag) {
        if (isDebug) {
            Logger.init(tag)
                    .methodCount(3)
                    .logLevel(LogLevel.FULL);
        } else {
            Logger.init().logLevel(LogLevel.NONE);
        }
    }

    /**
     * 打印d级别的log
     *
     * @param tag
     * @param msg
     */
    public static void d(String tag, String msg) {
        Logger.log(Logger.DEBUG, tag, msg, null);
        saveSystemInfo2File(msg, tag, "debug");
    }

    /**
     * 方便打log
     *
     * @param object
     * @param msg
     */
    public static void d(Object object, String msg) {
        Logger.log(Logger.DEBUG, object.getClass().getSimpleName(), msg, null);
        saveSystemInfo2File(msg, object.getClass().getSimpleName(), "debug");
    }

    /**
     * 打印e级别的log
     *
     * @param tag
     * @param msg
     */
    public static void e(String tag, String msg) {
        Logger.log(Logger.ERROR, tag, msg, null);
        saveSystemInfo2File(msg, tag, "error");
    }

    /**
     * 方便打log
     *
     * @param object
     * @param msg
     */
    public static void e(Object object, String msg) {
        Logger.log(Logger.ERROR, object.getClass().getSimpleName(), msg, null);
        saveSystemInfo2File(msg, object.getClass().getSimpleName(), "error");
    }

    /**
     * 可以直接打印出json
     * 其实这个没有必要封装这一层因为没有在每一个activity里面去初始化新的TAG
     *
     * @param json
     */
    public static void json(String json) {
        Logger.json(json);
    }

    /**
     * 系统日志 写入文件
     *
     * @param message 写入内容
     * @return {@code true}: 写入成功<br>{@code false}: 写入失败
     */
    private final static String SYSTEM_LOG_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/kekefile/systemlogs/";
    public static boolean saveSystemInfo2File(String message, String tag, String level) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String fileName = SYSTEM_LOG_PATH + dateFormat.format(new Date()) + ".txt";
        File file = FileUtils.getFileByPath(fileName);
        if (file == null || TextUtils.isEmpty(message)) return false;
        if (!FileUtils.createOrExistsFile(file)) return false;
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file, true));
            bw.write("datetime:" + TimeUtils.millis2String(System.currentTimeMillis()) + "\n");
            bw.write("tag:" + tag + "\n");
            bw.write("level:" + level + "\n");
            bw.write("msg:" + message + "\n\n");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            CloseUtils.closeIO(bw);
        }
    }
}
