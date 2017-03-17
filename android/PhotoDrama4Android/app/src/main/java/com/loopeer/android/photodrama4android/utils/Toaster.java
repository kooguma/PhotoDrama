package com.loopeer.android.photodrama4android.utils;

import android.content.Context;
import android.support.annotation.StringRes;
import android.view.Gravity;
import android.widget.Toast;

public class Toaster {

    private static Context sContext;

    public static void init(Context context) {
        sContext = context.getApplicationContext();
    }

    public static void showToast(@StringRes int resId) {
        checkInit();
        Toast toast = Toast.makeText(sContext, resId, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void showToast(CharSequence text) {
        checkInit();
        Toast toast = Toast.makeText(sContext, text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private static void checkInit() {
        if (sContext == null) {
            throw new IllegalStateException("call initAnalyst() first");
        }
    }
}
