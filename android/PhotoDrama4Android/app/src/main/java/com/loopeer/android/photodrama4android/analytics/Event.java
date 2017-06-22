package com.loopeer.android.photodrama4android.analytics;

public class Event {
    public interface Key {
        //广告
        String StartPageADClick = "StartPage_AD_Click";//点击启动页广告的次数
        String MyStartListADClick = "MyStart_List_AD_Click";//点击我要主演首页广告的次数

        //首页
        String HomeDramaClick = "Home_Drama_Click";//我要主演按钮的点击次数
        String HomeSettingClick = "Home_Setting_Click";//设置图标的点击次数

        //选择故事页面
        String DramaCategoryClick = "Drama_Category_Click";//各个类别的点击次数
        String DramaDetailClick = "Drama_Detail_Click";//故事详情点击次数
        String DramaUseClick = "Drama_Use_Click";//使用故事按钮点击次数

        //故事创作页面
        String DownloadClick = "Download_Click";//下载按钮的点击次数

        //设置页面
        String SettingSuggestClick = "Setting_Suggest_Click";//意见反馈的点击次数
        String SettingCacheClick = "Setting_Cache_Click";//清除缓存点击次数
        String SettingAboutUsClick = "Setting_AboutUs_Click";//关于我们页面

        //意见反馈页面
        String SuggestSubmitClick = "Suggest_Submit_Click";//提交按钮的点击次数

        //分享页面
        String ShareQQClick = "Share_QQ_Click";
        String ShareWeChatClick = "Share_WeChat_Click";
        String ShareMoreClick = "Share_More_Click";
        String ShareBackHomeClick = "Share_BackHome_Click";//返回首页

        String HomeMyCreatClick = "Home_MyCreat_Click";
        String MyStarDetailUseStoryClick = "MyStar_Detail_UseStory_Click";
        String MyStarPlayClick = "MyStar_Play_Click";
        String MyCreatStartClick = "MyCreat_Start_Click";
        String MyCreatTransferClick = "MyCreat_Transfer_Click";
        String MyCreatSubtitleClick = "MyCreat_Subtitle_Click";
        String MyCreatDubbingClick = "MyCreat_Dubbing_Click";
        String MyCreatSoundtrackClick = "MyCreat_Soundtrack_Click";
        String MyCreatSoundEffectClick = "MyCreat_SoundEffect_Click";
        String MyCreatTimeLongClick = "MyCreat_TimeLong_Click";
        String MyCreatDownloadClick = "MyCreat_Download_Click";
        String MyCreatTransferSaveClick = "MyCreat_Transfer_Save_Click";
        String MyCreatSubtitleSaveClick = "MyCreat_Subtitle_Save_Click";
        String MyCreatDubbingSaveClick = "MyCreat_Dubbing_Save_Click";
        String MyCreatSoundtrackAddClick = "MyCreat_Soundtrack_Add_Click";
        String MyCreatSoundtrackSaveClick = "MyCreat_Soundtrack_Save_Click";
        String MyCreatSoundEffectAddClick = "MyCreat_SoundEffect_Add_Click";
        String MyCreatSoundEffectSaveClick = "MyCreat_SoundEffect_Save_Click";
        String MyCreatTimeLongSaveClick = "MyCreat_TimeLong_Save_Click";
        String AddMusicSoundtrackAddClick = "AddMusic_Soundtrack_Add_Click";
        String AddMusicSoundtrackDetailClick = "AddMusic_Soundtrack_Detail_Click";
        String AddMusicSoundtrackFeaturedClick = "AddMusic_Soundtrack_Featured_Click";
        String AddMusicSoundtrackDownloadClic = "AddMusic_Soundtrack_Download_Clic";
        String AddMusicSoundtrackPlayClick = "AddMusic_Soundtrack_Play_Click";
        String AddEffectSoundEffectAddClick = "AddEffect_SoundEffect_Add_Click";
        String AddEffectSoundEffectPlayClick = "AddEffect_SoundEffect_Play_Click";
        String AddEffectSoundEffectDetailClick = "AddEffect_SoundEffect_Detail_Click";
        String AddEffectSoundEffectFeaturedClick = "AddEffect_SoundEffect_Featured_Click";
        String AddEffectSoundEffectDownloadClic = "AddEffect_SoundEffect_Download_Clic";
        String MyCreatShareQQClick = "MyCreat_ShareQQ_Click";
        String MyCreatShareWeixinClick = "MyCreat_ShareWeixin_Click";
        String MyCreatShareMoreClick = "MyCreat_ShareMore_Click";
        String MyCreatHomeClick = "MyCreat_Home_Click";

    }

    interface Param {
        String THEME_ID = "theme_id";
        String SOUND_TRACK_ID = "sound_track_id";
        String SOUND_TRACK_CATEGORY_ID = "sound_track_category_id";
        String SOUND_EFFECT_ID = "sound_effect_id";
        String SOUND_EFFECT_CATEGORY_ID = "sound_effect_category_id";
        String CATEGORY_ID = "category_id";
        String ADVERT_ID = "advert_id";
    }
}
