package com.loopeer.android.photodrama4android.ui.widget;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.loopeer.android.photodrama4android.media.cache.BitmapFactory;

public class ScrollSelectInnerImageView extends SimpleDraweeView {

    private int mDefaultSize;
    private int mCurrentSize;
    private String mLocalUrl;

    public ScrollSelectInnerImageView(Context context) {
        super(context);
    }

    public ScrollSelectInnerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollSelectInnerImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ScrollSelectInnerImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
            int childHeightSize = getMeasuredHeight();
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightMeasureSpec, MeasureSpec.EXACTLY);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) (1f * childHeightSize * mCurrentSize / mDefaultSize), MeasureSpec.EXACTLY);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    public void updateImage(int defaultSize, int currentSize, String path) {
        mDefaultSize = defaultSize;
        mCurrentSize = currentSize;
        setLocalUrl(path);
        requestLayout();
    }

    public void setLocalUrl(final String localUrl) {
        mLocalUrl = localUrl;

        if (getHeight() == 0) {
            post(new Runnable() {
                @Override
                public void run() {
                    setLocalUrl(localUrl);
                }
            });
            return;
        }

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mLocalUrl != null && BitmapFactory.getInstance().contains(mLocalUrl)) {

            Matrix matrix = new Matrix();
            Bitmap bitmap = BitmapFactory.getInstance().getBitmapFromMemCache(mLocalUrl);
            Bitmap localBitmap = Bitmap.createBitmap(getHeight(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas localCanvas = new Canvas(localBitmap);
            matrix.postTranslate(-1f * bitmap.getWidth() / 2, -1f * bitmap.getHeight() / 2);
            if (1f * bitmap.getHeight() / bitmap.getWidth() > 1f * getHeight() / getHeight()) {
                matrix.postScale(1f * getHeight() / bitmap.getWidth(), 1f * getHeight() / bitmap.getWidth());
            } else {
                matrix.postScale(1f * getHeight() / bitmap.getHeight(), 1f * getHeight() / bitmap.getHeight());
            }
            matrix.postTranslate(1f * getHeight() / 2, getHeight() / 2);
            Paint localPaint = new Paint();
            localPaint.setFilterBitmap(true);
            localCanvas.drawBitmap(bitmap, matrix, localPaint);
            int drawCount = (int) Math.ceil(1f * getWidth() / getHeight());
            Matrix drawMatrix = new Matrix();
            for (int i = 0; i < drawCount; i++) {
                canvas.drawBitmap(localBitmap, drawMatrix, localPaint);
                drawMatrix.postTranslate(getHeight(), 0f);
            }
            localBitmap.recycle();
        }
    }
}
