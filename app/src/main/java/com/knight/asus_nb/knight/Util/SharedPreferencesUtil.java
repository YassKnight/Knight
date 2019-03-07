package com.knight.asus_nb.knight.Util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtil {
    private static Activity mActivity;
    private static SharedPreferencesUtil Instance;
    private static SharedPreferences sharedPreferences;
    private SharedPreferencesUtil(){

    }

    /**
     * 获取示例
     * @param activity
     * @return
     */
    public static SharedPreferencesUtil getInstance(Activity activity){
        mActivity = activity;
        if (Instance == null){
            Instance = new SharedPreferencesUtil();
            sharedPreferences = activity.getSharedPreferences("set",Context.MODE_PRIVATE);
        }
        return Instance;
    }

    /**
     * 保存数据到本地
     * @param key 键值
     * @param value 值
     */
    public static void SaveData(String key,String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,value);
        editor.apply();
    }

    /**
     * 获取本地数据
     * @param key  键值
     * @param defluate 默认值
     * @return
     */
    public static String GetData(String key,String defluate){
         return sharedPreferences.getString(key,defluate);
    }
}
