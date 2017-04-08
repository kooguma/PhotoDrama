package com.loopeer.android.photodrama4android.media.utils;


import java.text.SimpleDateFormat;

public class DateUtils {

    public static String formatDate(long milliseconds, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(milliseconds);
    }

    public static String getCurrentTimeString() {
        return formatDate(System.currentTimeMillis(), "yyyyMMdd_HH_mm");
    }
}