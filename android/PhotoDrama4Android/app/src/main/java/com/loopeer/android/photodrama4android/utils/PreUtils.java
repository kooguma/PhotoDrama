package com.loopeer.android.photodrama4android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreUtils {

    public static final String PREF_VERSION = "pref_version";

    public static String getVersion(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_VERSION, "");
    }

    public static void putVersion(Context context, String splashUrl) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_VERSION, splashUrl).apply();
    }


}