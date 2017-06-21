package com.loopeer.android.photodrama4android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreUtils {

    public static final String PREF_VERSION = "pref_version";
    public static final String PREF_ADVERT_URL = "pref_advert_url";

    public static String getVersion(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_VERSION, "");
    }

    public static void putVersion(Context context, String splashUrl) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_VERSION, splashUrl).apply();
    }

    public static void setAdvertUrl(Context context, String url) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_ADVERT_URL, url).apply();
    }

    public static String getAdvertUrl(Context context, String url) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_ADVERT_URL,url);
    }

}