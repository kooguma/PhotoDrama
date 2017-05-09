package com.loopeer.android.photodrama4android.media;


import android.content.Context;
import android.content.res.TypedArray;
import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.TextureView;
import android.view.ViewGroup;

import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.media.recorder.gles.EglCore;
import com.loopeer.android.photodrama4android.media.recorder.gles.WindowSurface;

public class MovieMakerTextureView extends TextureView {
    protected float mRatioX;
    protected float mRatioY;

    public MovieMakerTextureView(Context context) {
        super(context);
    }

    public MovieMakerTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);

        getAttrs(context, attrs, 0);
        setOpaque(false);
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

}
