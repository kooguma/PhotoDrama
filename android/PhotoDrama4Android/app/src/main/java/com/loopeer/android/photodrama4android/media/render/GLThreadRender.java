package com.loopeer.android.photodrama4android.media.render;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.loopeer.android.photodrama4android.BuildConfig;
import com.loopeer.android.photodrama4android.media.IPlayerLife;
import com.loopeer.android.photodrama4android.media.IRendererWorker;
import com.loopeer.android.photodrama4android.media.SeekChangeListener;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLSurfaceView.RENDERMODE_WHEN_DIRTY;

public class GLThreadRender extends Thread implements GLSurfaceView.Renderer, IPlayerLife {

    private static final String TAG = "GLThreadRender";

    public static final boolean DEBUG = BuildConfig.DEBUG && false;

    protected GLSurfaceView mGLSurfaceView;
    protected Context mContext;
    protected boolean mIsStop;
    protected IRendererWorker mIRendererWorker;
    protected long mUsedTime;
    protected long mSumTime;
    protected boolean mIsManual;
    protected boolean mIsFinish;
    protected boolean mIsBackGround;
    protected SeekChangeListener mSeekChangeListener;
    public static final int RECORDFPS = 29;
    private boolean mIsRecording = false;

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

    public void setSeekChangeListener(SeekChangeListener seekChangeListener) {
        this.mSeekChangeListener = seekChangeListener;
    }

    public void stopUp() {
        synchronized (this) {
            mIsStop = true;
        }
    }

    public boolean isStop() {
        return mIsStop;
    }

    public void startUp() {
        setManual(false);
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
                    long startTime = System.currentTimeMillis();
                    mGLSurfaceView.requestRender();
                    this.wait();
                    if (DEBUG) {
                        Log.e(TAG, "sleep Time " + (1000 / RECORDFPS - (System.currentTimeMillis() - startTime)));
                    }
                    if (!mIsRecording) Thread.sleep(Math.max(0, 1000 / RECORDFPS - (System.currentTimeMillis() - startTime)));//睡眠
                    if (!mIsBackGround)
                        mUsedTime = mUsedTime + (mIsRecording ? 1000 / RECORDFPS : System.currentTimeMillis() - startTime);
                    else
                        mIsBackGround = false;
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

    public long getUsedTime() {
        return mUsedTime;
    }

    public void seekToTime(long usedTime) {
        stopUp();
        setManual(true);
        this.mUsedTime = usedTime;
        mGLSurfaceView.requestRender();
    }

    public void requestRender() {
        setManual(true);
        mGLSurfaceView.requestRender();
    }

    public void setManual(boolean isManual) {
        this.mIsManual = isManual;
    }

    public void setBackGround(boolean isBackGround) {
        this.mIsBackGround = isBackGround;
    }

    public void setRecording(boolean recording) {
        mIsRecording = recording;
    }
}
