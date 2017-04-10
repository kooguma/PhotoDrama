package com.loopeer.android.photodrama4android.ui.hepler;

import android.support.annotation.StringRes;

public interface ILoader {
    static final int VIEW_PROGRESS_INDEX = 1;
    static final int VIEW_CONTENT_INDEX = 0;
    void showProgress();
    void showContent();
    void showMessage(String message);
    void showMessage(@StringRes int resId);
}
