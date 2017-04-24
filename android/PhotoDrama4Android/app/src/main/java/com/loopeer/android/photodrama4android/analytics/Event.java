package com.loopeer.android.photodrama4android.analytics;

public class Event {
    interface Key {
        String SessionListPortraitClick = "SessionList_Portrait_Click";//列表头像的点击次数
        String SessionListChatClick = "SessionList_Chat_Click";//列表单个会话的点击次数
        String SessionListDeleteClick = "SessionList_Delete_Click";//列表单个会话删除点击次数
        String SessionListSessionClick = "SessionList_Session_Click";//会话详情回复图标的点击次数
        String SessionListSearchClick = "SessionList_Search_Click";//搜索的点击次数
        String SessionListSearchCancelClick = "SessionList_Search_Cancel_Click";//取消搜索的点击次数
        String CameraCancelClick = "Camera_Cancel_Click";//取消拍照的点击次数
        String CameraSwitchClick = "Camera_Switch_Click";//切换图标的点击次数
        String CameraTakeClick = "Camera_Take_Click";//拍摄按钮的点击次数
        String CameraBeautyClick = "Camera_ Beauty_Click";//美颜图标的点击次数
        String CameraFlashClick = "Camera_Flash_Click";//闪光灯的点击次数
        String PreviewCancelClick = "Preview_Cancel_Click";//取消预览的点击次数
        String PreviewNextClick = "Preview_Next_Click";//下一步按钮的点击次数
        String UnlockedCancelClick = "Unlocked_Cancel_Click";//取消按钮的点击次数
        String UnlockedNextClick = "Unlocked_Next_Click";//下一步按钮的点击次数
        String SelectCancelClick = "Select_Cancel_Click";//取消按钮的点击次数
        String SelectSendClick = "Select_Send_Click";//发送按钮的点击次数
        String SelectAllClick = "Select_All_Click";//全选的点击次数
        String SelectClearClick = "Select_Clear_Click";//清除的点击次数
        String SelectChooseClick = "Select_Choose_Click";//选择图标的点击次数
        String FindChangeClick = "Find_Change_Click";//换一换按钮的点击次数
        String FindRecommendFollowClick = "Find_ Recommend_Follow_Click";//人气推荐中关注按钮的点击次数
        String FindMoreFollowClick = "Find_ More_Follow_Click";//发现更多中关注按钮的点击次数
        String FindSearchClick = "Find_Search_Click";//发现页面搜索的点击次数
        String MineFansClick = "Mine_Fans_Click";//粉丝的点击次数
        String MineFollowerClick = "Mine_Follower_Click";//关注的点击次数
        String MineMaidouClick = "Mine_Maidou_Click";//麦豆的点击次数
        String MineEditClick = "Mine_Edit_Click";//编辑的点击次数
        String MineSettingClick = "Mine_Setting_Click";//设置的点击次数
        String FansFanClick = "Fans_Fan_Click";//单条粉丝的点击次数
        String FollowersFollowerClick = "Followers_Follower_Click";//单条关注的点击次数
        String BuyMaiDouBuyClick = "BuyMaiDou_Buy_Click";//购买按钮的点击次数
        String SettingFollowMessageClick = "Setting_FollowMessage_Click";//关注消息的点击次数
        String FollowMessageSettingClick = "FollowMessage_Setting_Click";//关注消息弹框去设定按钮的点击次数
        String SettingReplyMaidouClick = "Setting_ReplyMaidou_Click";//回复点数的点击次数
        String SettingTransactionClick = "Setting_Transaction_Click";//交易记录的点击次数
        String SettingAboutMaidouClick = "Setting_AboutMaidou_Click";//关于麦豆的点击次数
        String SettingProtocolClick = "Setting_ Protocol_Click";//协议条款的点击次数
        String SettingCleanCacheClick = "Setting_CleanCache_Click";//清除缓存的点击次数
        String SettingExitClick = "Setting_Exit_Click";//退出登录的点击次数
        String SayHiTakeClick = "SayHi_Take_Click";//录制sayhi视频拍摄按钮的点击次数
        String SayHiOKClick = "SayHi_OK_Click";//sayHi视频预览页面完成按钮点击次数
        String SayHiDeleteClick = "SayHi_Delete_Click";//sayhi删除视频的点击次数
        String SayHiUpdateClick = "SayHi_ Update_Click";//sayHi视频更新按钮的点击次数
        String AboutMaidouSuggestClick = "AboutMaidou_Suggest_Click";//建议反馈的点击次数
        String AboutMaidouPraiseClick = "AboutMaidou_Praise_Click";//好评的点击次数
        String SuggestSubmitClick = "Suggest_Submit_Click";//反馈建议提交的点击次数
        String EditCoverPictureClick = "Edit_CoverPicture_Click";//编辑封面图的点击次数
        String EditPortraitClick = "Edit_Portrait_Click";//编辑个人头像的点击次数
        String EditNameClick = "Edit_Name_Click";//编辑昵称的点击次数
        String EditAboutMeClick = "Edit_AboutMe_Click";//编辑关于我的点击次数
        String OthersHomepageFollowClick = "OthersHomepage_Follow_Click";//他人主页关注按钮的点击次数
        String OthersHomepageCancelFollowClick = "OthersHomepage_CancelFollow_Click";//他人主页取消关注的点击次数
        String OthersHomepageReportClick = "OthersHomepage_Report_Click";//他人主页举报的点击次数
        String LogInPhoneSuccessful = "LogIn_Phone_Successful";//手机号登录的成功次数
        String LogInwechatSuccessful = "LogIn_wechat_Successful";//微信登录的成功次数
        String LogInSuccessful = "LogIn_Successful";//登录成功次数
        String SessionClick = "Session_Click";//消息
        String CameraClick = "Camera_Click";//拍摄
        String FindClick = "Find_Click";//发现
        String MineClick = "Mine_Click";//我的
    }

    interface Param {
        String ACCOUNT_ID = "account_id";
        String CLICK_ACCOUNT_ID = "click_account_id";
        String DELETE_ACCOUNT_ID = "delete_account_id";
        String REPLY_ACCOUNT_ID = "reply_account_id";
        String POST_ID = "post_id";
        String FOLLOW_ACCOUNT_ID = "follow_account_id";
        String WATCH_ACCOUNT_ID = "watch_account_id";
        String REPORT_ACCOUNT_ID = "report_account_id";
    }
}
