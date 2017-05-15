package com.loopeer.android.photodrama4android.ui.hepler;

import android.databinding.ViewDataBinding;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.ui.activity.PhotoDramaBaseActivity;
import com.loopeer.android.photodrama4android.ui.widget.TextureViewWrapper;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class OrientationAdapter<T extends ViewDataBinding, E extends PhotoDramaBaseActivity> {

    private final CompositeDisposable mAllSubscription = new CompositeDisposable();

    protected T mBinding;
    protected E mActivity;
    private TextureViewWrapper mTextureViewWrapper;
    protected boolean mIsLandscape;

    public OrientationAdapter(T activityDataBinding, E activity) {
        mBinding = activityDataBinding;
        mActivity = activity;
        mTextureViewWrapper = (TextureViewWrapper)mBinding.getRoot().findViewById(R.id.animator);
    }

    public void onCreate() {

    }

    public void toggle(boolean isLandscape) {
        mIsLandscape = isLandscape;
        if (mTextureViewWrapper != null) mTextureViewWrapper.setLandscape(isLandscape);
        if (isLandscape) {
            changeToLandscape();
        } else {
            changeToPortrait();
        }
    }

    abstract void changeToPortrait();

    abstract void changeToLandscape();

    protected void registerSubscription(Disposable disposable) {
        this.mAllSubscription.add(disposable);
    }

    protected void unregisterSubscription(Disposable disposable) {
        this.mAllSubscription.remove(disposable);
    }

    protected void clearSubscription() {
        this.mAllSubscription.clear();
    }

    public void onDestroy() {
        this.clearSubscription();
    }
}
