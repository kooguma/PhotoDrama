package com.loopeer.android.photodrama4android.media;


import com.loopeer.android.photodrama4android.ui.widget.ScrollSelectView;

public class ScrollSelectViewSeekImpl implements SeekWrapper.SeekImpl {
    private ScrollSelectView mScrollSelectView;

    public ScrollSelectViewSeekImpl(ScrollSelectView scrollSelectView) {
        mScrollSelectView = scrollSelectView;
    }

    @Override
    public void setMax(int max) {
        mScrollSelectView.setMax(max);
    }

    @Override
    public void addOnSeekChangeListener(OnSeekProgressChangeListener listener) {
        mScrollSelectView.setOnSeekProgressChangeListener(this, listener);
    }

    @Override
    public void setProgress(int progress) {
        mScrollSelectView.setProgress(progress);
    }

    @Override
    public int getProgress() {
        return mScrollSelectView.getProgress();
    }

}
