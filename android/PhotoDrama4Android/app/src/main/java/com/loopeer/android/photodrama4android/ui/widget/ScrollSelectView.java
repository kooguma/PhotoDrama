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

import com.laputapp.utilities.DeviceScreenUtils;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.media.OnSeekProgressChangeListener;
import com.loopeer.android.photodrama4android.media.SeekWrapper;
import com.loopeer.android.photodrama4android.media.model.Clip;
import com.loopeer.android.photodrama4android.ui.activity.RecordMusicActivity;

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
    private ImageWrapperLineShape mImageWrapperLineShape;
    private ImageWrapperLineShape mSelectedImageWrapperLineShape;
    private IndicatorShape mSelectedIndicatorShape;
    private int mMiddleLineColor;
    private int mStrokeLineColor;
    private int mTextRectColor;
    private int mMiddlePos;

    private float mImageShowHeight;
    private float mImageMargin;
    private float mIndicatorWidth;
    private float mIndicatorInLineWidth;
    private float mIndicatorInLineHeight;

    private boolean isManual = false;
    private boolean isStop = false;
    private boolean isSelectedOnTouch = false;

    private float mMiddleLineWidth;
    private int mTotalContentWidth;
    private Adapter mAdapter;
    private final ChildViewDataObserver mObserver = new ChildViewDataObserver();
    private int mMaxValue;
    private List<OnSeekProgressChangeListener> mOnSeekProgressChangeListeners;
    private SeekWrapper.SeekImpl mSeek;
    private ClipIndicatorPosChangeListener mClipIndicatorPosChangeListener;
    private ClipSelectedListener mClipSelectedListener;
    private TouchStateListener mTouchStateListener;

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
        mOnSeekProgressChangeListeners = new ArrayList<>();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ScrollSelectView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        getAttrs(context, attrs, defStyleAttr);

        mMiddleLineColor = ContextCompat.getColor(context, android.R.color.white);
        mTextRectColor = ContextCompat.getColor(context, R.color.subtitle_text_rect_color);
        mStrokeLineColor = ContextCompat.getColor(context, android.R.color.white);
        setWillNotDraw(false);
        mClips = new ArrayList<>();
        mPaint = new Paint();
        mPaint.setColor(mMiddleLineColor);

        mStrokePaint = new Paint();
        mStrokePaint.setColor(mStrokeLineColor);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeWidth(mMiddleLineWidth);
        mStrokePaint.setStrokeCap(Paint.Cap.ROUND);

        mIndicatorShapeEnd = new IndicatorShape(mIndicatorWidth, mImageMargin, mImageShowHeight, mIndicatorInLineWidth, mIndicatorInLineHeight, false);
        mIndicatorShapeStart = new IndicatorShape(mIndicatorWidth, mImageMargin, mImageShowHeight, mIndicatorInLineWidth, mIndicatorInLineHeight, true);
        int selectedColor = ContextCompat.getColor(context, R.color.colorAccent);
        int unselectedColor = ContextCompat.getColor(context, android.R.color.white);
        int selectedLineColor = ContextCompat.getColor(context, android.R.color.white);
        int unselectedLineColor = ContextCompat.getColor(context, R.color.scroll_indicator_inline_color);
        mIndicatorShapeEnd.updateColor(selectedColor, unselectedColor, selectedLineColor, unselectedLineColor);
        mIndicatorShapeStart.updateColor(selectedColor, unselectedColor, selectedLineColor, unselectedLineColor);

        mImageWrapperLineShape = new ImageWrapperLineShape(mImageMargin, mImageShowHeight);
        mImageWrapperLineShape.updateColor(selectedColor, unselectedColor);

        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    private void getAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        if (attrs == null) return;
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ScrollSelectView, defStyleAttr, 0);
        if (a == null) return;

        mImageShowHeight = a.getDimension(R.styleable.ScrollSelectView_imageShowHeight, DeviceScreenUtils.dp2px(65f, getContext()));
        mImageMargin = a.getDimension(R.styleable.ScrollSelectView_imageMargin, DeviceScreenUtils.dp2px(4f, getContext()));
        mIndicatorWidth = a.getDimension(R.styleable.ScrollSelectView_indicatorWidth, DeviceScreenUtils.dp2px(12f, getContext()));
        mMiddleLineWidth = DeviceScreenUtils.dp2px(2f, getContext());
        mIndicatorInLineHeight = a.getDimension(R.styleable.ScrollSelectView_indicatorInLineHeight, DeviceScreenUtils.dp2px(28f, getContext()));
        mIndicatorInLineWidth = a.getDimension(R.styleable.ScrollSelectView_indicatorInLineWidth, DeviceScreenUtils.dp2px(2f, getContext()));

        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = (int) (mImageShowHeight + mImageMargin * 2);
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
        int childTop = (int) (getPaddingTop() + mImageMargin);
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
        if (mSelectedImageWrapperLineShape != null && mSelectedClip != null) {
            changeTimeByMiddleLine(getChangeTime(dx));
            invalidate();
        } else if (mSelectedIndicatorShape != null && mSelectedClip != null) {
            if (mSelectedIndicatorShape == mIndicatorShapeStart) {
                changeTimeByStartIndicator(getChangeTime(dx));
            } else {
                changeTimeByEndIndicator(getChangeTime(dx));
            }
            invalidate();
        } else {
            mPosX += dx;
            mPosY += dy;
            scrollContent();
            updateSelectedClip();
            onProgressChange();
        }
    }

    private void updateSelectedClip() {
        mPreSelectedClip = mSelectedClip;
        mSelectedClip = null;
        for (Clip clip : mClips) {
            if (getProgress() >= clip.startTime && getProgress() <= clip.getEndTime()) {
                mSelectedClip = clip;
                break;
            }
        }
        if (mPreSelectedClip != mSelectedClip && mClipSelectedListener != null) {
            mClipSelectedListener.onClipSelected(mSelectedClip);
        }
    }

    public void changeTimeByStartIndicator(int offset) {
        onProgressChanged(mSeek, mSelectedClip.startTime, true);
        if (mClipIndicatorPosChangeListener != null
                && mClipIndicatorPosChangeListener.changeTimeByStartIndicator(mSelectedClip
                , offset, mMinClipShowTime, mMaxValue))
            return;
        int endValue = mSelectedClip.startTime + mSelectedClip.showTime;
        if (offset < 0) {
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
    }

    public void changeTimeByMiddleLine(int offset) {
        onProgressChanged(mSeek, mSelectedClip.startTime, true);
        if (mClipIndicatorPosChangeListener != null
                && mClipIndicatorPosChangeListener.changeTimeByMiddleLine(mSelectedClip
                , offset, mMinClipShowTime, mMaxValue))
            return;
        int endValue = mSelectedClip.startTime + mSelectedClip.showTime;
        if (endValue >= mMaxValue) {
            mSelectedClip.startTime = mMaxValue - mSelectedClip.showTime;
        }
        if (mSelectedClip.startTime <= 0) {
            mSelectedClip.startTime = 0;
        }
        mSelectedClip.startTime += offset;
    }

    private void scrollToTime(int time) {
        onProgressChanged(mSeek, time, true);
        mPosX = -1f * time / mMaxValue * getTotalLength();
        scrollContent();
        notifyProgressChange();
    }

    public void changeTimeByEndIndicator(int offset) {
        onProgressChanged(mSeek, mSelectedClip.getEndTime(), true);
        if (mClipIndicatorPosChangeListener != null
                && mClipIndicatorPosChangeListener.changeTimeByEndIndicator(mSelectedClip
                , offset, mMinClipShowTime, mMaxValue))
            return;
        mSelectedClip.showTime += offset;
        if (mSelectedClip.getEndTime() >= mMaxValue + 1)
            mSelectedClip.showTime = mMaxValue + 1 - mSelectedClip.startTime;
        if (mSelectedClip.showTime <= mMinClipShowTime)
            mSelectedClip.showTime = mMinClipShowTime;
    }

    private void checkTouchOnIndicator(float lastTouchX, float lastTouchY) {
        boolean touchImageWrapperLine = mImageWrapperLineShape.isOnTouch(lastTouchX, lastTouchY);
        boolean touchStart = mIndicatorShapeStart.isOnTouch(lastTouchX, lastTouchY);
        boolean touchEnd = mIndicatorShapeEnd.isOnTouch(lastTouchX, lastTouchY);
        isSelectedOnTouch = true;
        if (touchImageWrapperLine) {
            mSelectedImageWrapperLineShape = mImageWrapperLineShape;
            return;
        }
        if (touchStart && touchEnd) {
            mSelectedIndicatorShape = (mIndicatorShapeEnd.rightRectTopX + mIndicatorShapeEnd.rightRectTopX) / 2 > lastTouchX
                    ? mIndicatorShapeStart : mIndicatorShapeEnd;
        } else if (touchStart) {
            mSelectedIndicatorShape = mIndicatorShapeStart;
        } else if (touchEnd) {
            mSelectedIndicatorShape = mIndicatorShapeEnd;
        } else {
            isSelectedOnTouch = false;
            mSelectedIndicatorShape = null;
        }

    }

    private void onStartTouch() {
        isManual = true;
        onStartTrackingTouch(mSeek);
        if (mTouchStateListener != null) mTouchStateListener.onStartTouch();
        setStop(false);
    }

    public void setStop(boolean stop) {
        isStop = stop;
    }

    private void onProgressChange() {
        onProgressChanged(mSeek, getProgress(), true);
        notifyProgressChange();
    }

    private void notifyProgressChange() {
        invalidate();
    }

    private void onStopTouch() {
        if (mTouchStateListener != null) mTouchStateListener.onStopTouch();
        setStop(true);
        onStopTrackingTouch(mSeek);
        isSelectedOnTouch = false;
        isManual = false;
        for (Clip clip : mClips) {
            if (getProgress() >= clip.startTime && getProgress() <= clip.getEndTime()) {
                mSelectedClip = clip;
            }
        }
        if (mSelectedClip != null && mSelectedIndicatorShape != null) {
            if (mSelectedIndicatorShape == mIndicatorShapeStart) {
                scrollToTime(mSelectedClip.startTime);
            } else {
                scrollToTime(mSelectedClip.getEndTime());
            }
            mSelectedIndicatorShape = null;
        }
        if (mSelectedImageWrapperLineShape != null && mSelectedClip != null) {
            scrollToTime(mSelectedClip.startTime);
            mSelectedImageWrapperLineShape = null;
        }
        invalidate();
    }

    public void onProgressStop() {
        if (isManual) return;
        updateSelected();
        invalidate();
    }

    private void updateSelected() {
        updateSelectedClip();
    }

    public void onProgressStart() {
        if (isManual) return;
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
        super.dispatchDraw(canvas);
        drawTextRect(canvas);
        drawMiddleLine(canvas);
    }

    private void drawMiddleLine(Canvas canvas) {
        if ((mSelectedIndicatorShape != null || mSelectedImageWrapperLineShape != null) && isSelectedOnTouch) return;
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mMiddleLineColor);
        mPaint.setStrokeWidth(mMiddleLineWidth);
        mPaint.setShadowLayer(4, 0, 0, ContextCompat.getColor(getContext(), R.color.shadow_color));
        canvas.drawRect(mMiddlePos - mMiddleLineWidth / 2, 0, mMiddlePos + mMiddleLineWidth / 2, getHeight(), mPaint);
    }

    private void drawTextRect(Canvas canvas) {
        for (Clip clip : mClips) {
            float left = mMiddlePos + mPosX + getTotalLength() * clip.startTime / mMaxValue;
            float right = mMiddlePos + mPosX + getTotalLength() * clip.getEndTime() / mMaxValue;
            if (clip != mSelectedClip) {
                mPaint.clearShadowLayer();
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(mTextRectColor);
                canvas.drawRect(left, mImageMargin, right, mImageMargin + mImageShowHeight, mPaint);
            }
        }
        if (mSelectedClip != null) {
            float left = mMiddlePos + mPosX + getTotalLength() * mSelectedClip.startTime / mMaxValue;
            float right = mMiddlePos + mPosX + getTotalLength() * mSelectedClip.getEndTime() / mMaxValue;
            if (!isSelectedOnTouch && !isStop) {
                mPaint.clearShadowLayer();
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(mTextRectColor);
                canvas.drawRect(left, mImageMargin, right, mImageMargin + mImageShowHeight, mPaint);
            } else {
                mImageWrapperLineShape.update(left, right);
                mImageWrapperLineShape.selected(mSelectedImageWrapperLineShape);
                mImageWrapperLineShape.draw(canvas, mPaint);
                mIndicatorShapeStart.updateTopDot(left - mIndicatorWidth);
                mIndicatorShapeStart.selected(mSelectedIndicatorShape);
                mIndicatorShapeStart.draw(canvas, mPaint);
                mIndicatorShapeEnd.updateTopDot(right);
                mIndicatorShapeEnd.selected(mSelectedIndicatorShape);
                mIndicatorShapeEnd.draw(canvas, mPaint);
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
        if (isManual) {
            return;
        }
        mPosX = -1f * progress / mMaxValue * getTotalLength();
        isManual = false;
        updateSelectedClip();
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
        setStop(true);
        invalidate();
    }

    private void notifySelectedClip() {
        mSelectedClip = null;
        mSelectedIndicatorShape = null;
        mSelectedImageWrapperLineShape = null;
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
        mOnSeekProgressChangeListeners.add(listener);
    }

    public void addOnSeekProgressChangeListener(OnSeekProgressChangeListener listener) {
        mOnSeekProgressChangeListeners.add(listener);
    }

    private void onProgressChanged(SeekWrapper.SeekImpl seek, int progress, boolean fromUser) {
        for (OnSeekProgressChangeListener listener :
                mOnSeekProgressChangeListeners) {
            listener.onProgressChanged(seek, progress, fromUser);
        }
    }

    private void onStartTrackingTouch(SeekWrapper.SeekImpl seek) {
        for (OnSeekProgressChangeListener listener :
                mOnSeekProgressChangeListeners) {
            listener.onStartTrackingTouch(seek);
        }
    }

    private void onStopTrackingTouch(SeekWrapper.SeekImpl seek) {
        for (OnSeekProgressChangeListener listener :
                mOnSeekProgressChangeListeners) {
            listener.onStopTrackingTouch(seek);
        }
    }

    public class IndicatorShape {
        public float rightRectTopX;
        public float rightRectTopY;
        public float width;
        public float triangleHeight;
        public float rectangleHeight;
        public float inLineWith;
        public float inLineHeight;
        public int selectedColor;
        public int unSelectedColor;
        public int selectedLineColor;
        public int unSelectedLineColor;
        public boolean isLeft;
        public boolean isSelected;

        public IndicatorShape(float width, float triangleHeight, float rectangleHeight, float inLineWith, float inLineHeight, boolean isLeft) {
            this.width = width;
            this.triangleHeight = triangleHeight;
            this.rectangleHeight = rectangleHeight;
            this.inLineWith = inLineWith;
            this.inLineHeight = inLineHeight;
            this.isLeft = isLeft;
        }

        public void updateColor(int selectedColor, int unselectedColor, int selectedLineColor, int unselectedLineColor) {
            this.selectedColor = selectedColor;
            this.unSelectedColor = unselectedColor;
            this.selectedLineColor = selectedLineColor;
            this.unSelectedLineColor = unselectedLineColor;
        }

        public void updateTopDot(float x) {
            rightRectTopX = x;
            rightRectTopY = triangleHeight;
        }

        public void selected(IndicatorShape indicatorShape) {
            isSelected = this == indicatorShape;
        }

        public void draw(Canvas canvas, Paint paint) {
            paint.setStyle(Paint.Style.FILL);
            if (isLeft) {
                paint.setShadowLayer(4, -5, 0, ContextCompat.getColor(getContext(), R.color.shadow_color));
            } else {
                paint.setShadowLayer(4, 5, 0, ContextCompat.getColor(getContext(), R.color.shadow_color));
            }
            drawTriangle(canvas, paint);
            drawRectangleInline(canvas, paint);
        }

        private void drawTriangle(Canvas canvas, Paint paint) {
            paint.setColor(isSelected ? selectedColor : unSelectedColor);
            paint.setAntiAlias(true);
            Path path = new Path();
            if (isLeft) {
                path.moveTo(rightRectTopX, rightRectTopY);
                path.lineTo(rightRectTopX + width, 0);
                path.lineTo(rightRectTopX + width, rightRectTopY * 2 + rectangleHeight);
                path.lineTo(rightRectTopX, rightRectTopY + rectangleHeight);
                path.moveTo(rightRectTopX, rightRectTopY);
            } else {
                path.moveTo(rightRectTopX + width, rightRectTopY);
                path.lineTo(rightRectTopX, 0);
                path.lineTo(rightRectTopX, rightRectTopY * 2 + rectangleHeight);
                path.lineTo(rightRectTopX + width, rightRectTopY + rectangleHeight);
                path.moveTo(rightRectTopX + width, rightRectTopY);
            }
            path.close();
            canvas.drawPath(path, paint);
        }

        private void drawRectangleInline(Canvas canvas, Paint paint) {
            paint.setColor(isSelected ? selectedLineColor : unSelectedLineColor);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(inLineWith);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.clearShadowLayer();
            canvas.drawLine(rightRectTopX + width / 2
                    , rightRectTopY + (rectangleHeight - inLineHeight) / 2
                    , rightRectTopX + width / 2
                    , rightRectTopY + (rectangleHeight + inLineHeight) / 2
                    , paint);
        }

        public boolean isOnTouch(float x, float y) {
            float altTouchWidth = width;
            if (y > 0
                    && y < rightRectTopY * 2 + rectangleHeight
                    && x > rightRectTopX - width - altTouchWidth
                    && x < rightRectTopX + width + altTouchWidth)
                return true;
            return false;
        }
    }

    public class ImageWrapperLineShape {
        public float leftRectTopX;
        public float rightRectTopY;
        public float lineWidth;
        public float rightRectTopX;
        public float rectangleHeight;
        public int selectedColor;
        public int unSelectedColor;
        public boolean isSelected;

        public ImageWrapperLineShape(float lineWidth, float rectangleHeight) {
            this.lineWidth = lineWidth;
            this.rectangleHeight = rectangleHeight;
        }

        public void updateColor(int selectedColor, int unselectedColor) {
            this.selectedColor = selectedColor;
            this.unSelectedColor = unselectedColor;
        }

        public void update(float left, float right) {
            leftRectTopX = left - 0.5f;
            rightRectTopY = 0;
            rightRectTopX = right + 0.5f;
        }

        public void draw(Canvas canvas, Paint paint) {
            paint.clearShadowLayer();
            paint.setStyle(Paint.Style.FILL);
            drawRectangle(canvas, paint);
        }

        private void drawRectangle(Canvas canvas, Paint paint) {
            paint.setColor(isSelected ? selectedColor : unSelectedColor);
            canvas.drawRect(leftRectTopX
                    , rightRectTopY
                    , rightRectTopX
                    , lineWidth
                    , paint);

            canvas.drawRect(leftRectTopX
                    , rightRectTopY + lineWidth + rectangleHeight
                    , rightRectTopX
                    , rightRectTopY + lineWidth * 2 + rectangleHeight
                    , paint);
        }

        public boolean isOnTouch(float x, float y) {
            if (x > leftRectTopX
                    && x < rightRectTopX) {
                if ((y > 0 && y < lineWidth + lineWidth * 2)
                        || (y > rectangleHeight - lineWidth && y < lineWidth * 2 + rectangleHeight)) {
                    return true;
                }
            }
            return false;
        }

        public void selected(ImageWrapperLineShape selectedImageWrapperLineShape) {
            isSelected = this == selectedImageWrapperLineShape;
        }
    }

    public void setClipIndicatorPosChangeListener(ClipIndicatorPosChangeListener clipIndicatorPosChangeListener) {
        mClipIndicatorPosChangeListener = clipIndicatorPosChangeListener;
    }

    public void setClipSelectedListener(ClipSelectedListener clipSelectedListener) {
        mClipSelectedListener = clipSelectedListener;
    }

    public void setTouchStateListener(TouchStateListener touchStateListener) {
        mTouchStateListener = touchStateListener;
    }

    public interface ClipIndicatorPosChangeListener {
        boolean changeTimeByStartIndicator(Clip clip, int offset, int minValue, int maxValue);

        boolean changeTimeByEndIndicator(Clip clip, int offset, int minValue, int maxValue);

        boolean changeTimeByMiddleLine(Clip clip, int offset, int minValue, int maxValue);
    }

    public interface ClipSelectedListener {
        void onClipSelected(Clip clip);
    }

    public interface TouchStateListener {
        void onStartTouch();
        void onStopTouch();
    }
}
