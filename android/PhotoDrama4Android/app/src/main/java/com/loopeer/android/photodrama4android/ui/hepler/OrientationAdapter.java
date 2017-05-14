package com.loopeer.android.photodrama4android.ui.hepler;

import android.content.Context;
import android.databinding.ViewDataBinding;

import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.ui.widget.TextureViewWrapper;

public abstract class OrientationAdapter<T extends ViewDataBinding> {

    private T mActivityDataBinding;
    protected Context mContext;
    private TextureViewWrapper mTextureViewWrapper;

    public OrientationAdapter(T activityDataBinding) {
        mActivityDataBinding = activityDataBinding;
        mContext = activityDataBinding.getRoot().getContext();
        mTextureViewWrapper = (TextureViewWrapper)mActivityDataBinding.getRoot().findViewById(R.id.animator);
    }

    public void toggle(boolean isLandscape) {
        if (mTextureViewWrapper != null) mTextureViewWrapper.setLandscape(isLandscape);
        if (isLandscape) {
            changeToLandscape(mActivityDataBinding);
        } else {
            changeToPortrait(mActivityDataBinding);
        }
    }

    abstract void changeToPortrait(T binding);

    abstract void changeToLandscape(T binding);
}
