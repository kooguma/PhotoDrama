package com.loopeer.android.photodrama4android.ui.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.laputapp.utilities.DeviceScreenUtils;
import com.loopeer.android.photodrama4android.BuildConfig;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.media.TextureRenderer;

public class SimpleImageMovieHolderView extends SimpleDraweeView {

    private static final String TAG = "TextureRenderer View";
    public static final boolean DEBUG = BuildConfig.DEBUG;

    protected float mRatioX;
    protected float mRatioY;

    public SimpleImageMovieHolderView(Context context) {
        this(context, null);
    }

    public SimpleImageMovieHolderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleImageMovieHolderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        getAttrs(context, attrs, 0);

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
            int childWidthSize = getMeasuredWidth();
            if (childWidthSize == Math.max(DeviceScreenUtils.getScreenHeight(getContext()), DeviceScreenUtils.getScreenWidth(getContext()))) {
                int childHeightSize = Math.min(DeviceScreenUtils.getScreenHeight(getContext())
                        , DeviceScreenUtils.getScreenWidth(getContext()));
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeightSize, MeasureSpec.EXACTLY);
                widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) (1f * childHeightSize * mRatioX / mRatioY), MeasureSpec.EXACTLY);
            } else {
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
                heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (1f * childWidthSize * mRatioY / mRatioX), MeasureSpec.EXACTLY);
            }
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

}
