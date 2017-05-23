package com.loopeer.android.photodrama4android.utils;

import android.media.MediaMetadataRetriever;
import com.loopeer.android.photodrama4android.model.Voice;
import java.text.SimpleDateFormat;

public class MusicInfoUtils {

    private static final int SUBS_LENGTH = 7;
    private static SimpleDateFormat formatter = new SimpleDateFormat("mm:ss.S");
    private static MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();

    public static String getFormatDuration(String duration) {
        int time = (int) (Float.valueOf(duration) * 1000);
        return formatter.format(time).substring(0,SUBS_LENGTH);
    }

    public static String getFormatDuration(int duration) {
        return formatter.format(duration).substring(0,SUBS_LENGTH);
    }

    public static String getFormatDurationFromLocal(String filePath, Voice voice) {
        String duration = "0";
        String fileName = filePath + voice.getSaveName();
        try {
            metaRetriever.setDataSource(fileName);
            duration = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        } catch (Exception e) {

        }
        return formatter.format(Integer.valueOf(duration)).substring(0,SUBS_LENGTH);
    }

    public static String getFormatDurationFromLocal(String filePath) {
        String duration = "0";
        try {
            metaRetriever.setDataSource(filePath);
            duration = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        } catch (Exception e) {

        }
        return formatter.format(Integer.valueOf(duration)).substring(0,SUBS_LENGTH);
    }

    public static String getBgmFormatDurationFromLocal(Voice voice) {
        return getFormatDurationFromLocal(FileManager.getInstance().getBgmPath(), voice);
    }

    public static String getEffectFormatDurationFromLocal(Voice voice) {
        return getFormatDurationFromLocal(FileManager.getInstance().getEffectPath(), voice);
    }

    public static String getDefaultStartTime() {
        return formatter.format(0).substring(0,SUBS_LENGTH);
    }
}
