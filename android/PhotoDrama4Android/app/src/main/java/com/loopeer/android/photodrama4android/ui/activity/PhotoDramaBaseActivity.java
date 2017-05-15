package com.loopeer.android.photodrama4android.ui.activity;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;

import android.widget.TextView;
import com.laputapp.ui.BaseActivity;
import com.loopeer.android.photodrama4android.R;

public class PhotoDramaBaseActivity extends BaseActivity {

    private boolean mHasParent;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        try {
            mHasParent = NavUtils.getParentActivityName(this, getComponentName()) != null;
        } catch (PackageManager.NameNotFoundException e) {
        }
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_black);
            if (mHasParent) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            ActivityInfo activityInfo = null;
            try {
                activityInfo = getPackageManager().getActivityInfo(
                        getComponentName(), PackageManager.GET_META_DATA);
                String title = activityInfo.loadLabel(getPackageManager())
                        .toString();
                if (!TextUtils.isEmpty(title)) {
                    setCenterTitle(title);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mHasParent) {
                    onBackPressed();
                    return true;
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void setCenterTitle(String title) {
        if (mToolbar != null) {
            mToolbar.setTitle("");
            TextView view = (TextView) mToolbar.findViewById(R.id.txt_toolbar_title);
            if (view != null) {
                view.setText(title);
            }
        }
    }

    protected void setCenterTitle(@StringRes int resId) {
        setCenterTitle(getString(resId));
    }

}
