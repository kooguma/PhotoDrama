package com.loopeer.android.photodrama4android.ui.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.loopeer.android.librarys.imagegroupview.utils.ImageGroupDisplayHelper;

public class ScrollSelectInnderImageView extends SimpleDraweeView {

    private int mDefaultSize;
    private int mCurrentSize;

    public ScrollSelectInnderImageView(Context context) {
        super(context);
    }

    public ScrollSelectInnderImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollSelectInnderImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ScrollSelectInnderImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
        if (getHeight() == 0) {
            post(new Runnable() {
                @Override
                public void run() {
                    setLocalUrl(localUrl);
                }
            });
            return;
        }
        ImageGroupDisplayHelper.displayImageLocal(this, localUrl, getWidth(), getHeight());
    }

}
