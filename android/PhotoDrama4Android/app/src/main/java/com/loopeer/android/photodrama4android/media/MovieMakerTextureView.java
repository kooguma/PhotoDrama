package com.loopeer.android.photodrama4android.media;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;
import android.view.ViewGroup;

import com.loopeer.android.photodrama4android.BuildConfig;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.media.recorder.gles.EglCore;
import com.loopeer.android.photodrama4android.media.recorder.gles.WindowSurface;

public class MovieMakerTextureView extends TextureView implements TextureView.SurfaceTextureListener {

    private static final String TAG = "TextureRenderer View";
    public static final boolean DEBUG = BuildConfig.DEBUG;

    protected float mRatioX;
    protected float mRatioY;
    private TextureRenderer mTextureRenderer;
    private TextureRenderer.Renderer mRenderer;
    private int mSurfaceWidth;
    private int mSurfaceHeight;

    public MovieMakerTextureView(Context context) {
        super(context);
    }

    public MovieMakerTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);

        getAttrs(context, attrs, 0);
        setOpaque(false);
        setSurfaceTextureListener(this);
    }

    private void getAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        if (attrs == null) return;
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MovieMakerTextureView, defStyleAttr, 0);
        if (a == null) return;

        mRatioX = a.getFloat(R.styleable.MovieMakerTextureView_screenRatioX, 1f);
        mRatioY = a.getFloat(R.styleable.MovieMakerTextureView_screenRatioY, 1f);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
            int childWidthSize = (getMeasuredWidth());
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (1f * childWidthSize * mRatioY / mRatioX), MeasureSpec.EXACTLY);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    public void setRenderer(TextureRenderer.Renderer renderer) {
        mRenderer = renderer;
    }

    public void requestRender() {
        if (mTextureRenderer != null) {
            mTextureRenderer.requestRender();
        }
    }

    public void onPause() {
        TextureRenderer.RenderHandler rh = mTextureRenderer.getHandler();
        rh.sendShutdown();
        mTextureRenderer = null;
    }

    public void onResume() {
        mTextureRenderer = new TextureRenderer();
        mTextureRenderer.start();
        mTextureRenderer.waitUntilReady();
        mTextureRenderer.setRenderer(mRenderer);
        if (getSurfaceTexture() != null) {
            mTextureRenderer.getHandler().sendSurfaceAvailable(getSurfaceTexture(), mSurfaceWidth, mSurfaceHeight);
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (mTextureRenderer != null) {
            mTextureRenderer.getHandler().sendSurfaceAvailable(surface, mSurfaceWidth, mSurfaceHeight);
            mSurfaceWidth = width;
            mSurfaceHeight = height;
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        if (mTextureRenderer != null) {
            mTextureRenderer.getHandler().sendSurfaceChanged(width, height);
            mSurfaceWidth = width;
            mSurfaceHeight = height;
        }
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (DEBUG) Log.e(TAG, "onSurfaceTextureDestroyed");

        if (mTextureRenderer != null) {
            mTextureRenderer.getHandler().sendSurfaceDestroyed();
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}
