package com.loopeer.android.photodrama4android.ui.hepler;


import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;

public class ScreenOrientationHelper {

    public boolean mOrientationLandscape;
    public Activity mActivity;

    public ScreenOrientationHelper(Activity activity, boolean orientationLandscape) {
        mActivity = activity;
        mOrientationLandscape = orientationLandscape;
    }

    private void toggle() {
        if (mOrientationLandscape) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    public void fullScreen() {
        if (!mOrientationLandscape)
            toggle();
    }

    public void backPressed() {
        if (mOrientationLandscape) {
            toggle();
        } else {
            mActivity.finish();
        }
    }

    public void updateOrientation(boolean b) {
        mOrientationLandscape = b;
    }
}
