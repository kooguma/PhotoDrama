package com.loopeer.android.photodrama4android.ui.activity;

import android.net.Uri;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import com.facebook.drawee.view.SimpleDraweeView;
import com.laputapp.http.BaseResponse;
import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.PhotoDramaApp;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.api.ResponseObservable;
import com.loopeer.android.photodrama4android.api.service.SystemService;
import com.loopeer.android.photodrama4android.model.Advert;
import com.loopeer.android.photodrama4android.utils.FileManager;
import com.loopeer.android.photodrama4android.utils.PreUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.Timed;
import io.reactivex.subjects.PublishSubject;
import java.io.File;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import zlc.season.rxdownload2.RxDownload;

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
                        final String adUrl = PreUtils.getAdvertUrl(this);
                        final String link = PreUtils.getAdvertLink(this);
                        if (!TextUtils.isEmpty(adUrl)) {
                            SimpleDraweeView imgAd = (SimpleDraweeView) findViewById(R.id.img_ad);
                            File file = getLocalAdFile(adUrl);
                            imgAd.setImageURI(Uri.fromFile(file));
                            if (!TextUtils.isEmpty(link)) {
                                imgAd.setOnClickListener(l -> {
                                    mDisposable.clear();
                                    Navigator.startMainActivity(LauncherActivity.this);
                                    Navigator.startWebActivity(this, link);
                                    finish();
                                });
                            }
                        }
                        updateLaunchAd();
                        mDisposable.add(Flowable.interval(1, TimeUnit.SECONDS)
                            .take(sCount + 1)
                            .filter(aLong -> aLong >= sCount)
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnNext(aLong -> Navigator.startMainActivity(LauncherActivity.this))
                            .doOnNext(aLong -> finish())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe()
                        );
                    }
                })
                .subscribe());

    }

    private void updateLaunchAd() {
        registerSubscription(
            SystemService.INSTANCE.launchAd()
                .subscribe(response -> {
                    if (response.isSuccessed()) {
                        final String imageUrl = response.mData.image;
                        final String link = response.mData.relValue;
                        RxDownload.getInstance(this)
                            .download(imageUrl)
                            .subscribeOn(Schedulers.io())
                            .subscribe(status -> {
                            }, throwable -> {
                            }, () -> {
                                PreUtils.setAdvertUrl(this, imageUrl);
                                PreUtils.setAdvertLink(this, link);
                            });
                    }
                })
        );
    }

    private File getLocalAdFile(String adUrl) {
        File[] files = RxDownload.getInstance(this).getRealFiles(adUrl);
        if (files != null && files[0].exists()) {
            return files[0];
        } else {
            return null;
        }
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

    @Override protected void onDestroy() {
        super.onDestroy();
        mDisposable.clear();
    }
}
