package com.loopeer.android.photodrama4android.ui.widget;


import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.loopeer.compatinset.InsetHelper;

public class InsetCoordinatorLayout extends CoordinatorLayout {

    public InsetCoordinatorLayout(Context context) {
        this(context, null);
    }

    public InsetCoordinatorLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InsetCoordinatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        InsetHelper.setupForInsets(this);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        InsetHelper.requestApplyInsets(this);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.KITKAT)
    protected boolean fitSystemWindows(Rect insets) {
        return super.fitSystemWindows(InsetHelper.clearInset(insets));
    }
}
