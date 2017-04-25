package com.loopeer.bottomimagepicker;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class GridLayoutItemDecoration extends RecyclerView.ItemDecoration {

    private int mSpacing;

    public GridLayoutItemDecoration(int spacing) {
        mSpacing = spacing;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = mSpacing;
        outRect.top = mSpacing;
        outRect.right = mSpacing;
        outRect.bottom = mSpacing;
    }

}
