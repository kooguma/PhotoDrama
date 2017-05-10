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

    public GLThreadRender(Context context, TextureView textureView, IRendererWorker iRendererWorker) {
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
                            mMovieMakerTextureView.post(new Runnable() {
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
                    mMovieMakerTextureView.requestRender();
                    this.wait();

                    if (DEBUG) {
                        Log.e(TAG, "this.wait(); start");
                    }

                    if (DEBUG) {
//                        Log.e(TAG, "sleep Time " + (1000 / RECORDFPS - (System.currentTimeMillis() - startTime)));
                    }
                    if (!mIsRecording)
                        Thread.sleep(Math.max(0, 1000 / RECORDFPS - (System.currentTimeMillis() - startTime)));//睡眠
                    if (!mIsBackGround)
                        mUsedTime = mUsedTime + (mIsRecording ? 1000 / RECORDFPS : System.currentTimeMillis() - startTime);
                    else
                        mIsBackGround = false;
                    if (mSeekChangeListener != null) {
                        mMovieMakerTextureView.post((new Runnable() {
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

    public void setManualUpSeekBar(long usedTime) {
        if (!mIsManual)
            return;
        this.mUsedTime = usedTime;
        mMovieMakerTextureView.requestRender();
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
        mMovieMakerTextureView.onDestroy();
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
    }

    @Override
    public void onSurfaceChanged(WindowSurface windowSurface, int width, int height) {
        mIRendererWorker.onSurfaceChanged(windowSurface, width, height);
    }

    @Override
    public void onDrawFrame(WindowSurface windowSurface) {
        if (!mIsManual) {
            synchronized (this) {
                mIRendererWorker.drawFrame(mContext, windowSurface, mUsedTime);
                this.notify();

                if (DEBUG) {
                    Log.e(TAG, "onDrawFrame notify");
                }
            }
        } else {

            if (DEBUG) {
                Log.e(TAG, "onDrawFrame mIsManual : " + mIsManual);
            }
            mIRendererWorker.drawFrame(mContext, windowSurface, mUsedTime);
        }
    }
}
