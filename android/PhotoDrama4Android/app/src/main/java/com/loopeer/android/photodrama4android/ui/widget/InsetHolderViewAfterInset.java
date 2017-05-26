package com.loopeer.android.photodrama4android.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.util.AttributeSet;
import android.view.View;

import com.loopeer.android.photodrama4android.R;
import com.loopeer.compatinset.InsetHelper;
import com.loopeer.compatinset.InsetHolderView;
import com.loopeer.compatinset.ViewUtils;
import com.loopeer.compatinset.statusbar.StatusBarFontHelper;

import java.lang.reflect.Field;

public class InsetHolderViewAfterInset extends InsetHolderView {
    WindowInsetsCompat mLastInsets;
    static final boolean SHOW_INSET_HOLDER;
    private int mStatusBarColor;
    private int mStatusBarDarkColor;

    static {
        if (Build.VERSION.SDK_INT >= 19) {
            SHOW_INSET_HOLDER = true;
        } else {
            SHOW_INSET_HOLDER = false;
        }
    }

    public InsetHolderViewAfterInset(Context context) {
        this(context, null);
    }

    public InsetHolderViewAfterInset(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InsetHolderViewAfterInset(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.InsetHolderView,
                defStyleAttr, R.style.Widget_CompatInset_InsetHolderView);
        mStatusBarColor = a.getColor(R.styleable.InsetHolderView_insetStatusBarColor
                , ContextCompat.getColor(context, android.R.color.transparent));

        mStatusBarDarkColor = a.getColor(R.styleable.InsetHolderView_insetStatusBarColor
                , -1);
        int style = a.getInt(R.styleable.InsetHolderView_insetStatusBarStyle
                , 0);
        if (style == 1) {
            StatusBarFontHelper.setStatusBarMode(((Activity) context), false);
        } else if (style == 2) {
            StatusBarFontHelper.setStatusBarMode(((Activity) context), true);
        }

        if (mStatusBarDarkColor != -1 && style == 0) {
            int result = StatusBarFontHelper.setStatusBarMode(((Activity) context), false);
            if (result < 1)
                mStatusBarColor = mStatusBarDarkColor;
        }
        ViewCompat.setOnApplyWindowInsetsListener(this,
                new android.support.v4.view.OnApplyWindowInsetsListener() {
                    @Override
                    public WindowInsetsCompat onApplyWindowInsets(View v,
                                                                  WindowInsetsCompat insets) {
                        return onWindowInsetChanged(insets);
                    }
                });
    }

    WindowInsetsCompat onWindowInsetChanged(final WindowInsetsCompat insets) {
        WindowInsetsCompat newInsets = null;
        if (ViewCompat.getFitsSystemWindows(this)) {
            newInsets = insets;
        }
        if (!ViewUtils.objectEquals(mLastInsets, newInsets)) {
            mLastInsets = newInsets;
            requestLayout();
        }
        return insets.consumeSystemWindowInsets();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        InsetHelper.requestApplyInsets(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), getInsetHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(mStatusBarColor);
    }

    private int getInsetHeight() {
        return SHOW_INSET_HOLDER ? getStatusBarHeight(getContext()) : 0;
    }

    public static int getStatusBarHeight(Context context) {
        Class<?> c;
        Object obj;
        Field field;

        int x, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }
}
