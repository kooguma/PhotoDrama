package com.loopeer.android.photodrama4android.media;

import android.graphics.SurfaceTexture;
import android.util.Log;
import android.view.TextureView;

import com.loopeer.android.photodrama4android.BuildConfig;
import com.loopeer.android.photodrama4android.media.recorder.gles.EglCore;
import com.loopeer.android.photodrama4android.media.recorder.gles.WindowSurface;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class TextureRenderer extends Thread implements TextureView.SurfaceTextureListener {
    private static final String TAG = "GLThreadRender Texture";
    public static final boolean DEBUG = BuildConfig.DEBUG;

    private Object mLock = new Object();
    private Object mLockRender = new Object();
    private SurfaceTexture mSurfaceTexture;
    private EglCore mEglCore;
    private boolean mDone;
    private Renderer mRenderer;
    private WindowSurface mWindowSurface;
    private boolean mSizeChanged = true;
    private boolean mRequestRender = true;

    public TextureRenderer() {
        super("TextureViewGL TextureRenderer");
    }

    public void setRenderer(Renderer renderer) {
        mRenderer = renderer;
    }

    @Override
    public void run() {

        synchronized (mLock) {
            while (!mDone && mSurfaceTexture == null) {
                try {
                    mLock.wait();
                    if (DEBUG) {
                        Log.e(TAG, "mEglCore = new EglCore");
                    }
                    if (mSurfaceTexture != null) {
                        mEglCore = new EglCore(null, EglCore.FLAG_RECORDABLE | EglCore.FLAG_TRY_GLES3);
                        mWindowSurface = new WindowSurface(mEglCore, mSurfaceTexture);
                        mWindowSurface.makeCurrent();
                        mRenderer.onSurfaceCreated(mWindowSurface, mEglCore);
                    }
                } catch (InterruptedException ie) {
                    throw new RuntimeException(ie);     // not expected
                }
            }
            Log.e(TAG, "out while (!mDone && mSurfaceTexture == null) ");

        /*    if (mDone) {
                break;
            }*/
        }

        while (!mDone) {

            if (DEBUG) {
                Log.e(TAG, "mSizeChanged : mRequestRender  " + mSizeChanged + " : " + mRequestRender);
            }
            if (mSizeChanged) {
                mRenderer.onSurfaceChanged(mWindowSurface, mWindowSurface.getWidth(), mWindowSurface.getHeight());
                mSizeChanged = false;
            }
            if (DEBUG) {
                Log.e(TAG, "mRenderer is request" + mRequestRender);
            }
            if (mRequestRender) {
                mRenderer.onDrawFrame(mWindowSurface);
                mRequestRender = false;
            }

            synchronized (mLock) {
                try {
                    if (DEBUG) {
                        Log.e(TAG, "mLock.wait() wait");
                    }
                    mLock.wait();
                    if (DEBUG) {
                        Log.e(TAG, "mLock.wait() start");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if (mWindowSurface != null) mWindowSurface.release();
        if (mEglCore != null) mEglCore.release();
        if (mSurfaceTexture != null) mSurfaceTexture.release();
    }

    public void requestRender() {
        if (DEBUG) {
            Log.e(TAG, "requestRender");
        }
        synchronized (mLock) {
            if (DEBUG) {
                Log.e(TAG, "notifyAll");
            }
            mRequestRender = true;
            mLock.notifyAll();
        }
    }

    public void sizeChanged() {
        synchronized (mLock) {
            mSizeChanged = true;
            mRequestRender = true;
            mLock.notifyAll();
        }
    }

    public void finish() {
        synchronized (mLock) {

            if (DEBUG) {
                Log.e(TAG, "mDone  true");
            }
            mDone = true;
            mLock.notify();
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture st, int width, int height) {
        synchronized (mLock) {
            mSurfaceTexture = st;
            mLock.notify();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture st, int width, int height) {
        sizeChanged();
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture st) {
        synchronized (mLock) {
            mSurfaceTexture = null;
        }
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture st) {
    }

    public interface Renderer {
        void onSurfaceCreated(WindowSurface windowSurface, EglCore eglCore);

        void onSurfaceChanged(WindowSurface windowSurface, int width, int height);

        void onDrawFrame(WindowSurface windowSurface);
    }
}