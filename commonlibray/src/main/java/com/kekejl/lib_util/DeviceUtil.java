package com.kekejl.lib_util;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.UUID;

/**
 * create by zqikai 20160525
 * 获取设备参数的类
 */
public class DeviceUtil {

    public static final String UTSDK_LOG_TAG = "UTSDK[MobileTracking]";

    /**
     * UTSDK_LOG_DEBUG
     */
    public static boolean UTSDK_LOG_DEBUG = false;

    /**
     * UTSDK_LOG_PARAM_NULL_VALUE
     */
    public static final String UTSDK_LOG_PARAM_NULL_VALUE = "NULL";

    //统计获取的参数
   /* public static final String IMEI = getIMEI();
    public static final String MAC = getWLANMAC();
    public static final String AndroidUNID = getAndroidId();*/
    private static Context mContext;

    public static void init(Context context) {
        mContext = context;
    }

    /**
     * 获取操作系统
     *
     * @return
     */
    public static String getOS() {
        return Build.VERSION.RELEASE;
    }

    //cc 1 The IMEI
    //only useful for Android Phone(android.permission.READ_PHONE_STATE in Manifest)
    public static String getIMEI() {
        String szImei = null;
        try {
            TelephonyManager TelephonyMgr = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            szImei = TelephonyMgr.getDeviceId();
        } catch (SecurityException e) {

        }
        return szImei;
    }

    public static String getPhoneNum() {
        String phonenum = "";
        try {
            TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            phonenum = telephonyManager.getLine1Number();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return phonenum;
    }

    public static String getICCID() {
        String ICCID = null;
        try {
            TelephonyManager TelephonyMgr = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            ICCID = TelephonyMgr.getSimSerialNumber();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(ICCID)) {
            ICCID = "";
        }
        return ICCID;
    }

    //cc 2 Pseudo-Unique ID
    //useful for phone/pad
    //通过取出ROM版本、制造商、CPU型号、以及其他硬件信息来实现这一点。这样计算出来的ID不是唯一的（因为如果两个手机应用了同样的硬件以及Rom 镜像）
    public static String getPUID() {
        String m_szDevIDShort = "35" + //make this look like a valid IMEI
                Build.BOARD.length() % 10 +
                Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 +
                Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 +
                Build.HOST.length() % 10 +
                Build.ID.length() % 10 +
                Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 +
                Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 +
                Build.TYPE.length() % 10 +
                Build.USER.length() % 10; //13 digits
        return m_szDevIDShort;
    }

    //cc 3 Android ID 获取Android设备的id
    //sometimes it will be null,cause this id can be changed by the manufacturer
    public static String getAndroidId() {
        String m_szAndroidID = Secure.getString(mContext.getContentResolver(), Secure.ANDROID_ID);
        return m_szAndroidID;
    }

    //cc 4 The WLAN MAC Address String
    //need android.permission.ACCESS_WIFI_STATE,or it will return null
    public static String getWLANMAC() {
        WifiManager wm = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        String m_szWLANMAC = wm.getConnectionInfo().getMacAddress();
        return m_szWLANMAC;
    }

    //cc 5 the BT MAC Address String
    //need android.permission.BLUETOOTH,or it will return null
    public static String getBTMAC() {
        //add by zqikai 20160418 for crash
        try {
            BluetoothAdapter m_BluetoothAdapter; // Local Bluetooth adapter
            m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            String m_szBTMAC = m_BluetoothAdapter.getAddress();
            return m_szBTMAC;
        } catch (NullPointerException e) {
            return "";
        }
    }

    //cc Combined Device ID
    public static String getCombinedId() {
        try {
            String szImei = getIMEI();
            String m_szDevIDShort = getPUID();
            String m_szAndroidID = getAndroidId();
            //		String m_szWLANMAC=getWLANMAC();
            String m_szWLANMAC = getMacAddress(mContext, getNetworkType(mContext));
            String m_szBTMAC = getBTMAC();
            String m_szLongID = szImei + m_szDevIDShort + m_szAndroidID + m_szWLANMAC + m_szBTMAC;
            LogUtil.e("szImei", "cccc|" + szImei);
            LogUtil.e("m_szDevIDShort", "cccc|" + m_szDevIDShort);
            LogUtil.e("m_szAndroidID", "cccc|" + m_szAndroidID);
            LogUtil.e("m_szWLANMAC", "cccc|" + m_szWLANMAC);
            LogUtil.e("m_szBTMAC", "cccc|" + m_szBTMAC);
            LogUtil.e("m_szLongID", "cccc|" + m_szLongID);
            // compute md5
            MessageDigest m = null;
            try {
                m = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            m.update(m_szLongID.getBytes(), 0, m_szLongID.length());
            // get md5 bytes
            byte p_md5Data[] = m.digest();
            // create a hex string
            String m_szUniqueID = "";
            for (int i = 0; i < p_md5Data.length; i++) {
                int b = (0xFF & p_md5Data[i]);
                // if it is a single digit, make sure it have 0 in front (proper padding)
                if (b <= 0xF)
                    m_szUniqueID += "0";
                // add number to string
                m_szUniqueID += Integer.toHexString(b);
            }   // hex string to uppercase
            m_szUniqueID = m_szUniqueID.toUpperCase();
            return m_szUniqueID;
        } catch (SecurityException e) {
            LogUtil.e("DeviceUtil", "由于用户没有授予权限,未取到唯一识别号");
            return getInstallationId();
        }
    }

    private static String sID = null;

    /**
     * 获取应用首次安装时的uuId,不能标示特定机型但可以标示新安装的应用.
     */
    public static String getInstallationId() {
        File installation = new File(mContext.getFilesDir(), "INSTALLATION");
        try {
            if (!installation.exists())
                writeInstallationFile(installation);
            sID = readInstallationFile(installation);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return sID;
    }


    private static String readInstallationFile(File installation) throws IOException {
        RandomAccessFile f = new RandomAccessFile(installation, "r");
        byte[] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        f.close();
        return new String(bytes);
    }

    private static void writeInstallationFile(File installation) throws IOException {
        FileOutputStream out = new FileOutputStream(installation);
        String id = UUID.randomUUID().toString();
        out.write(id.getBytes());
        out.close();
    }


    /**
     * get OS VS
     *
     * @return os info
     */
    public static String getOSVS() {
        int osvs = Build.VERSION.SDK_INT;
        return osvs > 0 ? (osvs + "") : UTSDK_LOG_PARAM_NULL_VALUE;
    }

    /**
     * 获取MAC地址
     *
     * @return MacAddress
     */
    public static String getMacAddress(Context context, String networkType) {
        String result = UTSDK_LOG_PARAM_NULL_VALUE;
        if (networkType != null && !networkType.equals(UTSDK_LOG_PARAM_NULL_VALUE) && networkType.equals("1")) {
            try {
                if (context.checkPermission(Manifest.permission.ACCESS_WIFI_STATE, android.os.Process.myPid(), android.os.Process.myUid()) == PackageManager.PERMISSION_GRANTED) {
                    WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    WifiInfo info = wifi.getConnectionInfo();
                    if (wifi.isWifiEnabled()) {
                        String mac = info.getMacAddress();
                        if (mac != null) {
                            result = mac.replace(":", "-").toLowerCase(Locale.CHINA);
                        }
                    }
                }
            } catch (Exception e) {
                if (UTSDK_LOG_DEBUG) LogUtil.e(UTSDK_LOG_TAG, e.getMessage());
                result = UTSDK_LOG_PARAM_NULL_VALUE;
            }
        } else {
            try {
                Process proc = Runtime.getRuntime().exec("busybox ifconfig");
                InputStreamReader is = new InputStreamReader(proc.getInputStream());
                BufferedReader br = new BufferedReader(is);
                //执行命令cmd，只取结果中含有filter的这一行
                String line;
                while ((line = br.readLine()) != null && line.contains("HWaddr") == false) {
                }
                result = line;
            } catch (Exception e) {
                if (UTSDK_LOG_DEBUG) LogUtil.e(UTSDK_LOG_TAG, e.getMessage());
                result = UTSDK_LOG_PARAM_NULL_VALUE;
            }
            //处理获取mac地址报空异常
            if (result == null) {
                result = "NULL";
            }
            if (result.length() > 0 && result.contains("HWaddr")) {
                String mac = result.substring(result.indexOf("HWaddr") + 6, result.length() - 1);
                if (mac.length() > 1) {
                    result = mac.replace(" ", "").toLowerCase(Locale.CHINA);
                } else {
                    result = UTSDK_LOG_PARAM_NULL_VALUE;
                }
            }
        }
        return result.trim();
    }

    /**
     * 判断Network具体类型（联通移动wap，电信wap，其他net）
     */
    public static String getNetworkType(Context context) {
        String stateCode = UTSDK_LOG_PARAM_NULL_VALUE;
        try {
            final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (context.checkPermission(Manifest.permission.ACCESS_NETWORK_STATE, android.os.Process.myPid(), android.os.Process.myUid()) == PackageManager.PERMISSION_GRANTED) {
                final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (connectivityManager != null && networkInfo != null && networkInfo.isConnected()
                        && networkInfo.isAvailable() && networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    // NetworkInfo不为null开始判断是网络类型
                    int netType = networkInfo.getType();
                    switch (networkInfo.getType()) {
                        case ConnectivityManager.TYPE_WIFI:
                            stateCode = "1";
                            break;
                        case ConnectivityManager.TYPE_MOBILE:
                            switch (networkInfo.getSubtype()) {
                                case TelephonyManager.NETWORK_TYPE_GPRS: //联通2g
                                case TelephonyManager.NETWORK_TYPE_CDMA: //电信2g
                                case TelephonyManager.NETWORK_TYPE_EDGE: //移动2g
                                case TelephonyManager.NETWORK_TYPE_1xRTT:
                                case TelephonyManager.NETWORK_TYPE_IDEN:
                                    stateCode = "2";
                                    break;
                                case TelephonyManager.NETWORK_TYPE_EVDO_A: //电信3g
                                case TelephonyManager.NETWORK_TYPE_UMTS:
                                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                                case TelephonyManager.NETWORK_TYPE_HSDPA:
                                case TelephonyManager.NETWORK_TYPE_HSUPA:
                                case TelephonyManager.NETWORK_TYPE_HSPA:
                                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                                case TelephonyManager.NETWORK_TYPE_EHRPD:
                                case TelephonyManager.NETWORK_TYPE_HSPAP:
                                    stateCode = "3";
                                    break;
                                case TelephonyManager.NETWORK_TYPE_LTE:
                                    stateCode = "4";
                                    break;
                                default:
                                    stateCode = "0";
                                    break;
                            }
                            break;
                        default:
                            stateCode = "0";
                            break;
                    }
                    if (netType == ConnectivityManager.TYPE_MOBILE && !stateCode.equals("4")) {
                        String netMode = networkInfo.getExtraInfo();
                        if (netMode != null) {
                            netMode = netMode.toLowerCase(Locale.CHINA);// 通过apn名称判断是否是联通和移动wap
                            if (netMode.equals("cmwap")) {
                                stateCode = stateCode.equals("3") ? "3" : "2";
                            } else if (netMode.equals("cmnet")) {
                                stateCode = stateCode.equals("3") ? "3" : "2";
                            } else if (netMode.equals("3gnet") || netMode.equals("uninet")) {
                                stateCode = stateCode.equals("3") ? "3" : "2";
                            } else if (netMode.equals("3gwap") || netMode.equals("uniwap")) {
                                stateCode = stateCode.equals("3") ? "3" : "2";
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            if (UTSDK_LOG_DEBUG) LogUtil.e(UTSDK_LOG_TAG, e.getMessage());
            stateCode = UTSDK_LOG_PARAM_NULL_VALUE;
        } finally {
            return stateCode;
        }
    }


    public static boolean checkPermission(Context context, String permission) {
        boolean result = false;
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                Class<?> clazz = Class.forName("android.content.Context");
                Method method = clazz.getMethod("checkSelfPermission", String.class);
                int rest = (Integer) method.invoke(context, permission);
                if (rest == PackageManager.PERMISSION_GRANTED) {
                    result = true;
                } else {
                    result = false;
                }
            } catch (Exception e) {
                result = false;
            }
        } else {
            PackageManager pm = context.getPackageManager();
            if (pm.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
                result = true;
            }
        }
        return result;
    }

    public static String getDeviceInfo(Context context) {
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String device_id = null;
            if (checkPermission(context, Manifest.permission.READ_PHONE_STATE)) {
                device_id = tm.getDeviceId();
            }
            String mac = null;
            FileReader fstream = null;
            try {
                fstream = new FileReader("/sys/class/net/wlan0/address");
            } catch (FileNotFoundException e) {
                fstream = new FileReader("/sys/class/net/eth0/address");
            }
            BufferedReader in = null;
            if (fstream != null) {
                try {
                    in = new BufferedReader(fstream, 1024);
                    mac = in.readLine();
                } catch (IOException e) {
                } finally {
                    if (fstream != null) {
                        try {
                            fstream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            json.put("mac", mac);
            if (TextUtils.isEmpty(device_id)) {
                device_id = mac;
            }
            if (TextUtils.isEmpty(device_id)) {
                device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),
                        android.provider.Settings.Secure.ANDROID_ID);
            }
            json.put("device_id", device_id);
            return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void rebootDevice() {
        ShellUtils.execCmd("reboot", true);
    }

    public static void screenShot() {
        ShellUtils.execCmd("/system/bin/screencap -p /sdcard/padManager" + System.currentTimeMillis() + "screenshot.png", true);
    }

    public static String getScreenStatus(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return powerManager.isScreenOn() ? "1" : "2";//  0.关机   1.开屏   2.熄屏

    }

    /**
     * get IP
     *
     * @param context
     * @return
     */
    public static String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return "";
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }
}
