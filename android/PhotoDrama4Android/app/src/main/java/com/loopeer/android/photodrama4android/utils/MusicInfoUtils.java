package com.loopeer.android.photodrama4android.utils;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import com.loopeer.android.photodrama4android.model.Voice;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class MusicInfoUtils {

    private static SimpleDateFormat formatter = new SimpleDateFormat("mm:ss.SS");
    private static MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();

    public static String getFormatDuration(String duration) {
        int time = (int) (Float.valueOf(duration) * 1000);
        return formatter.format(time);
    }

    public static String getFormatDuration(int duration) {
        return formatter.format(duration);
    }

    public static String getFormatDurationFromLocal(Context context, String filePath, Voice voice) {
        String duration = "0";
        String fileName = filePath + voice.getSaveName();
        try {
            metaRetriever.setDataSource(fileName);
            duration = metaRetriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_DURATION);
        } catch (Exception e) {

        }
        return formatter.format(Integer.valueOf(duration));
    }

    public static String getBgmFormatDurationFromLocal(Context context, Voice voice) {
        return getFormatDurationFromLocal(context,FileManager.getInstance().getBgmPath(),voice);
    }

    public static String getEffectFormatDurationFromLocal(Context context, Voice voice) {
        return getFormatDurationFromLocal(context,FileManager.getInstance().getEffectPath(),voice);
    }

    public static String getDefaultStartTime() {
        return formatter.format(0);
    }
}
