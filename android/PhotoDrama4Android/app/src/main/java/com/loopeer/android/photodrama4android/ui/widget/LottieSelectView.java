package com.loopeer.android.photodrama4android.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.airbnb.lottie.LottieAnimationView;

public class LottieSelectView extends LottieAnimationView {

    public LottieSelectView(Context context) {
        super(context);
    }

    public LottieSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LottieSelectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected) {
            setProgress(0.2f);
            setSpeed(2f);
            playAnimation();

        } else {
            setProgress(0.7f);
            setSpeed(-2f);
            playAnimation();
        }
    }
}
