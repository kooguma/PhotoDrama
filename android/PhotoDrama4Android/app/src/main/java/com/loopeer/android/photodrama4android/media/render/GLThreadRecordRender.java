package com.loopeer.android.photodrama4android.media.render;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.loopeer.android.photodrama4android.media.IRendererWorker;

public class GLThreadRecordRender extends GLThreadRender {

    private int mFps = 30;

    public GLThreadRecordRender(Context context, GLSurfaceView gLSurfaceView, IRendererWorker iRendererWorker) {
        super(context, gLSurfaceView, iRendererWorker);
    }

    @Override
    public void run() {

        synchronized (this) {
            while (!mIsFinish) {
                try {
                    if (mUsedTime >= mSumTime) {
                        if (mSeekChangeListener != null) {
                            mGLSurfaceView.post(new Runnable() {
                                @Override
                                public void run() {
                                    mSeekChangeListener.actionFinish();
                                }
                            });
                        }
                        this.wait();
                    }
                    if (mIsStop)
                        this.wait();
                    mGLSurfaceView.requestRender();
                    this.wait();
                    mUsedTime = mUsedTime + 1000 / mFps;
                    if (mSeekChangeListener != null) {
                        mGLSurfaceView.post((new Runnable() {
                            @Override
                            public void run() {
                                mSeekChangeListener.seekChange(mUsedTime);
                            }
                        }));
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
