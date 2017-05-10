package com.loopeer.android.photodrama4android.media;

import android.graphics.SurfaceTexture;
import android.util.Log;
import android.view.TextureView;

import com.loopeer.android.photodrama4android.media.recorder.gles.EglCore;
import com.loopeer.android.photodrama4android.media.recorder.gles.WindowSurface;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class TextureRenderer extends Thread implements TextureView.SurfaceTextureListener {
    private static final String TAG = "TextureRenderer";
    private Object mLock = new Object();        // guards mSurfaceTexture, mDone
    private SurfaceTexture mSurfaceTexture;
    private EglCore mEglCore;
    private boolean mDone;
    private Renderer mRenderer;
    private WindowSurface mWindowSurface;

    public TextureRenderer() {
        super("TextureViewGL TextureRenderer");
    }

    public void setRenderer(Renderer renderer) {
        mRenderer = renderer;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (mLock) {
                while (!mDone && mSurfaceTexture == null) {
                    try {
                        mLock.wait();
                        if (mSurfaceTexture != null) {
                            mEglCore = new EglCore(null, EglCore.FLAG_TRY_GLES3);
                            mWindowSurface = new WindowSurface(mEglCore, mSurfaceTexture);
                            mWindowSurface.makeCurrent();
                            mRenderer.onSurfaceCreated(mWindowSurface, mEglCore);
                        }
                    } catch (InterruptedException ie) {
                        throw new RuntimeException(ie);     // not expected
                    }
                }
                if (mDone) {
                    break;
                }
            }

            mRenderer.onDrawFrame(mWindowSurface);

            synchronized (mLock) {
                try {
                    mLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            /*if (!sReleaseInCallback) {
                Log.i(TAG, "Releasing SurfaceTexture in renderer thread");
                surfaceTexture.release();
            }*/
        }
        mWindowSurface.release();
        mEglCore.release();
        Log.d(TAG, "TextureRenderer thread exiting");
    }

    public void requestRender() {
        synchronized (mLock) {
            mLock.notify();
        }
    }

    public void finish() {
        synchronized (mLock) {
            mDone = true;
            mLock.notify();
        }
    }

    @Override   // will be called on UI thread
    public void onSurfaceTextureAvailable(SurfaceTexture st, int width, int height) {
        Log.d(TAG, "onSurfaceTextureAvailable(" + width + "x" + height + ")");
        synchronized (mLock) {
            mSurfaceTexture = st;
            mLock.notify();
        }
    }

    @Override   // will be called on UI thread
    public void onSurfaceTextureSizeChanged(SurfaceTexture st, int width, int height) {
        Log.d(TAG, "onSurfaceTextureSizeChanged(" + width + "x" + height + ")");

        mRenderer.onSurfaceChanged(mWindowSurface, mWindowSurface.getWidth(), mWindowSurface.getHeight());
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture st) {
        Log.d(TAG, "onSurfaceTextureDestroyed");
        synchronized (mLock) {
            mSurfaceTexture = null;
        }
        /*if (sReleaseInCallback) {
            Log.i(TAG, "Allowing TextureView to release SurfaceTexture");
        }
        return sReleaseInCallback;*/
        return false;
    }

    @Override   // will be called on UI thread
    public void onSurfaceTextureUpdated(SurfaceTexture st) {
        //Log.d(TAG, "onSurfaceTextureUpdated");
    }

    public interface Renderer {
        void onSurfaceCreated(WindowSurface windowSurface, EglCore eglCore);
        void onSurfaceChanged(WindowSurface windowSurface, int width, int height);
        void onDrawFrame(WindowSurface windowSurface);
    }
}