package com.loopeer.android.photodrama4android.ui.widget;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import com.loopeer.android.photodrama4android.R;

import java.lang.reflect.Field;

public class FixCursorEditTextWithLineSpace extends android.support.v7.widget.AppCompatEditText {

    public FixCursorEditTextWithLineSpace(Context context) {
        this(context, null);
    }

    public FixCursorEditTextWithLineSpace(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.editTextStyle);
    }

    public FixCursorEditTextWithLineSpace(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Field f = null;
            try {
                f = TextView.class.getDeclaredField("mCursorDrawableRes");
                f.setAccessible(true);
                f.set(this, R.drawable.shape_subtitle_edittext_cursor);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}