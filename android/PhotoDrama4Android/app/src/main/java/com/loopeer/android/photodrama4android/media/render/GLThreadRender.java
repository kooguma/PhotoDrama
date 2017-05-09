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
        mContext = context;
        mIRendererWorker = iRendererWorker;
        mIsManual = false;
        mIsFinish = false;
        if (!this.isAlive()) {
            this.start();
        }
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
        synchronized (mLock) {
            while (!mIsFinish && mSurfaceTexture == null) {
                try {
                    mLock.wait();
                    mEglCore = new EglCore(null, EglCore.FLAG_TRY_GLES3);
                    mWindowSurface = new WindowSurface(mEglCore, mSurfaceTexture);
                    mWindowSurface.makeCurrent();
                    onSurfaceCreated(mWindowSurface, mEglCore);
                } catch (InterruptedException ie) {
                    throw new RuntimeException(ie);     // not expected
                }
            }
        }

        while (!mIsFinish) {

            try {
                if (mUsedTime >= mSumTime) {
                    if (mSeekChangeListener != null) {
                        mTextureView.post(() -> mSeekChangeListener.actionFinish());
                    }
                    synchronized (this) {
                        this.wait();
                    }
                }
                synchronized (this) {
                    if (mIsStop)
                        this.wait();
                }
                long startTime = System.currentTimeMillis();
                doRender();
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
                    mTextureView.post((() -> mSeekChangeListener.seekChange(mUsedTime)));
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void onSurfaceCreated(WindowSurface windowSurface, EglCore eglCore) {
        mIRendererWorker.onSurfaceCreated(windowSurface, eglCore);

    }

    public void onSurfaceChanged(WindowSurface windowSurface, int width, int height) {
        mIRendererWorker.onSurfaceChanged(windowSurface, width, height);
    }

    public void onDrawFrame(WindowSurface windowSurface) {
        mIRendererWorker.drawFrame(mContext, windowSurface, mUsedTime);
    }

    public void setManualUpSeekBar(long usedTime) {
        if (!mIsManual)
            return;
        this.mUsedTime = usedTime;
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
        doRender();

    }

    public void requestRender() {
        setManual(true);
        doRender();
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
            mLock.notify();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        onSurfaceChanged(mWindowSurface, mWindowSurface.getWidth(), mWindowSurface.getHeight());
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
