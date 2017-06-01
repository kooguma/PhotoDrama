package com.loopeer.android.photodrama4android.ui.widget;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.loopeer.android.librarys.imagegroupview.model.SquareImage;
import com.loopeer.android.librarys.imagegroupview.utils.ImageGroupDisplayHelper;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.media.cache.BitmapFactory;

public class LocalSquareImageView extends SimpleDraweeView implements View.OnClickListener{

    private String mLocalUrl;
    private String mUploadKey;
    private String mInternetUrl;
    private int placeholderDrawable;
    private boolean mClickUpload = true;

    public LocalSquareImageView(Context context) {
        this(context, null);
    }

    public LocalSquareImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LocalSquareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        placeholderDrawable = R.drawable.ic_image_default;
        setScaleType(ImageView.ScaleType.CENTER_CROP);
        setClickable(mClickUpload);
        setOnClickListener(this);
        GenericDraweeHierarchyBuilder builder1 = new GenericDraweeHierarchyBuilder(getContext().getResources());
        builder1.setPlaceholderImage(ContextCompat.getDrawable(getContext(), placeholderDrawable), ScalingUtils.ScaleType.CENTER_CROP);
        getControllerBuilder().build().setHierarchy(builder1.build());
        getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        invalidate();
                    }
                });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
        int childWidthSize = getMeasuredWidth();
        heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setLocalUrl(final String localUrl) {
        if (!TextUtils.isEmpty(mInternetUrl)) mInternetUrl = null;
        mLocalUrl = localUrl;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mLocalUrl != null && BitmapFactory.getInstance().contains(mLocalUrl) && getHeight() != 0) {
            canvas.clipRect(0, 0, getWidth(), getHeight());
            Matrix matrix = new Matrix();
            Bitmap bitmap = BitmapFactory.getInstance().getBitmapFromMemCache(mLocalUrl);
            Bitmap localBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas localCanvas = new Canvas(localBitmap);
            matrix.postTranslate(-1f * bitmap.getWidth() / 2, -1f * bitmap.getHeight() / 2);
            if (1f * bitmap.getHeight() / bitmap.getWidth() > 1f * getHeight() / getWidth()) {
                matrix.postScale(1f * getWidth() / bitmap.getWidth(), 1f * getWidth() / bitmap.getWidth());
            } else {
                matrix.postScale(1f * getHeight() / bitmap.getHeight(), 1f * getHeight() / bitmap.getHeight());
            }
            matrix.postTranslate(1f * getWidth() / 2, getHeight() / 2);
            Paint localPaint = new Paint();
            localPaint.setFilterBitmap(true);
            localCanvas.drawBitmap(bitmap, matrix, localPaint);
            canvas.drawBitmap(localBitmap, new Matrix(), localPaint);
            localBitmap.recycle();
        }
    }

    @SuppressWarnings("unused")
    public void setmInternetUrl(String internetUrl) {
        mInternetUrl = internetUrl;
    }

    @SuppressWarnings("unused")
    public void setUploadKey(String key) {
        mUploadKey = key;
    }

    public String getUploadImageKey() {
        return mUploadKey;
    }

    public String getLocalUrl() {
        return mLocalUrl;
    }
    public String getImageLocalUrl() {
        return mLocalUrl;
    }

    public String getInternetUrl() {
        return mInternetUrl;
    }

    public void setInternetData(final String netUrl) {
        mInternetUrl = netUrl;
        mLocalUrl = null;
        if (netUrl == null) {
            setImageResource(placeholderDrawable);
            return;
        }
        if (getHeight() == 0) {
            post(new Runnable() {
                @Override
                public void run() {
                    setInternetData(netUrl);
                }
            });
            return;
        }
        ImageGroupDisplayHelper.displayImage(this, mInternetUrl, placeholderDrawable, getWidth(), getHeight());
    }

    public void setImageData(SquareImage squareImage) {
        if (squareImage.localUrl != null) setLocalUrl(squareImage.localUrl);
        if (squareImage.interNetUrl != null) setInternetData(squareImage.interNetUrl);
        if (squareImage.urlKey != null) setUploadKey(squareImage.urlKey);
    }

    @SuppressWarnings("unused")
    public void setClickAble (boolean able) {
        mClickUpload = able;
    }

    @Override
    public void onClick(View v) {
        if (mClickUpload) {

        } else {

        }
    }

    public View getSquareImage() {
        return null;
    }

    public void setRoundAsCircle(boolean flag) {
        if (flag == false) return;
        RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f);
        roundingParams.setRoundAsCircle(flag);
        getHierarchy().setRoundingParams(roundingParams);

    }
}
