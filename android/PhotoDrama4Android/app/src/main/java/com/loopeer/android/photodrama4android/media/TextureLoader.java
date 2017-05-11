package com.loopeer.android.photodrama4android.media;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.opengl.EGLSurface;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.loopeer.android.photodrama4android.media.cache.BitmapFactory;
import com.loopeer.android.photodrama4android.media.model.ImageInfo;
import com.loopeer.android.photodrama4android.media.model.SubtitleInfo;
import com.loopeer.android.photodrama4android.media.recorder.gles.EglCore;
import com.loopeer.android.photodrama4android.media.recorder.gles.WindowSurface;
import com.loopeer.android.photodrama4android.utils.LocalImageUtils;

import java.util.ArrayList;
import java.util.List;

public class TextureLoader extends Thread {

    private static final String TAG = "TextureLoader";
    private Context mContext;
    private WindowSurface mWindowSurface;
    private EglCore mEglCore;
    private EGLContext mEGLContext;

    private boolean mIsFinish;

    public List<HandlerWrapper> mHandlerWrappers = new ArrayList<>();

    public TextureLoader() {
    }

    public TextureLoader(Context context) {
        mContext = context;
    }

    public void run() {

        EglCore eglCore = new EglCore(mEglCore.mEGLContext, EglCore.FLAG_TRY_GLES3);
        EGLSurface eglSurface = eglCore.createOffscreenSurface(1, 1);
        eglCore.makeCurrent(eglSurface);
        while (!mIsFinish) {
            synchronized (this) {
                try {
                    if (mHandlerWrappers.isEmpty())
                        this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            handleImageTexture();
            handleImageResTexture();
        }
    }

    private void checkEglError(String msg) {
        int error;
        if ((error = EGL14.eglGetError()) != EGL14.EGL_SUCCESS) {
            throw new RuntimeException(msg + ": EGL error: 0x" + Integer.toHexString(error));
        }
    }

    private void handleImageTexture() {
        if (mHandlerWrappers.isEmpty()) return;
        HandlerWrapper<String, ImageInfo> handlerWrapper = mHandlerWrappers.get(0);
        if (handlerWrapper != null && handlerWrapper.getType() == HandlerWrapper.TYPE_LOAD_IMAGE) {
            ImageInfo imageInfo = null;
            if (BitmapFactory.getInstance().getBitmapFromMemCache(handlerWrapper.getData()) != null) {
                returnImageInfo(handlerWrapper, imageInfo);
                mHandlerWrappers.remove(handlerWrapper);
                return;
            }
            final Bitmap bitmap = LocalImageUtils.imageZoomByScreen(mContext, handlerWrapper.getData());
            BitmapFactory.getInstance().addBitmapToCache(handlerWrapper.getData(), bitmap);
            returnImageInfo(handlerWrapper, imageInfo);
            mHandlerWrappers.remove(handlerWrapper);
        }
    }

    private void handleImageResTexture() {
        if (mHandlerWrappers.isEmpty()) return;
        HandlerWrapper<Integer, ImageInfo> handlerWrapper = mHandlerWrappers.get(0);
        if (handlerWrapper != null && handlerWrapper.getType() == HandlerWrapper.TYPE_LOAD_IMAGE_RES) {
            ImageInfo imageInfo = null;
            if (BitmapFactory.getInstance().getBitmapFromMemCache(String.valueOf(handlerWrapper.getData())) != null) {
                returnImageInfo(handlerWrapper, imageInfo);
                mHandlerWrappers.remove(handlerWrapper);
                return;
            }
            final Bitmap bitmap = LocalImageUtils.imageZoomByScreen(mContext, handlerWrapper.getData());
            BitmapFactory.getInstance().addBitmapToCache(String.valueOf(handlerWrapper.getData()), bitmap);
            returnImageInfo(handlerWrapper, imageInfo);
            mHandlerWrappers.remove(handlerWrapper);
        }
    }

    private void returnImageInfo(HandlerWrapper<?, ImageInfo> handlerWrapper, ImageInfo imageInfo) {
        Message msg = new Message();
        Bundle b = new Bundle();
        b.putSerializable(handlerWrapper.getKey(), imageInfo);
        msg.setData(b);
        handlerWrapper.sendMessage(msg);
    }

    public synchronized void loadImageTexture(HandlerWrapper<String, ImageInfo> imageInfoHandlerWrapper) {
        ImageInfo imageInfo = null;
        if (BitmapFactory.getInstance().contains(String.valueOf(imageInfoHandlerWrapper.getData()))) {
            returnImageInfo(imageInfoHandlerWrapper, imageInfo);
            return;
        }
        mHandlerWrappers.add(imageInfoHandlerWrapper);
        if (isAlive())
            notify();
    }

    public void finish() {
        mIsFinish = true;
    }

    public void update(WindowSurface windowSurface, EglCore eglCore) {
        mWindowSurface = windowSurface;
        mEglCore = eglCore;
        mEGLContext = EGL14.eglCreateContext(mEglCore.mEGLDisplay, mEglCore.mEGLConfig, mEglCore.mEGLContext, mEglCore.getAttribList(), 0);

    }
}