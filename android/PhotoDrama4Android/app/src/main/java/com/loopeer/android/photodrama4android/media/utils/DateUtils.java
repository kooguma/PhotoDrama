package com.loopeer.android.photodrama4android.media.utils;


import java.text.SimpleDateFormat;

public class DateUtils {

    public static String formatDate(long milliseconds, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(milliseconds);
    }

    public static String formatTime(int time) {
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
        return formatter.format(time);
    }

    public static String formatTimeMilli(int time) {
        if (time > 0) time = time + 1;
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss.S");
        return formatter.format(time);
    }

    public static String getCurrentTimeString() {
        return formatDate(System.currentTimeMillis(), "yyyyMMdd_HH_mm");
    }

    public static String getCurrentTimeVideoString() {
        return "VID_" + formatDate(System.currentTimeMillis(), "yyyyMMdd_HHmmss");
    }
}
