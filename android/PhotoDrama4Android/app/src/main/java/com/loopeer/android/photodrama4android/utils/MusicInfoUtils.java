package com.loopeer.android.photodrama4android.utils;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import com.loopeer.android.photodrama4android.model.Voice;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class MusicInfoUtils {

    private static SimpleDateFormat formatter = new SimpleDateFormat("mm:ss.S");
    private static MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();

    public static String getFormatDuration(String duration) {
        int time = (int) (Float.valueOf(duration) * 1000);
        return formatter.format(time);
    }

    public static String getFormatDuration(int duration) {
        return formatter.format(duration);
    }

    public static String getFormatDurationFromLocal(String filePath, Voice voice) {
        String duration = "0";
        String fileName = filePath + voice.getSaveName();
        try {
            metaRetriever.setDataSource(fileName);
            duration = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        } catch (Exception e) {

        }
        return formatter.format(Integer.valueOf(duration));
    }

    public static String getFormatDurationFromLocal(String filePath) {
        String duration = "0";
        try {
            metaRetriever.setDataSource(filePath);
            duration = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        } catch (Exception e) {

        }
        return formatter.format(Integer.valueOf(duration));
    }

    public static String getBgmFormatDurationFromLocal(Voice voice) {
        return getFormatDurationFromLocal(FileManager.getInstance().getBgmPath(),voice);
    }

    public static String getEffectFormatDurationFromLocal(Voice voice) {
        return getFormatDurationFromLocal(FileManager.getInstance().getEffectPath(),voice);
    }

    public static String getDefaultStartTime() {
        return formatter.format(0);
    }
}
