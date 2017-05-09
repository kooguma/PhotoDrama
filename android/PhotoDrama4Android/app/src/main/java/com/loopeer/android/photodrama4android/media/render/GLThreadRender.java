package com.loopeer.android.photodrama4android.media.render;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.Log;
import android.view.TextureView;

import com.loopeer.android.photodrama4android.BuildConfig;
import com.loopeer.android.photodrama4android.media.IPlayerLife;
import com.loopeer.android.photodrama4android.media.IRendererWorker;
import com.loopeer.android.photodrama4android.media.MovieMakerTextureView;
import com.loopeer.android.photodrama4android.media.SeekChangeListener;
import com.loopeer.android.photodrama4android.media.recorder.gles.EglCore;
import com.loopeer.android.photodrama4android.media.recorder.gles.WindowSurface;

public class GLThreadRender extends Thread implements IPlayerLife, TextureView.SurfaceTextureListener {

    private static final String TAG = "GLThreadRender";

    public static final boolean DEBUG = BuildConfig.DEBUG && false;

    protected MovieMakerTextureView mTextureView;
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
    private Object mLock = new Object();        // guards mSurfaceTexture, mDone
    private SurfaceTexture mSurfaceTexture;
    private EglCore mEglCore;
    private WindowSurface mWindowSurface;

    public GLThreadRender(Context context, TextureView textureView, IRendererWorker iRendererWorker) {
        mTextureView = (MovieMakerTextureView) textureView;
        mTextureView.setSurfaceTextureListener(this);

        /*mGLSurfaceView.setEGLContextClientVersion(2);
        mGLSurfaceView.setRenderer(this);
        mGLSurfaceView.setRenderMode(RENDERMODE_WHEN_DIRTY);*/
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
                // Latch the SurfaceTexture when it becomes available.  We have to wait for
                // the TextureView to create it.
                synchronized (mLock) {
                    while (!mIsFinish && mSurfaceTexture == null) {
                        try {
                            mLock.wait();
                        } catch (InterruptedException ie) {
                            throw new RuntimeException(ie);     // not expected
                        }
                    }
                    if (mIsFinish) {
                        break;
                    }
                }

                try {
                    if (mUsedTime >= mSumTime) {
                        if (mSeekChangeListener != null) {
                            mTextureView.post(new Runnable() {
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
                    /*mGLSurfaceView.requestRender();*/
                    doRender();
                    this.wait();
                    if (DEBUG) {
                        Log.e(TAG, "sleep Time " + (1000 / RECORDFPS - (System.currentTimeMillis() - startTime)));
                    }
                    if (!mIsRecording)
                        Thread.sleep(Math.max(0, 1000 / RECORDFPS - (System.currentTimeMillis() - startTime)));//睡眠
                    if (!mIsBackGround)
                        mUsedTime = mUsedTime + (mIsRecording ? 1000 / RECORDFPS : System.currentTimeMillis() - startTime);
                    else
                        mIsBackGround = false;
                    if (mSeekChangeListener != null) {
                        mTextureView.post((new Runnable() {
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

    public void onSurfaceCreated(WindowSurface windowSurface, EglCore eglCore) {
        mIRendererWorker.onSurfaceCreated(windowSurface, eglCore);
        if (!this.isAlive()) {
            this.start();
        }

    }

    public void onSurfaceChanged(WindowSurface windowSurface, int width, int height) {
        mIRendererWorker.onSurfaceChanged(windowSurface, width, height);
    }

    public void onDrawFrame(WindowSurface windowSurface) {
        if (!mIsManual) {
            synchronized (this) {
                mIRendererWorker.drawFrame(mContext, windowSurface, mUsedTime);
                this.notify();
            }
        } else {
            mIRendererWorker.drawFrame(mContext, windowSurface, mUsedTime);
        }
    }

    public void setManualUpSeekBar(long usedTime) {
        if (!mIsManual)
            return;
        this.mUsedTime = usedTime;
//        mGLSurfaceView.requestRender();
        doRender();
    }

    public void doRender() {
        if (mWindowSurface == null) return;
        onDrawFrame(mWindowSurface);
    }

    @Override
    public void onPause() {
//        mGLSurfaceView.onPause();
    }

    @Override
    public void onResume() {
//        mGLSurfaceView.onResume();
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
        mTextureView = null;
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
//        mGLSurfaceView.requestRender();
        doRender();

    }

    public void requestRender() {
        setManual(true);
        doRender();

//        mGLSurfaceView.requestRender();

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

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (DEBUG) Log.e(TAG, "onSurfaceTextureAvailable(" + width + "x" + height + ")");

        synchronized (mLock) {
            mSurfaceTexture = surface;
            mEglCore = new EglCore(null, EglCore.FLAG_TRY_GLES3);
            mWindowSurface = new WindowSurface(mEglCore, mSurfaceTexture);
            mWindowSurface.makeCurrent();
            onSurfaceCreated(mWindowSurface, mEglCore);
            onSurfaceChanged(mWindowSurface, mWindowSurface.getWidth(), mWindowSurface.getHeight());
            mTextureView.updateLoader(mWindowSurface, mEglCore);
            mLock.notify();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        if (DEBUG) Log.e(TAG, "onSurfaceTextureSizeChanged(" + width + "x" + height + ")");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (DEBUG) Log.e(TAG, "onSurfaceTextureDestroyed");

        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        if (DEBUG) Log.e(TAG, "onSurfaceTextureUpdated");
    }
}
