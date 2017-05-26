package com.loopeer.android.photodrama4android.ui.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.ViewAnimator;

import com.laputapp.utilities.DeviceScreenUtils;

public class TextureViewWrapper extends ViewAnimator {

    boolean isLandscape = false;

    public TextureViewWrapper(Context context) {
        super(context);
    }

    public TextureViewWrapper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!isLandscape) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
        int childWidthSize = getMeasuredWidth();
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(DeviceScreenUtils.getScreenHeight(getContext()), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!isLandscape) {
            return super.dispatchTouchEvent(ev);
        }
        super.dispatchTouchEvent(ev);
        final ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true);
        }
        return true;
    }

    public void setLandscape(boolean landscape) {
        isLandscape = landscape;
    }
}
