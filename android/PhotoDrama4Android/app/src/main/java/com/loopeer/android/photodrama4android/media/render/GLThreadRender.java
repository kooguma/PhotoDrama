package com.loopeer.android.photodrama4android.media.render;

import android.content.Context;
import android.util.Log;
import android.view.TextureView;

import com.loopeer.android.photodrama4android.BuildConfig;
import com.loopeer.android.photodrama4android.media.IPlayerLife;
import com.loopeer.android.photodrama4android.media.IRendererWorker;
import com.loopeer.android.photodrama4android.media.MovieMakerTextureView;
import com.loopeer.android.photodrama4android.media.SeekChangeListener;
import com.loopeer.android.photodrama4android.media.TextureRenderer;
import com.loopeer.android.photodrama4android.media.recorder.gles.EglCore;
import com.loopeer.android.photodrama4android.media.recorder.gles.WindowSurface;

public class GLThreadRender extends Thread implements IPlayerLife, TextureRenderer.Renderer {

    private static final String TAG = "GLThreadRender";

    public static final boolean DEBUG = BuildConfig.DEBUG;

    protected MovieMakerTextureView mMovieMakerTextureView;
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
    private Object mLock = new Object();
    private boolean mTextureViewReadyOk = false;

    public GLThreadRender(Context context, TextureView textureView, IRendererWorker iRendererWorker) {
        super("GLThreadRender");
        mMovieMakerTextureView = (MovieMakerTextureView) textureView;
        mMovieMakerTextureView.setRenderer(this);
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
//        synchronized (mLock) {
            mIsStop = true;
//        }
    }

    public boolean isStop() {
        return mIsStop;
    }

    public void startUp() {
        mIsStop = false;
        checkToStart();
    }

    @Override
    public void run() {
        synchronized (mLock) {
            while (!mIsFinish) {
                try {
                    if (mUsedTime >= mSumTime) {
                        mUsedTime = mSumTime;
                        if (mSeekChangeListener != null && mMovieMakerTextureView != null) {
                            mMovieMakerTextureView.post(() -> mSeekChangeListener.actionFinish());
                            mMovieMakerTextureView.post((() -> mSeekChangeListener.actualFinishAt(mUsedTime)));
                        }
                        mLock.wait();
                    }
                    if (mIsStop) {
                        if (mSeekChangeListener != null && mMovieMakerTextureView != null) {
                            mMovieMakerTextureView.post((() -> mSeekChangeListener.actualFinishAt(mUsedTime)));
                        }
                        mLock.wait();
                    }
                    long startTime = System.currentTimeMillis();
                    mMovieMakerTextureView.requestRender();
                    mLock.wait();
                    if (!mIsRecording)
                        Thread.sleep(Math.max(0, 1000 / RECORDFPS - (System.currentTimeMillis() - startTime)));//睡眠
                    if (!mIsBackGround)
                        mUsedTime = mUsedTime + (mIsRecording ? 1000 / RECORDFPS : System.currentTimeMillis() - startTime);
                    else
                        mIsBackGround = false;
                    mUsedTime = mUsedTime >= mSumTime ? mSumTime : mUsedTime;
                    if (mSeekChangeListener != null && mMovieMakerTextureView != null) {
                        mMovieMakerTextureView.post((() -> mSeekChangeListener.seekChange(mUsedTime)));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onPause() {
        mMovieMakerTextureView.onPause();
    }

    @Override
    public void onResume() {
        mMovieMakerTextureView.onResume();
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
        mMovieMakerTextureView = null;
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
        mMovieMakerTextureView.requestRender();
    }

    public void requestRender() {
        setManual(true);
        mMovieMakerTextureView.requestRender();
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
    public void onSurfaceCreated(WindowSurface windowSurface, EglCore eglCore) {
        mIRendererWorker.onSurfaceCreated(windowSurface, eglCore);
        mIRendererWorker.onSurfaceChanged(windowSurface, windowSurface.getWidth(), windowSurface.getHeight());
        mTextureViewReadyOk = true;
        checkToStart();
    }

    @Override
    public void onSurfaceChanged(WindowSurface windowSurface, int width, int height) {
        mIRendererWorker.onSurfaceChanged(windowSurface, width, height);
    }

    private void checkToStart() {
        if (!mTextureViewReadyOk || mIsStop) return;
        setManual(false);
        synchronized (mLock) {
            mLock.notify();
        }
    }

    @Override
    public void onDrawFrame(WindowSurface windowSurface) {
        if (!mIsManual) {
            if (isStop()) {
                synchronized (mLock) {
                    mLock.notify();
                }
            }
            mIRendererWorker.drawFrame(mContext, windowSurface, mUsedTime);
            synchronized (mLock) {
                mLock.notify();
            }
        } else {
            mIRendererWorker.drawFrame(mContext, windowSurface, mUsedTime);
        }

    }

    @Override
    public void onSurfaceDestroy() {
        mIRendererWorker.onSurfaceDestroy();
    }
}
