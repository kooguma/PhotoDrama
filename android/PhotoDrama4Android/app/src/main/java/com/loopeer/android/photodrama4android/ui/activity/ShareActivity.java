package com.loopeer.android.photodrama4android.ui.activity;

import android.os.Bundle;
import android.view.View;

import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.analytics.Analyst;
import com.loopeer.android.photodrama4android.utils.ShareUtils;

public class ShareActivity extends PhotoDramaBaseActivity {

    private String mPath;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        mPath = getIntent().getStringExtra(Navigator.EXTRA_VIDEO_PATH);
    }

    @Override protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setCenterTitle(R.string.label_share);
    }

    public void onBackToMain(View view) {
        Analyst.shareBackHomeClick();
        Navigator.startDramaSelectActivity(this);
    }

    public void onQQClick(View view) {
        Analyst.shareQQClick();
        ShareUtils.startShare(this, ShareUtils.SHARE_TYPE_QQ, mPath);
    }

    public void onWeichatClick(View view) {
        Analyst.shareWeChatClick();
        ShareUtils.startShare(this, ShareUtils.SHARE_TYPE_WEICHAT, mPath);
    }

    public void onMoreClick(View view) {
        Analyst.shareMoreClick();
        ShareUtils.startShare(this, null, mPath);
    }
}
