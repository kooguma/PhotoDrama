package com.loopeer.android.photodrama4android.opengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.EGL14;
import android.os.Bundle;
import android.os.Message;

import com.loopeer.android.librarys.imagegroupview.utils.ImageUtils;
import com.loopeer.android.photodrama4android.opengl.cache.BitmapFactory;
import com.loopeer.android.photodrama4android.opengl.model.ImageInfo;
import com.loopeer.android.photodrama4android.opengl.model.SubtitleInfo;
import com.loopeer.android.photodrama4android.opengl.utils.TextureHelper;

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
            handleSubtitleTexture();
        }
    }

    private void handleSubtitleTexture() {
        if (mHandlerWrappers.isEmpty()) return;
        HandlerWrapper<SubtitleInfo, SubtitleInfo> handlerWrapper = mHandlerWrappers.get(0);
        if (handlerWrapper.getType() == HandlerWrapper.TYPE_LOAD_SUBTITLE) {
            SubtitleInfo subtitleInfo = TextureHelper.loadTexture(mContext, handlerWrapper.getData());
            returnSubtitleInfo(handlerWrapper, subtitleInfo);
            mHandlerWrappers.remove(handlerWrapper);
        }
    }

    private void handleImageTexture() {
        if (mHandlerWrappers.isEmpty()) return;
        HandlerWrapper<String, ImageInfo> handlerWrapper = mHandlerWrappers.get(0);
        if (handlerWrapper.getType() == HandlerWrapper.TYPE_LOAD_IMAGE) {
//            ImageInfo imageInfo = TextureHelper.loadTexture(mContext, handlerWrapper.getData());
            ImageInfo imageInfo = null;
            if (BitmapFactory.getInstance().getBitmapFromMemCache(handlerWrapper.getData()) != null) {
                returnImageInfo(handlerWrapper, imageInfo);
                mHandlerWrappers.remove(handlerWrapper);
                return;
            }
            final Bitmap bitmap = ImageUtils.imageZoomByScreen(mContext, handlerWrapper.getData());
            BitmapFactory.getInstance().addBitmapToCache(handlerWrapper.getData(), bitmap);
            returnImageInfo(handlerWrapper, imageInfo);
//            ImageTextureCache.getDefault().putImageInfo(handlerWrapper.getKey(), imageInfo);
            mHandlerWrappers.remove(handlerWrapper);
        }

    }

    private void returnImageInfo(HandlerWrapper<String, ImageInfo> handlerWrapper, ImageInfo imageInfo) {
        Message msg = new Message();
        Bundle b = new Bundle();
        b.putSerializable(handlerWrapper.getKey(), imageInfo);
        msg.setData(b);
        handlerWrapper.sendMessage(msg);
    }

    private void returnSubtitleInfo(HandlerWrapper<SubtitleInfo, SubtitleInfo> handlerWrapper, SubtitleInfo subtitleInfo) {
        Message msg = new Message();
        Bundle b = new Bundle();
        b.putSerializable(handlerWrapper.getKey(), subtitleInfo);
        msg.setData(b);
        handlerWrapper.sendMessage(msg);
    }

    public synchronized void loadImageTexture(HandlerWrapper<String, ImageInfo> imageInfoHandlerWrapper) {
       /* ImageInfo imageInfo;
        if ((imageInfo = ImageTextureCache.getDefault().getImageInfo(imageInfoHandlerWrapper.getKey())) != null) {
            returnImageInfo(imageInfoHandlerWrapper, imageInfo);
            return;
        }*/
        mHandlerWrappers.add(imageInfoHandlerWrapper);
        if (isAlive())
            notify();
    }

    public synchronized void loadSubtitleTexture(HandlerWrapper<SubtitleInfo, SubtitleInfo> handlerWrapper) {
        mHandlerWrappers.add(handlerWrapper);
        if (isAlive())
            notify();
    }

    public void finish() {
        mIsFinish = true;
    }
}