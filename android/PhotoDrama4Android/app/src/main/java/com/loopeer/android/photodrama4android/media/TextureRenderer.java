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
    private static final String TAG = "TextureRenderer";
    public static final boolean DEBUG = BuildConfig.DEBUG;

    private Object mLock = new Object();
    private SurfaceTexture mSurfaceTexture;
    private EglCore mEglCore;
    private boolean mDone;
    private Renderer mRenderer;
    private WindowSurface mWindowSurface;
    private boolean mSizeChanged = true;
    private boolean mRequestRender = true;

    public TextureRenderer() {
        super("TextureRenderer");
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
        }

        synchronized (mLock) {
            while (!mDone) {
                if (mSizeChanged) {
                    mRenderer.onSurfaceChanged(mWindowSurface, mWindowSurface.getWidth(), mWindowSurface.getHeight());
                    mSizeChanged = false;
                }
                if (mRequestRender) {
                    mRenderer.onDrawFrame(mWindowSurface);
                    if (DEBUG) {
                        Log.e(TAG, "Thread : " + Thread.currentThread().getName() + "     " + "swapBuffers start");
                    }
                    mWindowSurface.swapBuffers();
                    if (DEBUG) {
                        Log.e(TAG, "Thread : " + Thread.currentThread().getName() + "     " + "swapBuffers end");
                    }
                    mRequestRender = false;
                }

                try {
                    mLock.wait();
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
            Log.e(TAG, "Thread : " + Thread.currentThread().getName() + "     " + "requestRender");
        }
        synchronized (mLock) {
            if (DEBUG) {
                Log.e(TAG, "Thread : " + Thread.currentThread().getName() + "     " + "requestRender getLock");
            }
            mRequestRender = true;
            mLock.notifyAll();

            if (DEBUG) {
                Log.e(TAG, "Thread : " + Thread.currentThread().getName() + "     " + "requestRender releaseLock");
            }
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