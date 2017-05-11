package com.loopeer.android.photodrama4android.media;

import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.TextureView;

import com.loopeer.android.photodrama4android.BuildConfig;
import com.loopeer.android.photodrama4android.media.recorder.gles.EglCore;
import com.loopeer.android.photodrama4android.media.recorder.gles.WindowSurface;

import java.lang.ref.WeakReference;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class TextureRenderer extends Thread implements TextureView.SurfaceTextureListener {
    private static final String TAG = "TextureRenderer";
    public static final boolean DEBUG = BuildConfig.DEBUG;

    private EglCore mEglCore;
    private Renderer mRenderer;
    private WindowSurface mWindowSurface;
    private volatile RenderHandler mHandler;
    private Object mStartLock = new Object();
    private boolean mReady = false;

    public TextureRenderer() {
        super("TextureRenderer");
    }

    public void setRenderer(Renderer renderer) {
        mRenderer = renderer;
    }

    @Override
    public void run() {
        Looper.prepare();

        // We need to create the Handler before reporting ready.
        mHandler = new RenderHandler(this);
        synchronized (mStartLock) {
            mReady = true;
            mStartLock.notify();    // signal waitUntilReady()
        }

        // Prepare EGL and open the camera before we start handling messages.
        mEglCore = new EglCore(null, 0);
        Looper.loop();

        releaseGl();
        mEglCore.release();

        synchronized (mStartLock) {
            mReady = false;
        }
    }

    private void releaseGl() {
        if (mWindowSurface != null) {
            mWindowSurface.release();
            mWindowSurface = null;
        }

        mEglCore.makeNothingCurrent();
    }

    public void waitUntilReady() {
        synchronized (mStartLock) {
            while (!mReady) {
                try {
                    mStartLock.wait();
                } catch (InterruptedException ie) { /* not expected */ }
            }
        }
    }

    public void requestRender() {
        mHandler.sendRedraw();
    }

    public void finish() {
        mHandler.sendShutdown();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture st, int width, int height) {
        mHandler.sendSurfaceAvailable(st, width, height);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture st, int width, int height) {
        mHandler.sendSurfaceChanged(width, height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture st) {
        mHandler.sendSurfaceDestroyed();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture st) {
    }

    private void draw() {
        if (mWindowSurface == null) return;
        mRenderer.onDrawFrame(mWindowSurface);
        mWindowSurface.swapBuffers();
    }

    private void frameAvailable() {

    }

    private void shutdown() {
        releaseGl();
    }

    private void surfaceDestroyed() {
        releaseGl();
    }

    private void surfaceChanged(int width, int height) {
        mRenderer.onSurfaceChanged(mWindowSurface, width, height);
    }

    private void surfaceAvailable(SurfaceTexture surfaceTexture, boolean b) {
        mEglCore = new EglCore(null, EglCore.FLAG_RECORDABLE | EglCore.FLAG_TRY_GLES3);
        mWindowSurface = new WindowSurface(mEglCore, surfaceTexture);
        mWindowSurface.makeCurrent();
        mRenderer.onSurfaceCreated(mWindowSurface, mEglCore);    }

    public RenderHandler getHandler() {
        return mHandler;
    }

    public interface Renderer {
        void onSurfaceCreated(WindowSurface windowSurface, EglCore eglCore);

        void onSurfaceChanged(WindowSurface windowSurface, int width, int height);

        void onDrawFrame(WindowSurface windowSurface);
    }

    private static class RenderHandler extends Handler {
        private static final int MSG_SURFACE_AVAILABLE = 0;
        private static final int MSG_SURFACE_CHANGED = 1;
        private static final int MSG_SURFACE_DESTROYED = 2;
        private static final int MSG_SHUTDOWN = 3;
        private static final int MSG_FRAME_AVAILABLE = 4;
        private static final int MSG_ZOOM_VALUE = 5;
        private static final int MSG_SIZE_VALUE = 6;
        private static final int MSG_ROTATE_VALUE = 7;
        private static final int MSG_POSITION = 8;
        private static final int MSG_REDRAW = 9;

        private WeakReference<TextureRenderer> mWeakRenderThread;

        public RenderHandler(TextureRenderer rt) {
            mWeakRenderThread = new WeakReference<TextureRenderer>(rt);
        }

        public void sendSurfaceAvailable(SurfaceTexture st, int width, int height) {
            sendMessage(obtainMessage(MSG_SURFACE_AVAILABLE, width, height, st));
        }

        public void sendSurfaceChanged(int width,
                                       int height) {
            sendMessage(obtainMessage(MSG_SURFACE_CHANGED, width, height));
        }

        public void sendSurfaceDestroyed() {
            sendMessage(obtainMessage(MSG_SURFACE_DESTROYED));
        }

        public void sendShutdown() {
            sendMessage(obtainMessage(MSG_SHUTDOWN));
        }

        public void sendFrameAvailable() {
            sendMessage(obtainMessage(MSG_FRAME_AVAILABLE));
        }

        public void sendPosition(int x, int y) {
            sendMessage(obtainMessage(MSG_POSITION, x, y));
        }

        public void sendRedraw() {
            sendMessage(obtainMessage(MSG_REDRAW));
        }

        @Override  // runs on RenderThread
        public void handleMessage(Message msg) {
            int what = msg.what;
            //Log.d(TAG, "RenderHandler [" + this + "]: what=" + what);

            TextureRenderer renderThread = mWeakRenderThread.get();
            if (renderThread == null) {
                Log.w(TAG, "RenderHandler.handleMessage: weak ref is null");
                return;
            }

            switch (what) {
                case MSG_SURFACE_AVAILABLE:
                    renderThread.surfaceAvailable((SurfaceTexture) msg.obj, msg.arg1 != 0);
                    break;
                case MSG_SURFACE_CHANGED:
                    renderThread.surfaceChanged(msg.arg1, msg.arg2);
                    break;
                case MSG_SURFACE_DESTROYED:
                    renderThread.surfaceDestroyed();
                    break;
                case MSG_SHUTDOWN:
                    renderThread.shutdown();
                    break;
                case MSG_FRAME_AVAILABLE:
                    renderThread.frameAvailable();
                    break;
                case MSG_REDRAW:
                    renderThread.draw();
                    break;
                default:
                    throw new RuntimeException("unknown message " + what);
            }
        }
    }

}