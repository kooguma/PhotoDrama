package com.loopeer.android.photodrama4android.model;


public class SubtitleEditRectInfo {
    public float left, top, right, bottom;
    public float deleteBtnWidth, deleteBtnHeight;

    public void updateRect(float left, float top, float right, float bottom) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }

    public void updateDeleteSize(float width, float height) {
        this.deleteBtnWidth = width;
        this.deleteBtnHeight = height;
    }

}
