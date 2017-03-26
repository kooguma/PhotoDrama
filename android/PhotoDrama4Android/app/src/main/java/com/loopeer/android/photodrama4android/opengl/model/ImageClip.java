package com.loopeer.android.photodrama4android.opengl.model;


public class ImageClip extends Clip{
    public String path;
    public int startWithPreTransitionTime = -1;
    public int showTime = 2000;
    public int endWithNextTransitionTime = -1;
    public ScaleTranslateRatio startScaleTransRatio;
    public ScaleTranslateRatio endScaleTransRatio;

    public ImageClip(String path) {
        this.path = path;
    }

    public ImageClip(String path, int startTime) {
        this.path = path;
        this.startTime = startTime;
    }

    public int getEndTime() {
        return showTime + startTime - 1;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) return false;
        return path.equals(((ImageClip)obj).path);
    }

    public float getScaleFactor(long usedTime) {
        return getRatio(usedTime) * (endScaleTransRatio.scaleFactor - startScaleTransRatio.scaleFactor)
                + startScaleTransRatio.scaleFactor;
    }

    public float getTransX(long usedTime) {
        return getRatio(usedTime) * (endScaleTransRatio.x - startScaleTransRatio.x)
                + startScaleTransRatio.x;
    }

    public float getTransY(long usedTime) {
        return getRatio(usedTime) * (endScaleTransRatio.y - startScaleTransRatio.y)
                + startScaleTransRatio.y;
    }

    private float getRatio(long usedTime) {
        return 1f * (usedTime - startTime) / (showTime - 1);
    }
}
