package com.kekejl.lib_util;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * author：tzh on 2017/1/4 16:45
 * contact information: 188****5816
 * company :可可家里(北京)信息技术有限公司
 * describe:
 * modify instructions:
 */
public class MyLog {
    private static char MYLOG_TYPE = 'v';// 输入日志类型，w代表只输出告警信息等，v代表输出所有信息
    private static int SDCARD_LOG_FILE_SAVE_DAYS = 0;// sd卡中日志文件的最多保存天数
    //    private static String MYLOGFILEName = "Log.txt";// 本类输出的日志文件名称
    private static boolean MYLOG_SWITCH = false;
    private static Boolean MYLOG_WRITE_TO_FILE = false;//日志写入文件开关
    private static SimpleDateFormat myLogSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 日志的输出格式
    private static SimpleDateFormat logfile = new SimpleDateFormat("yyyy-MM-dd");// 日志文件格式


    public static void init(boolean isDebug) {
        MYLOG_SWITCH = isDebug;
        MYLOG_WRITE_TO_FILE = isDebug;
    }

    /**
     * 根据tag, msg和等级，输出日志
     *
     * @param tag
     * @param msg
     * @param level
     * @return void
     * @since v 1.0
     */
    private static void log(String tag, String msg, char level, String CACHE_DIR, String fileName) {
        if (MYLOG_SWITCH) {
            if ('e' == level && ('e' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) { // 输出错误信息
                Log.e(tag, msg);
            } else if ('w' == level && ('w' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) {
                Log.w(tag, msg);
            } else if ('d' == level && ('d' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) {
                Log.d(tag, msg);
            } else if ('i' == level && ('d' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) {
                Log.i(tag, msg);
            } else {
                Log.v(tag, msg);
            }
            if (MYLOG_WRITE_TO_FILE) writeLogtoFile(String.valueOf(level), tag, msg, CACHE_DIR, fileName);
        }
    }

    /**
     * 打开日志文件并写入日志
     *
     * @return
     **/
    private static void writeLogtoFile(String mylogtype, String tag, String text, String CACHE_DIR, String fileName) {// 新建或打开日志文件
        Date nowtime = new Date();
        String needWriteFiel = logfile.format(nowtime);
        String needWriteMessage = myLogSdf.format(nowtime) + "    " + mylogtype + "    " + tag + "    " + text;
        File dir = new File(CACHE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(CACHE_DIR, needWriteFiel + fileName);
        try {
            FileWriter filerWriter = new FileWriter(file, true);//后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
            BufferedWriter bufWriter = new BufferedWriter(filerWriter);
            bufWriter.write(needWriteMessage);
            bufWriter.newLine();
            bufWriter.close();
            filerWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除制定的日志文件
     */
    public static void delFile(String CACHE_DIR, String fileName) {// 删除日志文件
        String needDelFiel = logfile.format(getDateBefore());
        File file = new File(CACHE_DIR, needDelFiel + fileName);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 得到现在时间前的几天日期，用来得到需要删除的日志文件名
     */
    private static Date getDateBefore() {
        Date nowtime = new Date();
        Calendar now = Calendar.getInstance();
        now.setTime(nowtime);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - SDCARD_LOG_FILE_SAVE_DAYS);
        return now.getTime();
    }


    public static void writeLogtoFile(String text, String CACHE_DIR, String fileName) {// 新建或打开日志文件
        Date nowtime = new Date();
        String needWriteFiel = logfile.format(nowtime);
//        String needWriteMessage = myLogSdf.format(nowtime) + "    " + mylogtype
//                + "    " + tag + "    " + text;
        File dir = new File(CACHE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(CACHE_DIR, needWriteFiel + fileName);
        try {
            FileWriter filerWriter = new FileWriter(file, true);//后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
            BufferedWriter bufWriter = new BufferedWriter(filerWriter);
            bufWriter.write(text);
            bufWriter.newLine();
            bufWriter.close();
            filerWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
