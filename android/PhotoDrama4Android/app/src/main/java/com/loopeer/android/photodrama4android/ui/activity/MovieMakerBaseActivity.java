package com.loopeer.android.photodrama4android.ui.activity;


import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.laputapp.ui.BaseActivity;
import com.loopeer.android.photodrama4android.R;

public class MovieMakerBaseActivity extends BaseActivity {

    private boolean mHasParent;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        try {
            mHasParent = NavUtils.getParentActivityName(this, getComponentName()) != null;
        } catch (PackageManager.NameNotFoundException e) {
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (mHasParent) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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


}
