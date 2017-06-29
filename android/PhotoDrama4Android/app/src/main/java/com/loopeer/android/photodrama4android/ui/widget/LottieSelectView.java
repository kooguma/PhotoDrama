package com.loopeer.android.photodrama4android.ui.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import com.airbnb.lottie.LottieAnimationView;

public class LottieSelectView extends LottieAnimationView {

    private float speed;

    public LottieSelectView(Context context) {
        super(context);
        init();
    }

    public LottieSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LottieSelectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                if (speed == 1 && fraction >= 0.5f) {
                    pauseAnimation();
                }
            }
        });
    }

    @Override public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (!selected) { //0.5 暂停
            setProgress(0f);
            setSpeed(speed = 1f);
            playAnimation();
        } else {
            setProgress(0.5f);
            setSpeed(speed = -2.5f);
            playAnimation();
        }
    }
}
