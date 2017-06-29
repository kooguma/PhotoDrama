package com.loopeer.android.photodrama4android.ui.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.laputapp.utilities.DeviceScreenUtils;
import com.loopeer.android.photodrama4android.BuildConfig;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.media.cache.BitmapFactory;
import static com.loopeer.android.photodrama4android.media.model.ImageClip.BRIGHTNESS_VALUE;

public class ImageSelectPreviewView extends View implements BitmapFactory.ImageLoadListener {

    public static final boolean DEBUG = BuildConfig.DEBUG;

    protected float mRatioX;
    protected float mRatioY;
    private String mLocalUrl;

    private Bitmap mBitmap;
    private Bitmap mBlurBitmap;
    private float mViewScaleFactor;
    private float mBlurViewScaleFactor;

    public ImageSelectPreviewView(Context context) {
        this(context, null);
    }

    public ImageSelectPreviewView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageSelectPreviewView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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

    public void setLocalUrl(final String localUrl) {
        mLocalUrl = localUrl;
        if (mLocalUrl == null) {
            invalidate();
        } else {
            BitmapFactory.getInstance().loadImage(this, mLocalUrl);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mLocalUrl != null && BitmapFactory.getInstance().contains(mLocalUrl) && getHeight() != 0) {
            Matrix matrix = new Matrix();
            matrix.postScale(mViewScaleFactor, mViewScaleFactor);
            matrix.postTranslate(-1f * mViewScaleFactor * mBitmap.getWidth() / 2, -1f * mViewScaleFactor * mBitmap.getHeight() / 2);
            matrix.postTranslate(getWidth() / 2, getHeight() / 2);
            ColorMatrix imageMatrix = new ColorMatrix();
            imageMatrix.setScale(BRIGHTNESS_VALUE, BRIGHTNESS_VALUE, BRIGHTNESS_VALUE, 1);
            Paint blurImagePaint = new Paint();
            blurImagePaint.setColorFilter(new ColorMatrixColorFilter(imageMatrix));
            Matrix blurMatrix = new Matrix();
            blurMatrix.postScale(mBlurViewScaleFactor, mBlurViewScaleFactor);
            blurMatrix.postTranslate(-1f * (mBlurViewScaleFactor * mBitmap.getWidth() / 2 - getWidth() / 2)
                    , -1f * (mBlurViewScaleFactor * mBitmap.getHeight() / 2 - getHeight() / 2));
            if (mBlurBitmap.isRecycled()) return;
            canvas.drawBitmap(mBlurBitmap, blurMatrix, blurImagePaint);

            Paint localPaint = new Paint();
            localPaint.setFilterBitmap(true);
            if (mBitmap.isRecycled()) return;
            canvas.drawBitmap(mBitmap, matrix, localPaint);
        }
    }

    @Override
    public void loadSuccess() {
        mBitmap = BitmapFactory.getInstance().getBitmapFromMemCache(mLocalUrl);
        mBlurBitmap = BitmapFactory.getInstance().getBlurBitmapFromCache(mLocalUrl, mBitmap);
        if (mBitmap == null || mBlurBitmap == null) return;
        if (1f * mBitmap.getWidth() / mBitmap.getHeight() > 1f * getWidth() / getHeight()) {
            mViewScaleFactor = 1f * getWidth() / mBitmap.getWidth();
            mBlurViewScaleFactor = 1f * getHeight() / mBitmap.getHeight();
        } else {
            mViewScaleFactor = 1f * getHeight() / mBitmap.getHeight();
            mBlurViewScaleFactor = 1f * getWidth() / mBitmap.getWidth();
        }
        invalidate();
    }
}
