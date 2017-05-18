package com.loopeer.android.photodrama4android.media.model;


import com.loopeer.android.photodrama4android.media.mediaio.XmlImageClip;
import com.loopeer.android.photodrama4android.media.utils.ZipUtils;

import static com.loopeer.android.photodrama4android.media.Constants.DEFAULT_IMAGE_CLIP_SHOW_TIME;

public class ImageClip extends Clip{
    public String path;
    public int startWithPreTransitionTime = 0;
    public int endWithNextTransitionTime = 0;
    public ScaleTranslateRatio startScaleTransRatio;
    public ScaleTranslateRatio endScaleTransRatio;

    public ImageClip() {
        showTime = DEFAULT_IMAGE_CLIP_SHOW_TIME;
    }

    public ImageClip(String path, int startTime) {
        this();
        this.path = path;
        this.startTime = startTime;
        this.startWithPreTransitionTime = startTime;
        this.endWithNextTransitionTime = getEndTime();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj.getClass() != this.getClass()) return false;
        return startTime == ((ImageClip)obj).startTime && super.equals(obj);
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

    public XmlImageClip toXml() {
        XmlImageClip xmlImageClip = new XmlImageClip();
        xmlImageClip.path = ZipUtils.clipFileName(path);
        xmlImageClip.startTime = startTime;
        xmlImageClip.duration = showTime;
        xmlImageClip.startTransition = startScaleTransRatio.toXml();
        xmlImageClip.endTransition = endScaleTransRatio.toXml();
        return xmlImageClip;
    }
}
