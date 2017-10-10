package com.kekejl.lib_util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * author：tzh on 2017/1/4 16:03
 * contact information: 188****5816
 * company :可可家里(北京)信息技术有限公司
 * describe:
 * modify instructions:
 */
public class SpUtils {
    private static final String APP_SETTING = "appSetting_pad";
    private static final String USER_INFO = "userInfo_pad";
    private static Context mContext;

    public static void init(Context context) {
        mContext = context;
    }

    public static void put(String key, Object object) {
        SharedPreferences sp = mContext.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (object == null)
            return;
        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param key
     * @param defaultObject
     * @return
     */
    public static <T extends Object> T get(String key, T defaultObject) {
        SharedPreferences sp = mContext.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE);
        if (defaultObject instanceof String) {
            return (T) sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return (T) (Integer) sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return (T) (Boolean) sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return (T) (Float) sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return (T) (Long) sp.getLong(key, (Long) defaultObject);
        }
        return null;
    }

    public static void putAppSetting(String key, Object object) {
        SharedPreferences sp = mContext.getSharedPreferences(APP_SETTING, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (object == null)
            return;

        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param key
     * @param defaultObject
     * @return
     */
    public static <T extends Object> T getAppSetting(String key, T defaultObject) {
        SharedPreferences spAppsetting = mContext.getSharedPreferences(APP_SETTING, Context.MODE_PRIVATE);
        if (defaultObject instanceof String) {
            return (T) spAppsetting.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return (T) (Integer) spAppsetting.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return (T) (Boolean) spAppsetting.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return (T) (Float) spAppsetting.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return (T) (Long) spAppsetting.getLong(key, (Long) defaultObject);
        }
        return null;
    }

    /**
     * 保存应用配置的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     */
    public static void putAppSetting(HashMap<String, Object> entrys) {
        SharedPreferences sp = mContext.getSharedPreferences(APP_SETTING,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        Iterator<Map.Entry<String, Object>> iter = entrys.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<String, Object> set = iter.next();
            String key = set.getKey();
            Object val = set.getValue();
            if (val instanceof String) {
                editor.putString(key, (String) val);
            } else if (val instanceof Integer) {
                editor.putInt(key, (Integer) val);
            } else if (val instanceof Long) {
                editor.putLong(key, (Long) val);
            } else if (val instanceof Boolean) {
                editor.putBoolean(key, (Boolean) val);
            } else if (val instanceof Float) {
                editor.putFloat(key, (Float) val);
            }
        }

        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 清除所有数据
     */
    public static void clear() {
        SharedPreferences sp = mContext.getSharedPreferences(USER_INFO,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        SharedPreferencesCompat.apply(editor);
    }


    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     *
     * @author zhy
     */
    private static class SharedPreferencesCompat {
        private static final Method sApplyMethod = findApplyMethod();

        /**
         * 反射查找apply的方法
         *
         * @return
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        private static Method findApplyMethod() {
            try {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException e) {
            }

            return null;
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         *
         * @param editor
         */
        public static void apply(SharedPreferences.Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }
            editor.commit();
        }
    }

    public static <T> T getValue(String key, Class<T> clazz) {
        /*if (context == null) {
            throw new RuntimeException("请先调用带有context，name参数的构造！");
        }*/
        SharedPreferences sp = mContext.getSharedPreferences(APP_SETTING, Context.MODE_PRIVATE);
        return getValue(key, clazz, sp);
    }

    /**
     * 针对复杂类型存储<对象>
     *
     * @param key
     * @param object
     */
    public static void setObject(String key, Object object) {

        //创建字节输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //创建字节对象输出流
        ObjectOutputStream out = null;
        try {
            //然后通过将字对象进行64转码，写入key值为key的sp中
            out = new ObjectOutputStream(baos);
            out.writeObject(object);
            String objectVal = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
            SharedPreferences sp = mContext.getSharedPreferences(USER_INFO,
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(key, objectVal);
            editor.apply();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取存储的对象
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T getObject(String key, Class<T> clazz) {
        SharedPreferences sp = mContext.getSharedPreferences(USER_INFO,
                Context.MODE_PRIVATE);
        if (sp.contains(key)) {
            String objectVal = sp.getString(key, null);
            byte[] buffer = Base64.decode(objectVal, Base64.DEFAULT);
            //一样通过读取字节流，创建字节流输入流，写入对象并作强制转换
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(bais);
                T t = (T) ois.readObject();
                return t;
            } catch (StreamCorruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bais != null) {
                        bais.close();
                    }
                    if (ois != null) {
                        ois.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 对于外部不可见的过渡方法
     *
     * @param key
     * @param clazz
     * @param sp
     * @return
     */
    @SuppressWarnings("unchecked")
    private static <T> T getValue(String key, Class<T> clazz, SharedPreferences sp) {
        T t;
        try {

            t = clazz.newInstance();

            if (t instanceof Integer) {
                return (T) Integer.valueOf(sp.getInt(key, 0));
            } else if (t instanceof String) {
                return (T) sp.getString(key, "");
            } else if (t instanceof Boolean) {
                return (T) Boolean.valueOf(sp.getBoolean(key, false));
            } else if (t instanceof Long) {
                return (T) Long.valueOf(sp.getLong(key, 0L));
            } else if (t instanceof Float) {
                return (T) Float.valueOf(sp.getFloat(key, 0L));
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
            LogUtil.e("system", "类型输入错误或者复杂类型无法解析[" + e.getMessage() + "]");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            LogUtil.e("system", "类型输入错误或者复杂类型无法解析[" + e.getMessage() + "]");
        }
        LogUtil.e("system", "无法找到" + key + "对应的值");
        return null;
    }
}
