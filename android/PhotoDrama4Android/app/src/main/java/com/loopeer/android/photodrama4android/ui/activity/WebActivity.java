package com.loopeer.android.photodrama4android.ui.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;
import com.fastui.uipattern.IWeb;
import com.laputapp.widget.ProgressWebView;
import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.databinding.ActivityWebBinding;

public class WebActivity extends PhotoDramaBaseActivity implements IWeb {

    private String mUrl;
    private ActivityWebBinding mBinding;
    private ProgressWebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_web);
        mWebView = mBinding.webView;
        mUrl = getIntent().getStringExtra(Navigator.EXTRA_URL);
    }

    @Override protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String title = getIntent().getStringExtra(Intent.EXTRA_TITLE);
        setCenterTitle(title);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public String getUrl() {
        return mUrl;
    }

    @Override
    public void loadWebError(String error) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseWebViewResource();
    }

    private void releaseWebViewResource() {
        if (mWebView == null) return;
        ((ViewGroup) mWebView.getParent()).removeView(mWebView);
        mWebView.removeAllViews();
        mWebView.destroy();
        mWebView = null;
    }

}
