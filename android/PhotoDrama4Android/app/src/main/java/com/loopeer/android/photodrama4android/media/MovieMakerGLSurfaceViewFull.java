package com.loopeer.android.photodrama4android.media;


import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.loopeer.android.librarys.imagegroupview.utils.DisplayUtils;

public class MovieMakerGLSurfaceViewFull extends MovieMakerGLSurfaceView {
    public MovieMakerGLSurfaceViewFull(Context context) {
        super(context);
    }

    public MovieMakerGLSurfaceViewFull(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int screenWidth = DisplayUtils.getScreenWidth(getContext());
        int screenHeight = DisplayUtils.getScreenHeight(getContext());

        if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
            if ((float) screenWidth / screenHeight > mRatioX / mRatioY) {
                int childHeightSize = screenHeight;
                widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) (mRatioX * childHeightSize / mRatioY), MeasureSpec.EXACTLY);
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeightSize, MeasureSpec.EXACTLY);
            } else {
                int childWidthSize = screenWidth;
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
                heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (mRatioY * childWidthSize / mRatioX), MeasureSpec.EXACTLY);
            }

            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
