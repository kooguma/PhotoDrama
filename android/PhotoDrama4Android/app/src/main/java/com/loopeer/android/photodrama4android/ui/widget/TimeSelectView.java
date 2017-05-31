package com.loopeer.android.photodrama4android.ui.widget;


import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.laputapp.utilities.DeviceScreenUtils;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.utils.ShapeUtils;

import static android.view.MotionEvent.INVALID_POINTER_ID;

public class TimeSelectView extends View {

    public static final int CIRCLE_RADIUS = 11;
    public static final int LINE_HEIGHT = 2;
    private static final int TEXT_MARGIN = 12;
    private static final int RECT_HEIGHT = 22;
    private static final int RECT_RADIUS = 2;
    private static final int TRIANGLE_HEIGHT = 5;
    private static final int TEXT_SIZE = 15;
    private static final int TEXT_START_END_SIZE = 16;
    private static final int TEXT_PADDING_HORIZONTAL = 11;
    private static final int HORIZONTAL_TEXT_MARGIN = 12;
    private static final String TEXT_START = "1s";
    private static final String TEXT_END = "12s";

    public float mLastTouchY;
    public float mLastTouchX;
    private int mActivePointerId = INVALID_POINTER_ID;

    private int mCircleRadius;
    private int mLineHeight;
    private int mTextMargin;
    private int mRectHeight;
    private int mTriangleHeight;
    private int mLineTop;
    private int mRectRadius;

    private float mHorizontalTextMargin;
    private float mStartTextWidth;
    private float mEndTextWidth;
    private int mTextStartEndSize;
    private Paint mTextStartEndPaint;

    private int mTextPadding;
    private int mTextSize;

    private Paint mPaint;
    private Paint mLinePaint;
    private Paint mTextPaint;

    private int mProgressColor;
    private int mTextBgColor;
    private int mTextColor;
    private int mThumbColor;
    private int mProgressBgColor;

    private int mMaxValue = 12000;
    private int mMinValue = 1000;
    private int mProgress = mMinValue;

    private boolean mIsOnTouch;
    private TimeUpdateListener mTimeUpdateListener;

    public TimeSelectView(Context context) {
        this(context, null);
    }

    public TimeSelectView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeSelectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mProgressColor = ContextCompat.getColor(getContext(), R.color.colorAccent);
        mProgressBgColor = ContextCompat.getColor(getContext(), android.R.color.white);
        mThumbColor = ContextCompat.getColor(getContext(), android.R.color.white);
        mTextBgColor = ContextCompat.getColor(getContext(), R.color.colorAccent);
        mTextColor = ContextCompat.getColor(getContext(), android.R.color.white);

        mCircleRadius = DeviceScreenUtils.dp2px(CIRCLE_RADIUS, getContext());
        mLineHeight = DeviceScreenUtils.dp2px(LINE_HEIGHT, getContext());
        mTextMargin = DeviceScreenUtils.dp2px(TEXT_MARGIN, getContext());
        mRectHeight = DeviceScreenUtils.dp2px(RECT_HEIGHT, getContext());
        mHorizontalTextMargin = DeviceScreenUtils.dp2px(HORIZONTAL_TEXT_MARGIN, getContext());
        mTriangleHeight = DeviceScreenUtils.dp2px(TRIANGLE_HEIGHT, getContext());
        mTextSize = DeviceScreenUtils.sp2px(TEXT_SIZE, (Activity) getContext());
        mTextStartEndSize = DeviceScreenUtils.sp2px(TEXT_START_END_SIZE, (Activity) getContext());
        mTextPadding = DeviceScreenUtils.dp2px(TEXT_PADDING_HORIZONTAL, getContext());
        mRectRadius = DeviceScreenUtils.dp2px(RECT_RADIUS, getContext());

        mLineTop = mRectHeight + mTriangleHeight + mTextMargin + mCircleRadius - mLineHeight / 2;

        mLinePaint = new Paint();
        mLinePaint.setColor(mProgressBgColor);
        mLinePaint.setAntiAlias(true);

        mPaint = new Paint();
        mPaint.setColor(mThumbColor);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setAntiAlias(true);

        mTextPaint = new Paint();
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setAntiAlias(true);

        mTextStartEndPaint = new Paint();
        mTextStartEndPaint.setStyle(Paint.Style.FILL);
        mTextStartEndPaint.setColor(mTextColor);
        mTextStartEndPaint.setTextSize(mTextStartEndSize);
        mTextStartEndPaint.setAntiAlias(true);

        mStartTextWidth = mTextStartEndPaint.measureText(TEXT_START);
        mEndTextWidth = mTextStartEndPaint.measureText(TEXT_END);
    }

    private float getStartTextOffset() {
        return mHorizontalTextMargin + mStartTextWidth;
    }

    private float getEndTextOffset() {
        return mHorizontalTextMargin + mEndTextWidth;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = mCircleRadius * 2 + mTextMargin + mRectHeight + mTriangleHeight;
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                final float x = ev.getX(pointerIndex);
                final float y = ev.getY(pointerIndex);
                mLastTouchX = x;
                mLastTouchY = y;
                mActivePointerId = ev.getPointerId(0);
                checkTouchOnIndicator(mLastTouchX, mLastTouchY);
                onStartTouch();
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex =
                        ev.findPointerIndex(mActivePointerId);

                final float x = ev.getX(pointerIndex);
                final float y = ev.getY(pointerIndex);
                final float dx = x - mLastTouchX;
                final float dy = y - mLastTouchY;
                scrollAndMoveIndicator(dx, dy);
                mLastTouchX = x;
                mLastTouchY = y;
                break;
            }

            case MotionEvent.ACTION_UP: {
                mActivePointerId = INVALID_POINTER_ID;
                onStopTouch();
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                onStopTouch();
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                final int pointerId = ev.getPointerId(pointerIndex);

                if (pointerId == mActivePointerId) {
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = ev.getX(newPointerIndex);
                    mLastTouchY = ev.getY(newPointerIndex);
                    mActivePointerId = ev.getPointerId(newPointerIndex);
                }
                onStopTouch();
                break;
            }
        }

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mLinePaint.setColor(mProgressColor);
        canvas.drawRect(getStartTextOffset() + mCircleRadius, mLineTop, getProgressViewWidth(), mLineTop + mLineHeight, mLinePaint);

        mLinePaint.setColor(mProgressBgColor);
        canvas.drawRect(getProgressViewWidth(), mLineTop
                , getWidth() - getEndTextOffset() - mCircleRadius, mLineTop + mLineHeight, mLinePaint);

        mPaint.setColor(mThumbColor);
        canvas.drawCircle(getProgressViewWidth(), mLineTop + mLineHeight / 2, mCircleRadius, mPaint);

        String content = String.format("%.1f", 1f * mProgress / 1000) + "s";
        float drawTextWidth = mTextPaint.measureText(content);
        mPaint.setColor(mTextBgColor);
        float clampX = clampProgressWidth(getProgressViewWidth(), drawTextWidth);
        canvas.drawPath(ShapeUtils.RoundedRect(clampX - drawTextWidth / 2 - mTextPadding
                , 0
                , clampX + drawTextWidth / 2 + mTextPadding, mRectHeight, mRectRadius, mRectRadius), mPaint);
        Path path = new Path();
        path.moveTo(getProgressViewWidth(), mRectHeight + mTriangleHeight);
        path.lineTo(getProgressViewWidth() - 1f * mTriangleHeight, mRectHeight);
        path.lineTo(getProgressViewWidth() + 1f * mTriangleHeight, mRectHeight);
        path.lineTo(getProgressViewWidth(), mRectHeight + mTriangleHeight);
        path.close();
        canvas.drawPath(path, mPaint);

        Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
        int baseline = (mRectHeight - fontMetrics.bottom - fontMetrics.top) / 2;
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(content, clampX, baseline, mTextPaint);


        Paint.FontMetricsInt fontMetricsStartText = mTextStartEndPaint.getFontMetricsInt();
        int baselineStartEnd = mLineTop + mLineHeight / 2
                - (fontMetricsStartText.bottom + fontMetricsStartText.top) / 2;
        canvas.drawText(TEXT_START, mHorizontalTextMargin
                , baselineStartEnd, mTextStartEndPaint);
        canvas.drawText(TEXT_END, getWidth() - mHorizontalTextMargin - mEndTextWidth
                , baselineStartEnd, mTextStartEndPaint);
    }

    private float clampProgressWidth(float x, float width) {
        return x;
    }

    private float getProgressViewWidth() {
        return 1f * (getSeekBarWidth() - 2 * mCircleRadius) * (mProgress - mMinValue) / (mMaxValue - mMinValue) + mCircleRadius + getStartTextOffset();
    }

    private float getSeekBarWidth() {
        return getWidth() - getStartTextOffset() - getEndTextOffset();
    }

    public void updateProgress(int progress) {
        mProgress = progress;
        invalidate();
    }

    public int getProgress() {
        return mProgress;
    }

    private void scrollAndMoveIndicator(float dx, float dy) {
        if (mIsOnTouch) {
            mProgress += (dx / (getSeekBarWidth() - 2 * mCircleRadius) * (mMaxValue - mMinValue));
            clampTrans();
            if (mTimeUpdateListener != null) mTimeUpdateListener.onTimeUpdate(mProgress);
            invalidate();
        }
    }

    private void clampTrans() {
        if (mProgress > mMaxValue) mProgress = mMaxValue;
        if (mProgress < mMinValue) mProgress = mMinValue;
    }

    private void onStartTouch() {

    }

    private void onStopTouch() {
        mIsOnTouch = false;
    }

    private void checkTouchOnIndicator(float lastTouchX, float lastTouchY) {
        mIsOnTouch = isOnTouch(lastTouchX, lastTouchY);
    }

    public boolean isOnTouch(float x, float y) {
        float height = 1f * mCircleRadius * 3 / 2;
        float topDotY = mLineTop + mLineHeight / 2;
        float topDotX = getProgressViewWidth();
        if (y > topDotY - height
                && y < topDotY + height
                && x > topDotX - height
                && x < topDotX + height)
            return true;
        return false;
    }

    public void setTimeUpdateListener(TimeUpdateListener timeUpdateListener) {
        mTimeUpdateListener = timeUpdateListener;
    }

    public interface TimeUpdateListener{
        void onTimeUpdate(int time);
    }

}
