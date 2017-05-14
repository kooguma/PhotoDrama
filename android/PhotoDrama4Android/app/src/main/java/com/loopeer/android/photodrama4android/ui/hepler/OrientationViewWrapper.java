package com.loopeer.android.photodrama4android.ui.hepler;


import android.view.View;
import android.widget.TextView;

import com.loopeer.android.photodrama4android.media.VideoPlayerManager;

import java.text.SimpleDateFormat;

public class OrientationViewWrapper implements VideoPlayerManager.ProgressChangeListener {

    public View mPlayBtn;
    public TextView mTimeStartView;
    public TextView mTimeTotalView;
    public View[] mViews;

    public OrientationViewWrapper(View playBtn, TextView timeStartView, TextView timeTotalView, View... views) {
        mPlayBtn = playBtn;
        mTimeStartView = timeStartView;
        mTimeTotalView = timeTotalView;
        mViews = views;
    }

    @Override
    public void onProgressInit(int progress, int maxValue) {
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
        String hms = formatter.format(progress);
        mTimeStartView.setText(hms);
        String hmsTotal = formatter.format(maxValue + 1 - progress);
        mTimeTotalView.setText(hmsTotal);
    }

    @Override
    public void onProgressStop() {
        mPlayBtn.setSelected(true);
    }

    @Override
    public void onProgressChange(int progress, int maxValue) {
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
        String hms = formatter.format(progress);
        mTimeStartView.setText(hms);
        String hmsTotal = formatter.format(maxValue + 1 - progress);
        mTimeTotalView.setText(hmsTotal);
    }

    @Override
    public void onProgressStart() {
        mPlayBtn.setSelected(false);
    }

    public void hide() {
        for (View view :
                mViews) {
            view.setVisibility(View.GONE);
        }
    }

    public void show() {
        for (View view :
                mViews) {
            view.setVisibility(View.VISIBLE);
        }
    }
}
