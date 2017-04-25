package com.loopeer.android.photodrama4android.ui.activity;

import android.os.Bundle;

import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.PhotoDramaApp;
import com.loopeer.android.photodrama4android.utils.PreUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;

public class LauncherActivity extends PhotoDramaBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);

        Flowable.just(isFirstLaunch())
                .delay(1300, TimeUnit.MILLISECONDS)
                .doOnNext(aBoolean -> {
                    if (aBoolean) {
                        Navigator.startGuideActivity(LauncherActivity.this);
                    } else {
                        Navigator.startMainActivity(LauncherActivity.this);
                    }
                    finish();
                }).subscribe();
    }

    private boolean isFirstLaunch() {
        boolean isFirstLaunch = !PreUtils.getVersion(this).equals(PhotoDramaApp.getAppInfo().version);
        if (isFirstLaunch) {
            PreUtils.putVersion(this, PhotoDramaApp.getAppInfo().version);
        }
        return isFirstLaunch;
    }
}
