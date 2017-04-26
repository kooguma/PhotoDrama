package com.loopeer.android.photodrama4android.ui.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.loopeer.android.photodrama4android.R;

public class VideoRecordCover extends View {
    protected float mRatioX;
    protected float mRatioY;

    public VideoRecordCover(Context context) {
        this(context, null);
    }

    public VideoRecordCover(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoRecordCover(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttrs(context, attrs, defStyleAttr);
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
            int childWidthSize = (getMeasuredWidth());
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (1f * childWidthSize * mRatioY / mRatioX), MeasureSpec.EXACTLY);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

}
