package com.tonyhu.location.util;


import android.content.Context;
import android.content.SharedPreferences;

import com.tonyhu.location.TonyLocationApplication;

/**
 * Created by Administrator on 2017/3/23.
 */

public class PreferenceUtil {
    private static SharedPreferences pref = TonyLocationApplication.getContext().getSharedPreferences("config", Context.MODE_PRIVATE);

    public static void putString(String key,String value) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key,value);
        editor.apply();
    }

    public static String getString(String key) {
        return pref.getString(key,"");
    }
}
