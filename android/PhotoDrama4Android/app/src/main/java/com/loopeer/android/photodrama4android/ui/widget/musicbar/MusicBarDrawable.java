package com.loopeer.android.photodrama4android.ui.widget.musicbar;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import com.laputapp.utilities.DeviceScreenUtils;

public class MusicBarDrawable extends Drawable {

    private static final String TAG = "MusicBarDrawable";

    private int mWidth;
    private int mHeight;

    private final Rect mBounds = new Rect();

    private Params mParams;

    private ValueAnimator[] mAnimators = new ValueAnimator[4];
    private float mRatios[] = new float[4];
    private static final float[] sRatios = new float[] { 0.8f, 0.7f, 1.0f, 0.6f };

    public class AnimatorUpdateListenerWrapper implements ValueAnimator.AnimatorUpdateListener {

        public float ratio;

        public AnimatorUpdateListenerWrapper(float ratio) {
            this.ratio = ratio;
        }

        @Override public void onAnimationUpdate(ValueAnimator animation) {
            ratio = animation.getAnimatedFraction();
            //Log.e(TAG,"AnimatorUpdateListenerWrapper ratio = " + ratio);
            invalidateSelf();
        }
    }

    private MusicBarDrawable(Params params) {
        mParams = params;
        setupAnimators();
    }

    private void setupAnimators() {
        // TODO: 2017/5/17  one animator for all
        for (int i = 0; i < sRatios.length; i++) {
            mAnimators[i] = ValueAnimator.ofFloat(sRatios[i] / 2, sRatios[i]);
            mAnimators[i].setInterpolator(new LinearInterpolator());
            mAnimators[i].addUpdateListener(new AnimatorUpdateListenerWrapper(mRatios[i]));
            mAnimators[i].setDuration(500);
            mAnimators[i].setRepeatCount(ValueAnimator.INFINITE);
            mAnimators[i].setRepeatMode(ValueAnimator.RESTART);
            mAnimators[i].start();
        }
    }

    @Override protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        mBounds.left = bounds.left;
        mBounds.right = bounds.right;
        mBounds.top = bounds.top;
        mBounds.bottom = bounds.bottom;
    }

    @Override public void draw(@NonNull Canvas canvas) {
        final int left = mBounds.left;
        final int width = mBounds.width();
        final int rectWidth = (int) (width /
            ((mParams.mRectCount + 1) * mParams.mRatio + mParams.mRectCount));
        final int gapWidth = (int) (rectWidth * mParams.mRatio);

        final RectF rect = new RectF();
        rect.top = mBounds.top;
        rect.bottom = mBounds.bottom;

        for (int i = 0; i < mParams.mRectCount; i++) {
            rect.left = left + i * (gapWidth + rectWidth);
            rect.right = rect.left + rectWidth;
            rect.top = mRatios[i] * rect.bottom;
            Log.e(TAG, "ratio = " + mRatios[i] + "rect top = " + rect.top);
            canvas.drawRect(rect, mParams.mPaint);
        }

    }

    @Override public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        if (mParams.mPaint != null) {
            mParams.mPaint.setAlpha(alpha);
        }
    }

    @Override public void setColorFilter(@Nullable ColorFilter colorFilter) {
        if (mParams.mPaint != null) {
            mParams.mPaint.setColorFilter(colorFilter);
        }
    }

    @Override public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public static class Builder {

        int mRectCount;
        float mRatio; //
        Paint mPaint;
        int mRectColor;

        public Builder() {
            mPaint = new Paint();
        }

        public Builder setRectCount(int rectCount) {
            mRectCount = rectCount;
            return this;
        }

        public Builder setRectColor(int color) {
            mRectColor = color;
            return this;
        }

        public Builder setRatio(float ratio) {
            mRatio = ratio;
            return this;
        }

        private void apply() {
            mPaint.setAntiAlias(true);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setStrokeWidth(1);
            mPaint.setColor(mRectColor);
        }

        public MusicBarDrawable build() {
            apply();
            return new MusicBarDrawable(new Params(mRectCount, mRatio, mPaint, mRectColor));
        }
    }

    static class Params {

        private int mRectCount;
        private float mRatio; // (gapWidth / rectWidth) = ratio
        private Paint mPaint;
        private int mRectColor;

        public Params(int mRectCount, float mRatio, Paint mPaint, int mRectColor) {
            this.mRectCount = mRectCount;
            this.mRatio = mRatio;
            this.mPaint = mPaint;
            this.mRectColor = mRectColor;
        }

        @Override public String toString() {
            return "Params{" +
                "mRectCount=" + mRectCount +
                ", mRatio=" + mRatio +
                ", mPaint=" + mPaint +
                ", mRectColor=" + mRectColor +
                '}';
        }
    }
}
