package com.loopeer.android.photodrama4android.ui.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.databinding.ActivityImageSelectBinding;
import com.loopeer.android.photodrama4android.opengl.cache.BitmapFactory;
import com.loopeer.android.photodrama4android.opengl.model.Drama;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import rx.Observable;

public class ImageSelectActivity extends MovieMakerBaseActivity {

    private ActivityImageSelectBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_image_select);
    }

    public void onBtnClick(View view) {
        //TODO test placeholder
        ArrayList<String> urls = mBinding.imageGridView.getLocalUrls();
        showProgressLoading("");
        BitmapFactory.getInstance().loadImages(urls.toArray(new String[]{}));
        Observable.timer(300, TimeUnit.MILLISECONDS)
                .doOnNext(t -> Navigator.startMakeMovieActivity(this, Drama.createFromPath(urls)))
                .subscribe();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            mBinding.imageGridView.onParentResult(requestCode, data);
        }
    }
}
