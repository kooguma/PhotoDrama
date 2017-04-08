package com.loopeer.android.photodrama4android.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.utils.CacheUtils;
import com.loopeer.android.photodrama4android.utils.Toaster;
import com.loopeer.formitemview.FormItemView;

public class SettingActivity extends PhotoDramaBaseActivity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setupView();
    }

    @Override protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setCenterTitle(R.string.label_setting);
    }

    private void setupView() {
        final double cache = (double) CacheUtils.getCacheSize(this) / 1024 / 1024;
        FormItemView itemView = (FormItemView) findViewById(R.id.item_cache);
        itemView.setContentText(getString(R.string.settings_cache_size_format,cache));
    }

    public void onClearCacheClick(View view) {
        double cache = (double) CacheUtils.getCacheSize(this) / 1024 / 1024;
        new AlertDialog.Builder(this)
            .setMessage(getString(R.string.setting_is_clear_cache))
            .setPositiveButton(R.string.common_sure,
                (dialog, which) -> {
                    CacheUtils.clearCache(SettingActivity.this);
                    Toaster.showToast(R.string.settings_clear_cache_success);
                })
            .setNegativeButton(R.string.common_cancel, null)
            .show();
    }

    public void onFeedBackClick(View view) {
        Navigator.startFeedbackActivity(this);
    }

    public void onAboutClick(View view) {
        Navigator.startAboutActivity(this);
    }
}
