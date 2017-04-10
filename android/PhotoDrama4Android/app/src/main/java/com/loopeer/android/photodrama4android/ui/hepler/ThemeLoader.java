package com.loopeer.android.photodrama4android.ui.hepler;

import android.support.annotation.StringRes;
import android.util.Log;
import android.widget.ViewAnimator;
import android.widget.ViewSwitcher;
import com.loopeer.android.photodrama4android.ui.widget.TextProgressBar;

public class ThemeLoader implements ILoader {

    private ViewAnimator mAnimator;
    private TextProgressBar mProgressBar;

    public ThemeLoader(ViewAnimator animator) {
        mAnimator = animator;
        mProgressBar = (TextProgressBar) mAnimator.getChildAt(ILoader.VIEW_PROGRESS_INDEX);
    }

    @Override public void showProgress() {
        mAnimator.setDisplayedChild(ILoader.VIEW_PROGRESS_INDEX);
        mProgressBar.showProgress();
    }

    @Override public void showContent() {
        mAnimator.setDisplayedChild(ILoader.VIEW_CONTENT_INDEX);
        mProgressBar.dismissProgress();
    }

    @Override public void showMessage(String message) {
        mAnimator.setDisplayedChild(ILoader.VIEW_PROGRESS_INDEX);
        mProgressBar.setText(message);
    }

    @Override public void showMessage(@StringRes int resId) {
        mAnimator.setDisplayedChild(ILoader.VIEW_PROGRESS_INDEX);
        mProgressBar.setText(resId);
    }
}
