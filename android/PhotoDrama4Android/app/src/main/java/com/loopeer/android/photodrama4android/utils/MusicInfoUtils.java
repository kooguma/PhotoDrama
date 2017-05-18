package com.loopeer.android.photodrama4android.utils;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import com.loopeer.android.photodrama4android.model.Voice;
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

    public static String getFormatDurationFromLocal(Context context, Voice voice) {
        String filePath = FileManager.getInstance().getAudioPath(context, voice);
        metaRetriever.setDataSource(filePath);
        String duration = metaRetriever.extractMetadata(
            MediaMetadataRetriever.METADATA_KEY_DURATION);
        return formatter.format(Integer.valueOf(duration));
    }

    public static String getDefaultStartTime() {
        return formatter.format(0);
    }
}
