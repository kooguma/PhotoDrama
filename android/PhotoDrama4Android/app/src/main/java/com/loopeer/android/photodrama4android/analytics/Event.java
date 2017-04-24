package com.loopeer.android.photodrama4android.analytics;

public class Event {
    interface Key {
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
    }

    interface Param {
        String THEME_ID = "theme_id";
        String CATEGORY_ID = "category_id";
    }
}
