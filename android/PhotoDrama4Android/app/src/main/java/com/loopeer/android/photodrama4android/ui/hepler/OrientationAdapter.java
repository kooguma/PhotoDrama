package com.loopeer.android.photodrama4android.ui.hepler;


import android.content.Context;
import android.content.res.Configuration;
import android.databinding.ViewDataBinding;

public abstract class OrientationAdapter<T extends ViewDataBinding> {

    private T mActivityDataBinding;
    protected Context mContext;

    public OrientationAdapter(T activityDataBinding) {
        mActivityDataBinding = activityDataBinding;
        mContext = activityDataBinding.getRoot().getContext();
    }

    public void toggle(boolean isLandscape) {
        if (isLandscape) {
            changeToLandscape(mActivityDataBinding);
        } else {
            changeToPortrait(mActivityDataBinding);
        }
    }

    abstract void changeToPortrait(T binding);

    abstract void changeToLandscape(T binding);
}
