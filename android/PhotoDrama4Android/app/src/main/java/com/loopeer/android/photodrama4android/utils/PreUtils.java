package com.loopeer.android.photodrama4android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.google.gson.Gson;
import com.loopeer.android.photodrama4android.model.Advert;
import com.loopeer.android.photodrama4android.utils.gson.GsonHelper;

public class PreUtils {

    public static final String PREF_VERSION = "pref_version";
    public static final String PREF_ADVERT = "pref_advert";

    public static String getVersion(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_VERSION, "");
    }

    public static void putVersion(Context context, String splashUrl) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_VERSION, splashUrl).apply();
    }

    public static void setAdvert(Context context, Advert advert) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String json = GsonHelper.getDefault().toJson(advert);
        sp.edit().putString(PREF_ADVERT, json).apply();
    }

    public static String getAdvert(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_ADVERT, "");
    }

}