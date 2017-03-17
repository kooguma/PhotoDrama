package com.loopeer.android.photodrama4android.opengl;


import android.content.Context;
import android.content.res.TypedArray;
import android.opengl.EGL14;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.loopeer.android.photodrama4android.R;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGL11;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

public class MovieMakerGLSurfaceView extends GLSurfaceView {

    private TextureLoader mTextureLoader;
    private float mRatioX;
    private float mRatioY;

    public MovieMakerGLSurfaceView(Context context) {
        super(context);

        init();
    }

    public MovieMakerGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        getAttrs(context, attrs, 0);
        init();
    }

    private void getAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        if (attrs == null) return;
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MovieMakerGLSurfaceView, defStyleAttr, 0);
        if (a == null) return;

        mRatioX = a.getFloat(R.styleable.MovieMakerGLSurfaceView_screenRatioX, 1f);
        mRatioY = a.getFloat(R.styleable.MovieMakerGLSurfaceView_screenRatioY, 1f);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
            int childWidthSize = getMeasuredWidth();
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (1f * childWidthSize * mRatioY / mRatioX), MeasureSpec.EXACTLY);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private void init() {
        mTextureLoader = new TextureLoader();
        setEGLContextFactory(new EGLContextFactory() {

            @Override
            public void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context) {
                egl.eglDestroyContext(display, context);
            }

            @Override
            public EGLContext createContext(final EGL10 egl, final EGLDisplay display,
                                            final EGLConfig eglConfig) {
                final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;
                int[] contextAttributes =
                        { EGL_CONTEXT_CLIENT_VERSION, 2, EGL14.EGL_NONE};

                EGLContext renderContext = egl.eglCreateContext(display, eglConfig,
                        EGL11.EGL_NO_CONTEXT, contextAttributes);
                mTextureLoader.update(egl, renderContext, display, eglConfig, getContext());
                if (!mTextureLoader.isAlive()) mTextureLoader.start();
                return renderContext;
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mTextureLoader.finish();
    }

    public TextureLoader getTextureLoader() {
        return mTextureLoader;
    }
}
