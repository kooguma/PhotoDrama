package com.loopeer.android.photodrama4android.ui.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.ViewAnimator;

public class TextureViewWrapper extends ViewAnimator {

    public TextureViewWrapper(Context context) {
        super(context);
    }

    public TextureViewWrapper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        final ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true);
        }
        return true;
    }

}
