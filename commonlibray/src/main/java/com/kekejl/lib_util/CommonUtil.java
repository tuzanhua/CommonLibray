package com.kekejl.lib_util;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 作者：Charles(zqikai) on 2016/7/17 11:57
 * <p/>
 * 邮箱：charleszhuqikai@foxmail.com
 * <p/>
 * 类描述:常用的工具类
 */
public class CommonUtil {

    private static final String TAG = "CommonUtil";
    private static final int STATE_APROVE_PASS = 1;
    private static Context mContext;
    private static SpannableStringBuilder spannableString;

    public static void init(Context context) {
        mContext = context;
    }

    /**
     * 判断身份证是不是合理
     */
    public static boolean vIDNumByRegex(String idNum) {
        String curYear = "" + Calendar.getInstance().get(Calendar.YEAR);
        int y3 = Integer.valueOf(curYear.substring(2, 3));
        int y4 = Integer.valueOf(curYear.substring(3, 4));
        // 43 0103 1973 0 9 30 051 9
        return idNum.matches("^(1[1-5]|2[1-3]|3[1-7]|4[1-6]|5[0-4]|6[1-5]|71|8[1-2])\\d{4}(19\\d{2}|20([0-" + (y3 - 1) + "][0-9]|" + y3 + "[0-" + y4
                + "]))(((0[1-9]|1[0-2])(0[1-9]|[1-2][0-9]|3[0-1])))\\d{3}([0-9]|x|X)$");
        // 44 1825 1988 0 7 1 3 003 4
    }

    private static int ID_LENGTH = 17;

    public static boolean vIDNumByCode(String idNum) {
        // 系数列表
        int[] ratioArr = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};

        // 校验码列表
        char[] checkCodeList = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};

        // 获取身份证号字符数组
        char[] cIds = idNum.toCharArray();

        // 获取最后一位（身份证校验码）
        char oCode = cIds[ID_LENGTH];
        int[] iIds = new int[ID_LENGTH];
        int idSum = 0;// 身份证号第1-17位与系数之积的和
        int residue;// 余数(用加出来和除以11，看余数是多少？)

        for (int i = 0; i < ID_LENGTH; i++) {
            iIds[i] = cIds[i] - '0';
            idSum += iIds[i] * ratioArr[i];
        }

        residue = idSum % 11;// 取得余数

        return Character.toUpperCase(oCode) == checkCodeList[residue];
    }

    /**
     * 这是判断身份证合理不合理的方法
     */
    public static boolean vId(String idNum) {
        return vIDNumByCode(idNum) && vIDNumByRegex(idNum);
    }

    /**
     * 将自己重父view中移除
     *
     * @param childView
     */
    public static void removeSelfFromParent(View childView) {
        if (childView != null) {
            ViewParent parent = childView.getParent();
            if (parent instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) parent;
                group.removeView(childView);//移除子view
            }
        }
    }

    /**
     * 将图片转成2进制数组
     *
     * @param bmp
     * @param needRecycle
     * @return
     */
    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        LogUtil.e(TAG, "bmp的大小: " + bmp.getByteCount() + "图片的宽度" + bmp.getHeight() + "高度" + bmp.getHeight() + "config" + bmp.getConfig());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        LogUtil.e(TAG, "output.size():" + output.size());

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        LogUtil.e(TAG, "图片流的大小" + result.length);
        return result;
    }

    /**
     * 随机生成三位随机数
     *
     * @return
     */
    public static String generateThreeRandom() {
        Random random = new Random();
        int num = random.nextInt(10);
        String s = String.valueOf(num) + random.nextInt(10) + random.nextInt(10);
        return s;
    }

    /**
     * 获取当前app版本名
     */
    public static String getVersionName(Context mContext) {
        // modify by suxm on 2016/12/16
        String version = "";
        try {
            PackageManager manager = mContext.getPackageManager();
            PackageInfo info;
            info = manager.getPackageInfo(mContext.getPackageName(), 0);
            version = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    public static int getVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        int versionCode = 0;
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static int getWoshipinVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        int versionCode = 0;
        try {
            packageInfo = packageManager.getPackageInfo("com.unicom.woshipin", 0);
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static String getWoshipinVersionName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        String versionName = "";
        try {
            packageInfo = packageManager.getPackageInfo("com.unicom.woshipin", 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static int getCarlifeVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        int versionCode = 0;
        try {
            packageInfo = packageManager.getPackageInfo("com.kkjl.carlife", 0);
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取进程名
     *
     * @return
     */
    public static String getProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取当前时间精确到秒
     *
     * @return
     */
    public static String getCurrentTime() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        String format = simpleDateFormat.format(date);
        return format;
    }

    public static String getLocationTime() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        String format = simpleDateFormat.format(date);
        return format;
    }

    public static String getLocationTime(long ts) {
        Date date = new Date(ts);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        String format = simpleDateFormat.format(date);
        return format;
    }

    //生成随机数字和字母,
    public static String getStringRandom(int length) {

        String val = "";
        Random random = new Random();
        //length为几位密码
        for (int i = 0; i < length; i++) {
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            //输出字母还是数字
            if ("char".equalsIgnoreCase(charOrNum)) {
                //输出是大写字母还是小写字母
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val += (char) (random.nextInt(26) + temp);
            } else if ("num".equalsIgnoreCase(charOrNum)) {
                val += String.valueOf(random.nextInt(10));
            }
        }
        return val;
    }


    /**
     * 判断是否有网络链接
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        LogUtil.e(TAG, System.currentTimeMillis() + "isNetworkConnected");
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                LogUtil.e(TAG, System.currentTimeMillis() + "isNetworkConnected");
                return mNetworkInfo.isAvailable();
            }
        }
        LogUtil.e(TAG, System.currentTimeMillis() + "isNetworkConnected");
        return false;
    }


    /**
     * 判断应用是否运行在前台
     *
     * @return
     */
    public static boolean isRunningForeground(Context context) {
        String packageName = getPackageName(context);
        String topActivityClassName = getTopActivityName(context);
        LogUtil.e(TAG, "packageName=" + packageName + ",topActivityClassName=" + topActivityClassName);
        if (packageName != null && topActivityClassName != null && topActivityClassName.startsWith(packageName)) {
            LogUtil.e(TAG, "---> isRunningForeGround");
            return true;
        } else {
            LogUtil.e(TAG, "---> isRunningBackGround");
            return false;
        }
    }

    public static SpannableString getSizeText(Context context, String str) {
        SpannableString spStr = new SpannableString(str);
        spStr.setSpan(new AbsoluteSizeSpan(sp2px(context, 12)), 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spStr;
    }

    /**
     * sp2px
     *
     * @param context
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获取程序的包名
     *
     * @param context
     * @return
     */
    public static String getPackageName(Context context) {
        String packageName = context.getPackageName();
        return packageName;
    }

    private static DecimalFormat decimalFormat;

    /**
     * 小数格式化,保留两位小数
     */
    public static DecimalFormat getDecimalFormat() {
        if (decimalFormat == null)
            decimalFormat = new DecimalFormat("##0.00");
        return decimalFormat;
    }

    /**
     * 判断金额是否合法
     *
     * @param money 金额
     * @return
     */
    public static boolean isLegalMoney(String money) {
        Pattern pattern = Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$"); // 判断小数点后2位的数字的正则表达式
        Matcher match = pattern.matcher(money);
        return match.matches();
    }

    /**
     * 获取栈顶activity的全类名
     *
     * @param context
     * @return
     */
    public static String getTopActivityName(Context context) {
        String topActivityClassName = null;
        ActivityManager activityManager =
                (ActivityManager) (context.getSystemService(android.content.Context.ACTIVITY_SERVICE));
        //android.app.ActivityManager.getRunningTasks(int maxNum)
        //int maxNum--->The maximum number of entries to return in the list
        //即最多取得的运行中的任务信息(RunningTaskInfo)数量
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1);
        if (runningTaskInfos != null) {
            ComponentName f = runningTaskInfos.get(0).topActivity;
            topActivityClassName = f.getClassName();

        }
        //按下Home键盘后 topActivityClassName=com.android.launcher2.Launcher
        return topActivityClassName;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * 判断手机号码是否合理
     *
     * @param phoneNums
     */
    public static boolean judgePhoneNums(String phoneNums) {


        if (isMatchLength(phoneNums, 11) && isMobileNO(phoneNums)) {
            return true;
        }
        //ToastUtil.showToast("请输入正确的手机号");
        return false;

    }

    /**
     * 判断一个字符串的位数
     *
     * @param str
     * @param length
     * @return
     */
    public static boolean isMatchLength(String str, int length) {
        return !TextUtils.isEmpty(str) && (str.length() == length);
    }

    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobileNums) {
        /*
         * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 */
        String telRegex = "[1][34578]\\d{9}";
        // "[1]"代表第1位为数字1，"[3578]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        return !TextUtils.isEmpty(mobileNums) && mobileNums.matches(telRegex);
    }


    /**
     * 密码的判断根据实际情况修改
     *
     * @param pwd
     * @return
     */
    public static boolean judgePassword(String pwd) {
        String regex = "(\\w){6,16}";
        if (pwd != null) {
            return pwd.matches(regex);
        }
        return false;
    }

    /**
     * 根据路径获取图片并压缩
     */
    public static Bitmap getImage(String srcPath, Context context) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap;//此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
//        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
//        float hh = 800f;//这里设置高度为800f
//        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > DensityUtil.getScreenW(context)) {//如果宽度大的话根据宽度固定大小缩放
            be = newOpts.outWidth / DensityUtil.getScreenW(context);
        } else if (w < h && h > DensityUtil.getScreenH(context)) {//如果高度高的话根据宽度固定大小缩放
            be = newOpts.outHeight / DensityUtil.getScreenH(context);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }

    /**
     * 再次压缩图片,按照图片质量压缩
     */
    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //add by zqikai 20160509 for nullpointException
        if (image == null)
            return image;
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            if (options > 10) {
                options -= 10;//每次都减少10
            } else {
                break;
            }
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中

        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(isBm, null, null);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            bitmap = image;
        }
        return bitmap;
    }


    /**
     * 获取当前的时间精确到day
     *
     * @return
     */
    public static String getCurrentDay() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String format = simpleDateFormat.format(date);
        return format;
    }

    /**
     * 传入具体的日期，进行格式化
     *
     * @param date
     * @return
     */
    public static String getCurrentDay(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String format = simpleDateFormat.format(date);
        return format;
    }

    /**
     * 初始化文件
     *
     * @param CACHE_DIR
     * @return
     */
    public static boolean initDataFolder(String CACHE_DIR) {
        boolean isExist;
        File file = new File(CACHE_DIR);
        //文件不存在
        //文件存在
        isExist = !(!file.exists() && !file.isDirectory()) || file.mkdirs();
        return isExist;
    }


    /**
     * 手机号隐藏中间四位
     *
     * @param phone
     * @return
     */
    public static String phoneHide(String phone) {
        if (phone != null && phone.length() >= 11) {
            return phone.replace(phone.substring(3, 7), "****");
        } else {
            return "";
        }
    }


    /**
     * 隐藏车牌号后三位
     *
     * @param carNo
     * @return
     */
    public static String carLicenseHide(String carNo) {
        if (carNo == null)
            return carNo;
        if (carNo.length() == 7) {
            return carNo.replace(carNo.substring(3, 7), "***");
        } else {
            return carNo;
        }
    }

    /**
     * @param i
     * @return
     */
    public static String formatNextMonth(int i) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, i);
        c.add(Calendar.HOUR, 48);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String nextMonth = dateFormat.format(c.getTime());
        return nextMonth;
    }


    /**
     * 格式化小数四舍五入
     *
     * @param src   原始小数
     * @param count 保留的位数
     * @return
     */
    public static double formatDecimal(double src, double count) {
        double d = Math.round(src * Math.pow(10, count)) / Math.pow(10, count);
        //String format = getDecimalFormat().format(d);
        return d;
    }

    public static String formatDecimalStr(double src, double count) {
        double d = formatDecimal(src, count);
        String format = getDecimalFormat().format(d);
        return format;
    }

    public static void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public static String exChange(String str) {
        StringBuffer sb = new StringBuffer();
        if (str != null) {
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                if (Character.isLowerCase(c)) {
                    sb.append(Character.toUpperCase(c));
                } else {
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }


    public static boolean judgeCode(String code) {
        return !TextUtils.isEmpty(code) && code.trim().length() == 4;
    }

    public static Date strigToDate(String dateString) {
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            if (!TextUtils.isEmpty(dateString)) {
                date = sdf.parse(dateString);
            } else {
                date = sdf.parse(CommonUtil.getCurrentDay());
            }
        } catch (ParseException e) {
            e.printStackTrace();
            date = null;
        } finally {
            return date;
        }
    }

    /**
     * 在主线程执行任务
     *
     * @param r
     */
    public static void runOnUIThread(Handler handler, Runnable r) {
        handler.post(r);
    }

    public static boolean isMailAddressLegal(String mailAddress) {
        String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(mailAddress);
        return matcher.matches();
    }

    public static boolean isVinCode(String strVinCode) {
        //车辆识别代码 17位数字和英文
        String check = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{17}$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(strVinCode);
        return matcher.matches();
    }

    public static void setInputFilterForSpace(EditText editText) {
        editText.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        //返回null表示接收输入的字符,返回空字符串表示不接受输入的字符
                        if (source.equals(" "))
                            return "";
                        else
                            return null;
                    }
                }
        });
    }

    public static InputFilter getSpaceInputFilter() {
        return new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                //返回null表示接收输入的字符,返回空字符串表示不接受输入的字符
                if (source.equals(" ") || source.equals("*"))
                    return "";
                else
                    return null;
            }
        };
    }

    /**
     * @param string 需要改变的整个字符串
     * @return
     */
    public static SpannableStringBuilder changeStringColor(String string) {
        if (TextUtils.isEmpty(string)) {
            SpannableStringBuilder spannableString = new SpannableStringBuilder("");
            return spannableString;
        }
        try {

            int startIndex = string.indexOf("{");
            int endIndex = string.lastIndexOf("}");
            String desc1 = string.substring(0, startIndex);
            String desc2 = string.substring(startIndex + 2, endIndex);
            String desc3 = string.substring(endIndex + 1, string.length());
            String str = desc1 + desc2 + desc3;
            spannableString = new SpannableStringBuilder(str);
            if (!TextUtils.isEmpty(desc2)) {
                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FF0000")), str.indexOf(desc2), str.indexOf(desc2) + desc2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                spannableString = new SpannableStringBuilder(desc1 + desc3);
            }
        } catch (Exception ex) {
            spannableString = new SpannableStringBuilder(string);
            ex.printStackTrace();
        }
        return spannableString;
    }


    /**
     * 打开指定包名的App
     *
     * @param context 上下文
     * @return {@code true}: 打开成功<br>{@code false}: 打开失败
     */
    public static boolean openAppByPackageName(Context context) {
        Intent intent = getIntentByPackageName(context);
        if (intent != null) {
            context.startActivity(intent);
            return true;
        }
        return false;
    }

    /**
     * 根据包名获取意图
     *
     * @param context 上下文
     * @return 意图
     */
    public static Intent getIntentByPackageName(Context context) {

        return context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
    }

    public static String getFileNameWithoutSuffix(String filepath) {
        File f = new File(filepath);
        String fileName = f.getName();
        String shortName = fileName.substring(0, fileName.lastIndexOf("."));
        return shortName;
    }

    public static String formateDate(long ts) {
        Date date = new Date(ts);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd.HHmmss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return simpleDateFormat.format(date);

    }


    public static boolean saveBitmap(Bitmap bitmap, String dir, String fileName) {
        if (bitmap == null || TextUtils.isEmpty(fileName) || TextUtils.isEmpty(dir)) {
            return false;
        }
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        int quality = 100;
        OutputStream stream = null;
        try {
            stream = new FileOutputStream(dir + fileName);
        } catch (FileNotFoundException ex) {
            return false;
        }
        return bitmap.compress(format, quality, stream);
    }
}
