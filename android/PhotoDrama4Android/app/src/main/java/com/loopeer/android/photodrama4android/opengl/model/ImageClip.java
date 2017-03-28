package com.loopeer.android.photodrama4android.opengl.model;


public class ImageClip extends Clip{
    public String path;
    public int startWithPreTransitionTime = 0;
    public int endWithNextTransitionTime = 0;
    public ScaleTranslateRatio startScaleTransRatio;
    public ScaleTranslateRatio endScaleTransRatio;

    public ImageClip(String path) {
        this.path = path;
    }

    public ImageClip(String path, int startTime) {
        this.path = path;
        this.startTime = startTime;
        this.startWithPreTransitionTime = startTime;
        this.endWithNextTransitionTime = getEndTime();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) return false;
        return startTime == ((ImageClip)obj).startTime;
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

    @Override
    public String toString() {
        return "ImageClip{" +
                super.toString() +
                "startWithPreTransitionTime=" + startWithPreTransitionTime +
                ", endWithNextTransitionTime=" + endWithNextTransitionTime +
                '}';
    }
}
