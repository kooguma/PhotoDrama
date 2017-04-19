package com.loopeer.android.photodrama4android.media.recorder;

public abstract class MediaDecoder implements Runnable{
    protected static final int TIMEOUT_USEC = 5000;

    protected DecodeProgressCallback mCallback;

    public MediaDecoder(DecodeProgressCallback callback) {
        mCallback = callback;
    }

    public void startDecode() {
        new Thread(this, getClass().getSimpleName()).start();
    }

    public interface DecodeProgressCallback {
        void onFinish();
    }
}
