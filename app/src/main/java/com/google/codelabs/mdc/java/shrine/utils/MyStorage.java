package com.google.codelabs.mdc.java.shrine.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * class lưu trữ và lấy data của sharePreference
 */
public class MyStorage {

    private final SharedPreferences prefs;

    public MyStorage(Context context) {
        prefs = context.getSharedPreferences(Constant.SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public void save(String key, String value) {
        prefs.edit().putString(key, value).apply();
    }

    public String get(String key) {
        return prefs.getString(key,"");
    }
}
