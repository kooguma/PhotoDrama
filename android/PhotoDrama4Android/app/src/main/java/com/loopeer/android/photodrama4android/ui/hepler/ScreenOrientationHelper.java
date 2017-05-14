package com.loopeer.android.photodrama4android.ui.hepler;


import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.view.MenuItem;

public class ScreenOrientationHelper {

    public boolean mOrientationLandscape;
    public Activity mActivity;
    private OrientationAdapter mOrientationAdapter;

    public ScreenOrientationHelper(Activity activity, boolean orientationLandscape, OrientationAdapter orientationAdapter) {
        mActivity = activity;
        mOrientationLandscape = orientationLandscape;
        mOrientationAdapter = orientationAdapter;
    }

    private void toggle() {
        if (mOrientationLandscape) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        mOrientationAdapter.toggle(!mOrientationLandscape);
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
