package com.loopeer.android.photodrama4android.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.loopeer.android.photodrama4android.R;

public class TextProgressBar extends FrameLayout {

    private TextView mTextView;
    private ProgressBar mProgressbar;
    protected float mRatioX;
    protected float mRatioY;

    public TextProgressBar(Context context) {
        super(context);
    }

    public TextProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        getAttrs(context,attrs,0);
        init();
    }

    public TextProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        getAttrs(context,attrs,defStyle);
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
        View view = LayoutInflater.from(getContext())
            .inflate(R.layout.view_text_progressbar, this, true);
        mProgressbar = (ProgressBar) view.findViewById(R.id.progressBar);
        mTextView = (TextView) view.findViewById(R.id.text);
    }

    public void showProgress(){
        mProgressbar.setVisibility(VISIBLE);
    }

    public void dismissProgress(){
        mProgressbar.setVisibility(INVISIBLE);
    }

    public void setText(String text){
        dismissProgress();
        mTextView.setText(text);
    }

    public void setText(@StringRes int resId){
        dismissProgress();
        mTextView.setText(resId);
    }
}