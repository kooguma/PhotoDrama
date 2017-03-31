package com.loopeer.android.photodrama4android.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.Observable;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.media.OnSeekProgressChangeListener;
import com.loopeer.android.photodrama4android.media.SeekWrapper;
import com.loopeer.android.photodrama4android.media.model.Clip;

import java.util.ArrayList;
import java.util.List;

import static android.view.MotionEvent.INVALID_POINTER_ID;

public class ScrollSelectView extends ViewGroup {

    public float mPosY;
    public float mPosX;
    public float mLastTouchY;
    public float mLastTouchX;
    private int mActivePointerId = INVALID_POINTER_ID;

    private Paint mPaint;
    private Paint mStrokePaint;
    private IndicatorShape mIndicatorShapeStart;
    private IndicatorShape mIndicatorShapeEnd;
    private IndicatorShape mSelectedIndicatorShape;
    private int mMiddleLineColor;
    private int mStrokeLineColor;
    private int mTextRectColor;
    private int mTextRectSelectedColor;
    private int mMiddlePos;

    private float mImageShowHeight;
    private float mIndicatorTriangleHeight;
    private float mIndicatorRectangleHeight;
    private float mIndicatorBottomMargin;
    private float mIndicatorWidth;

    private boolean isManual = true;

    private float mMiddleLineWidth = 4f;
    private int mTotalContentWidth;
    private Adapter mAdapter;
    private final ChildViewDataObserver mObserver = new ChildViewDataObserver();
    private int mMaxValue;
    private OnSeekProgressChangeListener mOnSeekProgressChangeListener;
    private SeekWrapper.SeekImpl mSeek;
    private ClipIndicatorPosChangeListener mClipIndicatorPosChangeListener;
    private ClipSelectedListener mClipSelectedListener;

    private List<Clip> mClips;
    private Clip mSelectedClip;
    private Clip mPreSelectedClip;
    private int mMinClipShowTime = 500;

    public ScrollSelectView(Context context) {
        this(context, null);
    }

    public ScrollSelectView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollSelectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ScrollSelectView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        getAttrs(context, attrs, defStyleAttr);

        mMiddleLineColor = ContextCompat.getColor(context, R.color.colorAccent);
        mTextRectColor = ContextCompat.getColor(context, R.color.subtitle_text_rect_color);
        mTextRectSelectedColor = ContextCompat.getColor(context, R.color.subtitle_text_rect_color_selected);
        mStrokeLineColor = ContextCompat.getColor(context, android.R.color.white);
        setWillNotDraw(false);
        mClips = new ArrayList<>();
        mPaint = new Paint();
        mPaint.setColor(mMiddleLineColor);

        mStrokePaint = new Paint();
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setColor(mStrokeLineColor);
        mStrokePaint.setStrokeWidth(mMiddleLineWidth);
        mStrokePaint.setStrokeCap(Paint.Cap.ROUND);

        mIndicatorShapeEnd = new IndicatorShape(mIndicatorWidth);
        mIndicatorShapeEnd.setRectangleHeight(mIndicatorRectangleHeight);
        mIndicatorShapeEnd.setTriangleHeight(mIndicatorTriangleHeight);
        mIndicatorShapeStart = new IndicatorShape(mIndicatorWidth);
        mIndicatorShapeStart.setRectangleHeight(mIndicatorRectangleHeight);
        mIndicatorShapeStart.setTriangleHeight(mIndicatorTriangleHeight);
    }

    private void getAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        if (attrs == null) return;
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ScrollSelectView, defStyleAttr, 0);
        if (a == null) return;

        mImageShowHeight = a.getDimension(R.styleable.ScrollSelectView_imageShowHeight, 120f);
        mIndicatorRectangleHeight = a.getDimension(R.styleable.ScrollSelectView_indicatorRectangleHeight, 30f);
        mIndicatorTriangleHeight = a.getDimension(R.styleable.ScrollSelectView_indicatorTriangleHeight, 20f);
        mIndicatorBottomMargin = a.getDimension(R.styleable.ScrollSelectView_indicatorBottomMargin, 20f);
        mIndicatorWidth = a.getDimension(R.styleable.ScrollSelectView_indicatorWidth, 20f);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = (int) (mImageShowHeight + mIndicatorRectangleHeight + mIndicatorTriangleHeight + mIndicatorBottomMargin);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChild(widthMeasureSpec);
    }

    private void measureChild(int widthMeasureSpec) {
        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            int heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) mImageShowHeight, MeasureSpec.EXACTLY);
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
        }
        mMiddlePos = getWidth() / 2;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        layoutChild();
    }

    private void layoutChild() {
        int childTop = getPaddingTop();
        int childLeft = mMiddlePos;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            final int childWidth = child.getMeasuredWidth();
            int childHeight = (int) mImageShowHeight;
            if (i != 0) {
                child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
            } else {
                child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
            }
            childLeft += childWidth;
        }
        mTotalContentWidth = childLeft - mMiddlePos;
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

    private void scrollAndMoveIndicator(float dx, float dy) {
        if (mSelectedIndicatorShape == null || mSelectedClip == null) {
            mPosX += dx;
            mPosY += dy;
            scrollContent();
            updateSelectedClip();
            onProgressChange();
        } else {
            if (mSelectedIndicatorShape == mIndicatorShapeStart) {
                changeTimeByStartIndicator(getChangeTime(dx));
            } else {
                changeTimeByEndIndicator(getChangeTime(dx));
            }
            invalidate();
        }
    }

    private void updateSelectedClip() {
        mPreSelectedClip = mSelectedClip;
        mSelectedClip = null;
        for (Clip clip : mClips) {
            if (isManual && getProgress() >= clip.startTime && getProgress() <= clip.getEndTime()) {
                mSelectedClip = clip;
                break;
            }
        }
        if (mPreSelectedClip != mSelectedClip && mClipSelectedListener != null) {
            mClipSelectedListener.onClipSelected(mSelectedClip);
        }
    }

    public void changeTimeByStartIndicator(int offset) {
        if (mClipIndicatorPosChangeListener != null
                && mClipIndicatorPosChangeListener.changeTimeByStartIndicator(mSelectedClip
                , offset, mMinClipShowTime, mMaxValue))
            return;
        int endValue = mSelectedClip.startTime + mSelectedClip.showTime;
        if (offset < 0){
            mSelectedClip.startTime += offset;
            mSelectedClip.showTime = endValue - mSelectedClip.startTime;
        }
        if (offset > 0) {
            mSelectedClip.startTime += offset;
            mSelectedClip.showTime = endValue - mSelectedClip.startTime;
        }
        if (mSelectedClip.showTime <= mMinClipShowTime) {
            mSelectedClip.showTime = mMinClipShowTime;
            mSelectedClip.startTime = endValue - mSelectedClip.showTime;
        }
        if (mSelectedClip.startTime <= 0) {
            mSelectedClip.startTime = 0;
            mSelectedClip.showTime = endValue;
        }
        mOnSeekProgressChangeListener.onProgressChanged(mSeek, mSelectedClip.startTime, true);
    }

    private void scrollToTime(int time) {
        mOnSeekProgressChangeListener.onProgressChanged(mSeek, time, true);
        setProgress(time);
    }

    public void changeTimeByEndIndicator(int offset) {
        if (mClipIndicatorPosChangeListener != null
                && mClipIndicatorPosChangeListener.changeTimeByEndIndicator(mSelectedClip
                , offset, mMinClipShowTime, mMaxValue))
            return;
        mSelectedClip.showTime += offset;
        if (mSelectedClip.getEndTime() >= mMaxValue + 1)
            mSelectedClip.showTime = mMaxValue + 1 - mSelectedClip.startTime;
        if (mSelectedClip.showTime <= mMinClipShowTime)
            mSelectedClip.showTime = mMinClipShowTime;

        mOnSeekProgressChangeListener.onProgressChanged(mSeek, mSelectedClip.getEndTime(), true);
    }

    private void checkTouchOnIndicator(float lastTouchX, float lastTouchY) {
        boolean touchStart = mIndicatorShapeStart.isOnTouch(lastTouchX, lastTouchY);
        boolean touchEnd = mIndicatorShapeEnd.isOnTouch(lastTouchX, lastTouchY);
        if (touchStart && touchEnd) {
            mSelectedIndicatorShape = (mIndicatorShapeEnd.topDotX + mIndicatorShapeEnd.topDotX) / 2 > lastTouchX
                    ? mIndicatorShapeStart : mIndicatorShapeEnd;
        } else if (touchStart) {
            mSelectedIndicatorShape = mIndicatorShapeStart;
        } else if (touchEnd) {
            mSelectedIndicatorShape = mIndicatorShapeEnd;
        } else {
            mSelectedIndicatorShape = null;
        }

    }

    private void onStartTouch() {
        mOnSeekProgressChangeListener.onStartTrackingTouch(mSeek);
        isManual = true;
    }

    private void onProgressChange() {
        mOnSeekProgressChangeListener.onProgressChanged(mSeek, getProgress(), true);
        notifyProgressChange();
    }

    private void notifyProgressChange() {
        invalidate();
    }

    private void onStopTouch() {
        mOnSeekProgressChangeListener.onStopTrackingTouch(mSeek);
        if (mSelectedClip != null && mSelectedIndicatorShape != null) {
            if (mSelectedIndicatorShape == mIndicatorShapeStart) {
                scrollToTime(mSelectedClip.startTime);
            } else {
                scrollToTime(mSelectedClip.getEndTime());
            }
            mSelectedIndicatorShape = null;
        }
    }

    private void scrollContent() {
        clampTrans();
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setTranslationX(mPosX);
        }
    }

    private float clampTrans() {
        if (mPosX >= 0) {
            mPosX = 0;
        }
        if (mPosX <= -getTotalLength())
            mPosX = -getTotalLength();
        return mPosX;
    }

    private float getTotalLength() {
        return mTotalContentWidth;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        mPaint.setColor(ContextCompat.getColor(getContext(), android.R.color.black));
        canvas.drawRect(0, 0, getWidth(), mImageShowHeight, mPaint);
        super.dispatchDraw(canvas);
        drawTextRect(canvas);
        mPaint.setColor(mMiddleLineColor);
        canvas.drawRect(mMiddlePos - mMiddleLineWidth / 2, 0, mMiddlePos + mMiddleLineWidth / 2, mImageShowHeight, mPaint);
    }

    private void drawTextRect(Canvas canvas) {
        for (Clip clip : mClips) {
            float left = mMiddlePos + mPosX + getTotalLength() * clip.startTime / mMaxValue;
            float right = mMiddlePos + mPosX + getTotalLength() * clip.getEndTime() / mMaxValue;
            if (clip == mSelectedClip) {
                mPaint.setColor(mTextRectSelectedColor);
                canvas.drawRect(left, 0, right, mImageShowHeight, mPaint);
                canvas.drawRect(left, 0, right, mImageShowHeight, mStrokePaint);
                mIndicatorShapeStart.updateTopDot(left, mImageShowHeight);
                mIndicatorShapeStart.draw(canvas, mPaint);
                mIndicatorShapeEnd.updateTopDot(right, mImageShowHeight);
                mIndicatorShapeEnd.draw(canvas, mPaint);
            } else {
                mPaint.setColor(mTextRectColor);
                canvas.drawRect(left, 0, right, mImageShowHeight, mPaint);
            }
        }
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    public void setMax(int max) {
        mMaxValue = max;
    }

    public void setMinClipShowTime(int minClipShowTime) {
        mMinClipShowTime = minClipShowTime;
    }

    public void setProgress(int progress) {
        mPosX = -1f * progress / mMaxValue * getTotalLength();
        isManual = false;
        scrollContent();
        notifyProgressChange();
    }

    public int getProgress() {
        return (int) (1f * mMaxValue * -mPosX / getTotalLength());
    }

    public int getChangeTime(float transX) {
        return (int) (1f * mMaxValue * transX / getTotalLength());
    }

    public static class LayoutParams extends MarginLayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    public interface IAdapter<T> {
        View onCreateView(LayoutInflater inflater, ViewGroup parent);

        void onBindView(View view, T t);

        int getItemCount();

        T getItem(int position);
    }

    public abstract static class Adapter<TH> implements IAdapter<TH> {
        private final AdapterDataObservable mObservable = new AdapterDataObservable();

        public Adapter() {
            mDatas = new ArrayList<>();
        }

        protected List<TH> mDatas;

        public void registerAdapterDataObserver(AdapterDataObserver observer) {
            mObservable.registerObserver(observer);
        }

        public void updateDatas(List<TH> datas) {
            setDatas(datas);
            notifyDataChange();
        }

        public void setDatas(List<TH> datas) {
            mDatas.clear();
            mDatas.addAll(datas);
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        public void notifyDataChange() {
            mObservable.notifyChanged();
        }

        @Override
        public TH getItem(int position) {
            return mDatas.get(position);
        }
    }

    static class AdapterDataObservable extends Observable<AdapterDataObserver> {
        public boolean hasObservers() {
            return !mObservers.isEmpty();
        }

        public void notifyChanged() {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onChanged();
            }
        }
    }

    public static abstract class AdapterDataObserver {
        public void onChanged() {

        }
    }

    private class ChildViewDataObserver extends AdapterDataObserver {
        ChildViewDataObserver() {
        }

        @Override
        public void onChanged() {
            reBindView();
            requestLayout();
        }
    }

    public void setAdapter(Adapter adapter) {
        adapter.registerAdapterDataObserver(mObserver);
        mAdapter = adapter;
        reBindView();
        requestLayout();
    }

    public void updateClips(List<? extends Clip> clips) {
        mClips.clear();
        mClips.addAll(clips);
        notifySelectedClip();
        invalidate();
    }

    private void notifySelectedClip() {
        mSelectedClip = null;
        for (Clip clip : mClips) {
            if (getProgress() >= clip.startTime && getProgress() <= clip.getEndTime()
                    && mClipSelectedListener != null) {
                mSelectedClip = clip;
                break;
            }
        }
        if (mPreSelectedClip != mSelectedClip) {
            mClipSelectedListener.onClipSelected(mSelectedClip);
        }
        mPreSelectedClip = mSelectedClip;
    }

    private void reBindView() {
        removeAllViews();
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            View view = mAdapter.onCreateView(LayoutInflater.from(getContext()), this);
            mAdapter.onBindView(view, mAdapter.getItem(i));
            addView(view);
        }
    }

    public void setOnSeekProgressChangeListener(SeekWrapper.SeekImpl seek, OnSeekProgressChangeListener listener) {
        mSeek = seek;
        mOnSeekProgressChangeListener = listener;
    }

    public class IndicatorShape {
        public float topDotX;
        public float topDotY;
        public float width;
        public float triangleHeight = 10f;
        public float rectangleHeight = 20f;

        public IndicatorShape(float width) {
            this.width = width;
        }

        public void setTriangleHeight(float triangleHeight) {
            this.triangleHeight = triangleHeight;
        }

        public void setRectangleHeight(float rectangleHeight) {
            this.rectangleHeight = rectangleHeight;
        }

        public void updateTopDot(float x, float y) {
            topDotX = x;
            topDotY = y;
        }

        public void draw(Canvas canvas, Paint paint) {
            drawTriangle(canvas, paint);
            drawRectangle(canvas, paint);
        }

        private void drawTriangle(Canvas canvas, Paint paint) {
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setAntiAlias(true);
            Path path = new Path();
            path.moveTo(topDotX, topDotY);
            path.lineTo(topDotX - width / 2, topDotY + triangleHeight);
            path.lineTo(topDotX + width / 2, topDotY + triangleHeight);
            path.lineTo(topDotX, topDotY);
            path.close();
            canvas.drawPath(path, paint);
        }

        private void drawRectangle(Canvas canvas, Paint paint) {
            canvas.drawRect(topDotX - width / 2
                    , topDotY + triangleHeight
                    , topDotX + width / 2
                    , topDotY + triangleHeight + rectangleHeight
                    , paint);
        }

        public boolean isOnTouch(float x, float y) {
            float height = mIndicatorRectangleHeight + mIndicatorTriangleHeight;
            if (y > topDotY - height
                    && y < topDotY + height
                    && x > topDotX - height
                    && x < topDotX + height)
                return true;
            return false;
        }
    }

    public void setClipIndicatorPosChangeListener(ClipIndicatorPosChangeListener clipIndicatorPosChangeListener) {
        mClipIndicatorPosChangeListener = clipIndicatorPosChangeListener;
    }

    public void setClipSelectedListener(ClipSelectedListener clipSelectedListener) {
        mClipSelectedListener = clipSelectedListener;
    }

    public interface ClipIndicatorPosChangeListener{
        boolean changeTimeByStartIndicator(Clip clip, int offset, int minValue, int maxValue);
        boolean changeTimeByEndIndicator(Clip clip, int offset, int minValue, int maxValue);
    }

    public interface ClipSelectedListener{
        void onClipSelected(Clip clip);
    }
}
