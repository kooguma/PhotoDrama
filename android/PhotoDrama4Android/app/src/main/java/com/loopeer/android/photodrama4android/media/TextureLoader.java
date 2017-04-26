package com.loopeer.android.photodrama4android.media;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.EGL14;
import android.os.Bundle;
import android.os.Message;

import com.loopeer.android.photodrama4android.media.cache.BitmapFactory;
import com.loopeer.android.photodrama4android.media.model.ImageInfo;
import com.loopeer.android.photodrama4android.media.model.SubtitleInfo;
import com.loopeer.android.photodrama4android.media.utils.TextureHelper;
import com.loopeer.android.photodrama4android.utils.LocalImageUtils;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

public class TextureLoader extends Thread {

    private EGLContext textureContext;
    private EGL10 egl;
    private EGLConfig eglConfig;
    private EGLDisplay display;
    private Context mContext;

    private boolean mIsFinish;

    public List<HandlerWrapper> mHandlerWrappers = new ArrayList<>();

    public TextureLoader() {
    }

    public TextureLoader(EGL10 egl, EGLContext renderContext, EGLDisplay display,
                         EGLConfig eglConfig, Context androidContext) {
        //update(egl, renderContext, display, eglConfig, androidContext,);
    }

    public void update(EGL10 egl, EGLContext renderContext, EGLDisplay display,
                       EGLConfig eglConfig, Context androidContext,int[] contextAttributes) {
        this.egl = egl;
        this.display = display;
        this.eglConfig = eglConfig;
        this.mContext = androidContext;

        textureContext = egl.eglCreateContext(display, eglConfig, renderContext, contextAttributes);
    }

    public void run() {
        int pbufferAttribs[] = {EGL14.EGL_WIDTH, 1, EGL14.EGL_HEIGHT, 1, EGL14.EGL_TEXTURE_TARGET,
                EGL14.EGL_NO_TEXTURE, EGL14.EGL_TEXTURE_FORMAT, EGL14.EGL_NO_TEXTURE,
                EGL14.EGL_NONE};

        EGLSurface localSurface = egl.eglCreatePbufferSurface(display, eglConfig, pbufferAttribs);

        egl.eglMakeCurrent(display, localSurface, localSurface, textureContext);

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
        if (BitmapFactory.getInstance().getBitmapFromMemCache(imageInfoHandlerWrapper.getData()) != null) {
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
}