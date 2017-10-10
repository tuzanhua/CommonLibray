package com.kekejl.lib_util;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.Timer;
import java.util.TimerTask;

/**
 * author：tzh on 2017/1/4 16:42
 * contact information: 188****5816
 * company :可可家里(北京)信息技术有限公司
 * describe:
 * modify instructions:
 */
public class KeyboardUtils {
    /**
     * 获取输入法状态
     *
     * @param edittext
     * @return
     */
    public static boolean getKeyBoardStatu(EditText edittext)
    {
        InputMethodManager imManager = (InputMethodManager) edittext
                .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        return imManager.isActive();
    }

    /**
     * 修改输入法状态
     *
     * @param context
     */
    public static void changeKeyBoardStatu(Context context)
    {
        InputMethodManager imManager = (InputMethodManager) context
                .getApplicationContext().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
        imManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 隐藏输入法
     *
     * @param editText
     */
    public static void hideKeyboard(View editText)
    {
        InputMethodManager imManager = (InputMethodManager) editText
                .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imManager.isActive())
        {
            imManager.hideSoftInputFromWindow(
                    editText.getApplicationWindowToken(), 0);
        }
    }

    public static void hideKeyboardDelay(final View view){
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideKeyboard(view);
            }
        }, 150);
    }

    /**
     * 显示输入法
     *
     * @param editText
     */
    public static void showKeyboard(View editText)
    {
        InputMethodManager imManager = (InputMethodManager) editText
                .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imManager.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
    }

    // 强制显示或者关闭系统键盘
    public static void keyBoard(final EditText txtSearchKey, final String status)
    {

        Timer timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                InputMethodManager m = (InputMethodManager) txtSearchKey
                        .getContext().getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                if (status.equals("open"))
                {
                    m.showSoftInput(txtSearchKey,
                            InputMethodManager.SHOW_FORCED);
                }
                else
                {
                    m.hideSoftInputFromWindow(txtSearchKey.getWindowToken(), 0);
                }
            }
        }, 300);
    }

    // 通过定时器强制隐藏虚拟键盘
    public static void timerHideKeyboard(final View editText)
    {
        Timer timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                InputMethodManager imManager = (InputMethodManager) editText
                        .getContext().getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                if (imManager.isActive())
                {
                    imManager.hideSoftInputFromWindow(
                            editText.getApplicationWindowToken(), 0);
                }
            }
        }, 10);
    }

    /**
     * 延时显示输入法
     *
     * @param mHandler
     * @param time
     * @param editText
     */
    public static void openKeyboard(Handler mHandler, int time,
                                    final View editText)
    {
        mHandler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                InputMethodManager imManager = (InputMethodManager) editText
                        .getContext().getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                imManager
                        .toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, time);
    }




}
