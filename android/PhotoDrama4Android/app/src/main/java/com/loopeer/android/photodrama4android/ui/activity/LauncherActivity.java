package com.loopeer.android.photodrama4android.ui.activity;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.PhotoDramaApp;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.utils.PreUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Timed;
import io.reactivex.subjects.PublishSubject;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;

public class LauncherActivity extends PhotoDramaBaseActivity {

    private static final int sCount = 3;
    private CompositeDisposable mDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
        setContentView(R.layout.activity_launch);
        mDisposable
            .add(Flowable.just(isFirstLaunch())
                .doOnNext(aBoolean -> {
                    if (aBoolean) {
                        Navigator.startGuideActivity(this);
                    } else {
                        mDisposable.add(Flowable.interval(1, TimeUnit.SECONDS)
                            .take(sCount + 1)
                            .filter(aLong -> aLong >= sCount)
                            .doOnNext(aLong -> Navigator.startMainActivity(LauncherActivity.this))
                            .doOnNext(aLong -> finish())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe()
                        );
                    }
                })
                .subscribe());

    }

    private boolean isFirstLaunch() {
        boolean isFirstLaunch = !PreUtils.getVersion(this)
            .equals(PhotoDramaApp.getAppInfo().version);
        if (isFirstLaunch) {
            PreUtils.putVersion(this, PhotoDramaApp.getAppInfo().version);
        }
        return isFirstLaunch;
    }

    public void onJumpClick(View view) {
        Navigator.startMainActivity(LauncherActivity.this);
        finish();
    }

    @Override protected void onPause() {
        super.onPause();
        mDisposable.clear();
    }
}
