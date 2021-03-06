package com.loopeer.android.photodrama4android.ui.activity;

import android.os.Bundle;
import android.view.View;

import com.laputapp.http.BaseResponse;
import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.analytics.Analyst;
import com.loopeer.android.photodrama4android.api.service.SystemService;
import com.loopeer.android.photodrama4android.api.service.ThemeService;
import com.loopeer.android.photodrama4android.model.Theme;
import com.loopeer.android.photodrama4android.utils.ShareUtils;

import static com.loopeer.android.photodrama4android.utils.Toaster.showToast;

public class ShareActivity extends PhotoDramaBaseActivity {

    private String mPath;
    private Theme mTheme;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        mPath = getIntent().getStringExtra(Navigator.EXTRA_VIDEO_PATH);
        mTheme = (Theme) getIntent().getSerializableExtra(Navigator.EXTRA_THEME);
    }

    @Override protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setCenterTitle(R.string.label_share);
    }

    public void onBackToMain(View view) {
        if (mTheme == null) {
            Analyst.myCreatHomeClick();
        } else {
            Analyst.shareBackHomeClick();
        }
        Navigator.startMainActivity(this);
    }

    public void onQQClick(View view) {
        shareSubmit();
        if (mTheme == null) {
            Analyst.myCreatShareQQClick();
        } else {
            Analyst.shareQQClick();
        }
        ShareUtils.startShare(this, ShareUtils.SHARE_TYPE_QQ, mPath);

    }

    public void onWeichatClick(View view) {
        shareSubmit();
        if (mTheme == null) {
            Analyst.myCreatShareWeixinClick();
        } else {
            Analyst.shareWeChatClick();
        }
        ShareUtils.startShare(this, ShareUtils.SHARE_TYPE_WEICHAT, mPath);
    }

    public void onMoreClick(View view) {
        shareSubmit();
        if (mTheme == null) {
            Analyst.myCreatShareMoreClick();
        } else {
            Analyst.shareMoreClick();
        }
        ShareUtils.startShare(this, null, mPath);
    }

    public void shareSubmit() {
        if (mTheme == null) return;
        registerSubscription(
                ThemeService.INSTANCE.share(mTheme.id)
                        .subscribe()
        );
    }
}
