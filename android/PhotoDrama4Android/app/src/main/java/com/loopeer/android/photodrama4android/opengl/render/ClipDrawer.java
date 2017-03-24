package com.loopeer.android.photodrama4android.opengl.render;


import android.view.View;

public class ClipDrawer {

    protected int mViewWidth;
    protected int mViewHeight;

    public ClipDrawer(View view) {
        mViewWidth = view.getWidth();
        mViewHeight = view.getHeight();
    }
}
