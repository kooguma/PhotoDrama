package com.loopeer.android.photodrama4android.media;

public class SeekWrapperHolders {

    private SeekWrapper[] mSeekWrappers;

    public SeekWrapperHolders(SeekWrapper[] seekWrappers) {
        mSeekWrappers = seekWrappers;
    }

    public void setMax(int max) {
        if (mSeekWrappers == null) return;
        for (SeekWrapper seekWraper :
                mSeekWrappers) {
            seekWraper.setMax(max);
        }
    }

    public void setOnSeekChangeListener(OnSeekProgressChangeListener listener) {
        if (mSeekWrappers == null) return;
        for (SeekWrapper seekWraper :
                mSeekWrappers) {
            seekWraper.setOnSeekChangeListener(listener);
        }
    }

    public void setProgress(int progress) {
        if (mSeekWrappers == null) return;
        for (SeekWrapper seekWraper :
                mSeekWrappers) {
            seekWraper.setProgress(progress);
        }
    }


}
