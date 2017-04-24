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

    public static void sessionListSearchClick() {
        sAnalytics.logEvent(Key.SessionListSearchClick, accountIdParam());
    }

    public static void sessionListSearchCancelClick() {
        sAnalytics.logEvent(Key.SessionListSearchCancelClick, accountIdParam());
    }

    public static void cameraCancelClick() {
        sAnalytics.logEvent(Key.CameraCancelClick, accountIdParam());
    }

    public static void cameraSwitchClick() {
        sAnalytics.logEvent(Key.CameraSwitchClick, accountIdParam());
    }

    public static void cameraTakeClick() {
        sAnalytics.logEvent(Key.CameraTakeClick, accountIdParam());
    }

    public static void cameraBeautyClick() {
        sAnalytics.logEvent(Key.CameraBeautyClick, accountIdParam());
    }

    public static void cameraFlashClick() {
        sAnalytics.logEvent(Key.CameraFlashClick, accountIdParam());
    }

    public static void previewCancelClick() {
        sAnalytics.logEvent(Key.PreviewCancelClick, accountIdParam());
    }

    public static void previewNextClick() {
        sAnalytics.logEvent(Key.PreviewNextClick, accountIdParam());
    }

    public static void unlockedCancelClick() {
        sAnalytics.logEvent(Key.UnlockedCancelClick, accountIdParam());
    }

    public static void unlockedNextClick() {
        sAnalytics.logEvent(Key.UnlockedNextClick, accountIdParam());
    }

    public static void selectCancelClick() {
        sAnalytics.logEvent(Key.SelectCancelClick, accountIdParam());
    }

    public static void selectSendClick() {
        sAnalytics.logEvent(Key.SelectSendClick, accountIdParam());
    }

    public static void selectAllClick() {
        sAnalytics.logEvent(Key.SelectAllClick, accountIdParam());
    }

    public static void selectClearClick() {
        sAnalytics.logEvent(Key.SelectClearClick, accountIdParam());
    }

    public static void selectChooseClick() {
        sAnalytics.logEvent(Key.SelectChooseClick, accountIdParam());
    }

    public static void findChangeClick() {
        sAnalytics.logEvent(Key.FindChangeClick, accountIdParam());
    }

    public static void findSearchClick() {
        sAnalytics.logEvent(Key.FindSearchClick, accountIdParam());
    }

    public static void mineFansClick() {
        sAnalytics.logEvent(Key.MineFansClick, accountIdParam());
    }

    public static void mineFollowerClick() {
        sAnalytics.logEvent(Key.MineFollowerClick, accountIdParam());
    }

    public static void mineMaidouClick() {
        sAnalytics.logEvent(Key.MineMaidouClick, accountIdParam());
    }

    public static void mineEditClick() {
        sAnalytics.logEvent(Key.MineEditClick, accountIdParam());
    }

    public static void mineSettingClick() {
        sAnalytics.logEvent(Key.MineSettingClick, accountIdParam());
    }

    public static void buyMaiDouBuyClick() {
        sAnalytics.logEvent(Key.BuyMaiDouBuyClick, accountIdParam());
    }

    public static void settingFollowMessageClick() {
        sAnalytics.logEvent(Key.SettingFollowMessageClick, accountIdParam());
    }

    public static void followMessageSettingClick() {
        sAnalytics.logEvent(Key.FollowMessageSettingClick, accountIdParam());
    }

    public static void settingReplyMaidouClick() {
        sAnalytics.logEvent(Key.SettingReplyMaidouClick, accountIdParam());
    }

    public static void settingTransactionClick() {
        sAnalytics.logEvent(Key.SettingTransactionClick, accountIdParam());
    }

    public static void settingAboutMaidouClick() {
        sAnalytics.logEvent(Key.SettingAboutMaidouClick, accountIdParam());
    }

    public static void settingProtocolClick() {
        sAnalytics.logEvent(Key.SettingProtocolClick, accountIdParam());
    }

    public static void settingCleanCacheClick() {
        sAnalytics.logEvent(Key.SettingCleanCacheClick, accountIdParam());
    }

    public static void settingExitClick() {
        sAnalytics.logEvent(Key.SettingExitClick, accountIdParam());
    }

    public static void sayHiTakeClick() {
        sAnalytics.logEvent(Key.SayHiTakeClick, accountIdParam());
    }

    public static void sayHiOKClick() {
        sAnalytics.logEvent(Key.SayHiOKClick, accountIdParam());
    }

    public static void sayHiDeleteClick() {
        sAnalytics.logEvent(Key.SayHiDeleteClick, accountIdParam());
    }

    public static void sayHiUpdateClick() {
        sAnalytics.logEvent(Key.SayHiUpdateClick, accountIdParam());
    }

    public static void aboutMaidouSuggestClick() {
        sAnalytics.logEvent(Key.AboutMaidouSuggestClick, accountIdParam());
    }

    public static void aboutMaidouPraiseClick() {
        sAnalytics.logEvent(Key.AboutMaidouPraiseClick, accountIdParam());
    }

    public static void suggestSubmitClick() {
        sAnalytics.logEvent(Key.SuggestSubmitClick, accountIdParam());
    }

    public static void sessionClick() {
        sAnalytics.logEvent(Key.SessionClick, accountIdParam());
    }

    public static void cameraClick() {
        sAnalytics.logEvent(Key.CameraClick, accountIdParam());
    }

    public static void findClick() {
        sAnalytics.logEvent(Key.FindClick, accountIdParam());
    }

    public static void mineClick() {
        sAnalytics.logEvent(Key.MineClick, accountIdParam());
    }

    public static void logInPhoneSuccessful() {
        sAnalytics.logEvent(Key.LogInPhoneSuccessful);
    }

    public static void logInwechatSuccessful() {
        sAnalytics.logEvent(Key.LogInwechatSuccessful);
    }

    public static void logInSuccessful() {
        sAnalytics.logEvent(Key.LogInSuccessful);
    }

    public static void editCoverPictureClick() {
        sAnalytics.logEvent(Key.EditCoverPictureClick, accountIdParam());
    }

    public static void editPortraitClick() {
        sAnalytics.logEvent(Key.EditPortraitClick, accountIdParam());
    }

    public static void editNameClick() {
        sAnalytics.logEvent(Key.EditNameClick, accountIdParam());
    }

    public static void editAboutMeClick() {
        sAnalytics.logEvent(Key.EditAboutMeClick, accountIdParam());
    }

    private static Map<String, String> accountIdParam() {
        Map<String, String> param = new HashMap<>();
//        param.put(Event.Param.ACCOUNT_ID, AccountUtils.getCurrentAccountId());
        return param;
    }
/*
    private static Map<String, String> accountIdParam(String key, String value) {
        Map<String, String> param = new HashMap<>();
        param.put(Event.Param.ACCOUNT_ID, AccountUtils.getCurrentAccountId());
        param.put(key, value);
        return param;
    }

    private static Map<String, String> accountIdParam(String key1, String value1, String key2, String value2) {
        Map<String, String> param = new HashMap<>();
        param.put(Event.Param.ACCOUNT_ID, AccountUtils.getCurrentAccountId());
        param.put(key1, value1);
        param.put(key2, value2);
        return param;
    }*/
}
