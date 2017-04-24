package com.loopeer.android.photodrama4android.analytics;

import java.util.HashMap;
import java.util.Map;
import com.loopeer.android.photodrama4android.analytics.Event.Key;

public class Analyst {

    private static Analytics sAnalytics;

    public static void init(Analytics analytics) {
        if (sAnalytics == null) {
            sAnalytics = analytics;
        }
    }

    public static void homeDramaClick() {
        sAnalytics.logEvent(Key.HomeDramaClick);
    }

    public static void homeSettingClick() {
        sAnalytics.logEvent(Key.HomeSettingClick);
    }

    public static void dramaCategoryClick(String categoryId) {
        sAnalytics.logEvent(Key.DramaCategoryClick, idParam(Event.Param.CATEGORY_ID, categoryId));
    }

    public static void dramaDetailClick(String themeId) {
        sAnalytics.logEvent(Key.DramaDetailClick, idParam(Event.Param.THEME_ID, themeId));
    }

    public static void dramaUseClick(String themeId) {
        sAnalytics.logEvent(Key.DramaUseClick, idParam(Event.Param.THEME_ID, themeId));
    }

    public static void downloadClick() {
        sAnalytics.logEvent(Key.DownloadClick);
    }

    public static void settingSuggestClick() {
        sAnalytics.logEvent(Key.SettingSuggestClick);
    }

    public static void settingCacheClick() {
        sAnalytics.logEvent(Key.SettingCacheClick);
    }

    public static void settingAboutUsClick() {
        sAnalytics.logEvent(Key.SettingAboutUsClick);
    }

    public static void suggestSubmitClick() {
        sAnalytics.logEvent(Key.SuggestSubmitClick);
    }

    public static void shareQQClick() {
        sAnalytics.logEvent(Key.ShareQQClick);
    }

    public static void shareWeChatClick() {
        sAnalytics.logEvent(Key.ShareWeChatClick);
    }

    public static void shareMoreClick() {
        sAnalytics.logEvent(Key.ShareMoreClick);
    }

    public static void shareBackHomeClick() {
        sAnalytics.logEvent(Key.ShareBackHomeClick);
    }

    private static Map<String, String> idParam(String key, String value) {
        Map<String, String> param = new HashMap<>();
        param.put(key, value);
        return param;
    }
}
