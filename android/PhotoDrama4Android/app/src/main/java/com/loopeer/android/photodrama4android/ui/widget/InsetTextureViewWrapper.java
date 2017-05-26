package com.loopeer.android.photodrama4android.ui.widget;


import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;

import com.loopeer.compatinset.InsetHelper;

public class InsetTextureViewWrapper extends TextureViewWrapper {

    public InsetTextureViewWrapper(Context context) {
        this(context, null);
    }

    public InsetTextureViewWrapper(Context context, AttributeSet attrs) {
        super(context, attrs);
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
