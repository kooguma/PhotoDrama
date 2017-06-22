package com.loopeer.android.photodrama4android.analytics;

import java.security.PublicKey;
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

    public static void startPageADClick(String advertId){
        sAnalytics.logEvent(Key.StartPageADClick,idParam(Event.Param.ADVERT_ID,advertId));
    }

    public static void myStartListADClick(String advertId){
        sAnalytics.logEvent(Key.MyStartListADClick,idParam(Event.Param.ADVERT_ID,advertId));
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

    public static void homeMyCreatClick() {
        sAnalytics.logEvent(Key.HomeMyCreatClick);
    }

    public static void myStarDetailUseStoryClick(String themeId) {
        sAnalytics.logEvent(Key.MyStarDetailUseStoryClick, idParam(Event.Param.THEME_ID, themeId));
    }

    public static void myStarPlayClick() {
        sAnalytics.logEvent(Key.MyStarPlayClick);
    }

    public static void myCreatStartClick() {
        sAnalytics.logEvent(Key.MyCreatStartClick);
    }

    public static void myCreatTransferClick() {
        sAnalytics.logEvent(Key.MyCreatTransferClick);
    }

    public static void myCreatSubtitleClick() {
        sAnalytics.logEvent(Key.MyCreatSubtitleClick);
    }

    public static void myCreatDubbingClick() {
        sAnalytics.logEvent(Key.MyCreatDubbingClick);
    }

    public static void myCreatSoundtrackClick() {
        sAnalytics.logEvent(Key.MyCreatSoundtrackClick);
    }

    public static void myCreatSoundEffectClick() {
        sAnalytics.logEvent(Key.MyCreatSoundEffectClick);
    }

    public static void myCreatTimeLongClick() {
        sAnalytics.logEvent(Key.MyCreatTimeLongClick);
    }

    public static void myCreatDownloadClick() {
        sAnalytics.logEvent(Key.MyCreatDownloadClick);
    }

    public static void myCreatTransferSaveClick() {
        sAnalytics.logEvent(Key.MyCreatTransferSaveClick);
    }

    public static void myCreatSubtitleSaveClick() {
        sAnalytics.logEvent(Key.MyCreatSubtitleSaveClick);
    }

    public static void myCreatDubbingSaveClick() {
        sAnalytics.logEvent(Key.MyCreatDubbingSaveClick);
    }

    public static void myCreatSoundtrackAddClick() {
        sAnalytics.logEvent(Key.MyCreatSoundtrackAddClick);
    }

    public static void myCreatSoundtrackSaveClick() {
        sAnalytics.logEvent(Key.MyCreatSoundtrackSaveClick);
    }

    public static void myCreatSoundEffectAddClick() {
        sAnalytics.logEvent(Key.MyCreatSoundEffectAddClick);
    }

    public static void myCreatSoundEffectSaveClick() {
        sAnalytics.logEvent(Key.MyCreatSoundEffectSaveClick);
    }

    public static void myCreatTimeLongSaveClick() {
        sAnalytics.logEvent(Key.MyCreatTimeLongSaveClick);
    }

    public static void addMusicSoundtrackAddClick(String id) {
        sAnalytics.logEvent(Key.AddMusicSoundtrackAddClick, idParam(Event.Param.SOUND_TRACK_ID, id));
    }

    public static void addMusicSoundtrackDetailClick(String id) {
        sAnalytics.logEvent(Key.AddMusicSoundtrackDetailClick, idParam(Event.Param.SOUND_TRACK_ID, id));
    }

    public static void addMusicSoundtrackFeaturedClick(String id) {
        sAnalytics.logEvent(Key.AddMusicSoundtrackFeaturedClick, idParam(Event.Param.SOUND_TRACK_CATEGORY_ID, id));
    }

    public static void addMusicSoundtrackDownloadClic(String id) {
        sAnalytics.logEvent(Key.AddMusicSoundtrackDownloadClic, idParam(Event.Param.SOUND_TRACK_ID, id));
    }

    public static void addMusicSoundtrackPlayClick(String id) {
        sAnalytics.logEvent(Key.AddMusicSoundtrackPlayClick, idParam(Event.Param.SOUND_TRACK_ID, id));
    }

    public static void addEffectSoundEffectAddClick(String id) {
        sAnalytics.logEvent(Key.AddEffectSoundEffectAddClick, idParam(Event.Param.SOUND_EFFECT_ID, id));
    }

    public static void addEffectSoundEffectPlayClick(String id) {
        sAnalytics.logEvent(Key.AddEffectSoundEffectPlayClick, idParam(Event.Param.SOUND_EFFECT_ID, id));
    }

    public static void addEffectSoundEffectDetailClick(String id) {
        sAnalytics.logEvent(Key.AddEffectSoundEffectDetailClick, idParam(Event.Param.SOUND_EFFECT_ID, id));
    }

    public static void addEffectSoundEffectFeaturedClick(String id) {
        sAnalytics.logEvent(Key.AddEffectSoundEffectFeaturedClick, idParam(Event.Param.SOUND_EFFECT_CATEGORY_ID, id));
    }

    public static void addEffectSoundEffectDownloadClic(String id) {
        sAnalytics.logEvent(Key.AddEffectSoundEffectDownloadClic, idParam(Event.Param.SOUND_EFFECT_ID, id));
    }

    public static void myCreatShareQQClick() {
        sAnalytics.logEvent(Key.MyCreatShareQQClick);
    }

    public static void myCreatShareWeixinClick() {
        sAnalytics.logEvent(Key.MyCreatShareWeixinClick);
    }

    public static void myCreatShareMoreClick() {
        sAnalytics.logEvent(Key.MyCreatShareMoreClick);
    }

    public static void myCreatHomeClick() {
        sAnalytics.logEvent(Key.MyCreatHomeClick);
    }

    public static void logEvent(String key) {
        sAnalytics.logEvent(key);
    }

    private static Map<String, String> idParam(String key, String value) {
        Map<String, String> param = new HashMap<>();
        param.put(key, value);
        return param;
    }
}
