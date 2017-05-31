package com.loopeer.android.photodrama4android.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.analytics.Analyst;
import com.loopeer.android.photodrama4android.utils.CacheUtils;
import com.loopeer.android.photodrama4android.utils.Toaster;
import com.loopeer.formitemview.FormItemView;

public class SettingActivity extends PhotoDramaBaseActivity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        updateView();
    }

    @Override protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setCenterTitle(R.string.label_setting);
    }

    private void updateView() {
        final double cache = (double) CacheUtils.getCacheSize(this) / 1024 / 1024;
        FormItemView itemView = (FormItemView) findViewById(R.id.item_cache);
        itemView.setContentText(getString(R.string.settings_cache_size_format,cache));
    }

    public void onClearCacheClick(View view) {
        Analyst.settingCacheClick();
        CacheUtils.clearCache(SettingActivity.this);
        Toaster.showToast(R.string.settings_clear_cache_success);
        updateView();
    }

    public void onFeedBackClick(View view) {
        Analyst.settingSuggestClick();
        Navigator.startFeedbackActivity(this);
    }

    public void onAboutClick(View view) {
        Analyst.settingAboutUsClick();
        Navigator.startAboutActivity(this);
    }

    public void onRankClick(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=" + getPackageName()));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.setting_rank_no_app, Toast.LENGTH_SHORT).show();
        }
    }
}
