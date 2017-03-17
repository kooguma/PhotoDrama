package com.loopeer.android.photodrama4android.opengl.render;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.loopeer.android.photodrama4android.opengl.IPlayerLife;
import com.loopeer.android.photodrama4android.opengl.IRendererWorker;
import com.loopeer.android.photodrama4android.opengl.IUpSeekBar;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLSurfaceView.RENDERMODE_WHEN_DIRTY;

public class GLThreadRender extends Thread implements GLSurfaceView.Renderer, IPlayerLife {

    private GLSurfaceView mGLSurfaceView;
    private Context mContext;
    private boolean mIsStop;
    private IRendererWorker mIRendererWorker;
    private long mUsedTime;
    private long mSumTime;
    private boolean mIsManual;
    private boolean mIsFinish;
    private boolean mIsBackGround;
    private IUpSeekBar mIUpSeekBar;

    public GLThreadRender(Context context, GLSurfaceView gLSurfaceView, IRendererWorker iRendererWorker) {
        mGLSurfaceView = gLSurfaceView;
        mGLSurfaceView.setEGLContextClientVersion(2);
        mGLSurfaceView.setRenderer(this);
        mGLSurfaceView.setRenderMode(RENDERMODE_WHEN_DIRTY);
        mContext = context;
        mIRendererWorker = iRendererWorker;
        mIsManual = false;
        mIsFinish = false;
    }

    public void setUpSeekBarListener(IUpSeekBar iUpSeekBar) {
        this.mIUpSeekBar = iUpSeekBar;
    }

    public void stopUp() {
        mIsStop = true;
    }

    public void startUp() {
        synchronized (this) {
            mIsStop = false;
            this.notify();
        }
    }

    @Override
    public void run() {
        synchronized (this) {
            while (!mIsFinish) {
                try {
                    if (mUsedTime >= mSumTime) {
                        if (mIUpSeekBar != null) {
                            mGLSurfaceView.post(new Runnable() {
                                @Override
                                public void run() {
                                    mIUpSeekBar.actionFinish();
                                }
                            });
                        }
                        this.wait();
                    }
                    if (mIsStop)
                        this.wait();
                    long startTime = System.currentTimeMillis();
                    mGLSurfaceView.requestRender();
                    this.wait();
                    Thread.sleep(Math.max(0, 30 - (System.currentTimeMillis() - startTime)));//睡眠
                    if (!mIsBackGround)
                        mUsedTime = mUsedTime + System.currentTimeMillis() - startTime;
                    else
                        mIsBackGround = false;
                    if (mIUpSeekBar != null) {
                        mGLSurfaceView.post((new Runnable() {
                            @Override
                            public void run() {
                                mIUpSeekBar.upSeekBar(mUsedTime);
                            }
                        }));
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mIRendererWorker.onSurfaceCreated(gl, config);
        if (!this.isAlive()) {
            this.start();
        }

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mIRendererWorker.onSurfaceChanged(gl, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (!mIsManual) {
            synchronized (this) {
                mIRendererWorker.drawFrame(mContext, gl, mUsedTime);
                this.notify();
            }
        } else {
            mIRendererWorker.drawFrame(mContext, gl, mUsedTime);
        }
    }

    public void setManualUpSeekBar(long usedTime) {
        if (!mIsManual)
            return;
        this.mUsedTime = usedTime;
        mGLSurfaceView.requestRender();
    }

    @Override
    public void onPause() {
        mGLSurfaceView.onPause();
    }

    @Override
    public void onResume() {
        mGLSurfaceView.onResume();
    }

    @Override
    public void onRestart() {
        startUp();
    }

    @Override
    public void onStop() {
        setBackGround(true);
        stopUp();
    }

    public void onDestroy() {
        mIsFinish = true;
        mIsStop = true;
        mGLSurfaceView = null;
    }

    public void updateTime(long start, long end) {
        mUsedTime = start;
        mSumTime = end;
    }

    public void setUsedTime(long usedTime) {
        this.mUsedTime = usedTime;
    }

    public void seekToTime(long usedTime) {
        this.mUsedTime = usedTime;
        mGLSurfaceView.requestRender();
    }

    public void setManual(boolean isManual) {
        this.mIsManual = isManual;
    }

    public void setBackGround(boolean isBackGround) {
        this.mIsBackGround = isBackGround;
    }


}
