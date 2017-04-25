package com.loopeer.android.photodrama4android.media.model;


import com.loopeer.android.photodrama4android.R;

public class EndLogoClip extends Clip {

    public static EndLogoClip getInstance(){
        return new EndLogoClip();
    }

    public int logoRes = R.drawable.ic_drama_end_logo;
    public int textRes = R.drawable.ic_drama_end_text;

    public float animationFactor = 0.7f;

    public float getDegree(int useTime) {
        float progress = getProgress(useTime);
        return 360 * progress;
    }

    private float getProgress(int useTime) {
        float progress = 1f * (useTime - startTime) / (int)(showTime * animationFactor);
        if (progress > 1) progress = 1;
        return progress;
    }

    public int getAlpha(int useTime) {
        float progress = getProgress(useTime);
        return (int) (255 * progress);
    }
}
