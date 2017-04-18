package com.loopeer.android.photodrama4android.media.recorder;


public class AudioBufferTimeParser {
    public final static long AUDIO_BYTE_SPERSAMPLE = 44100 * 16 / 8;
    public static int BUFFER_SIZE = 4096;
    public static long BUFFER_SIZE_TIME_LENGTH = getTimeOffset(BUFFER_SIZE);

    public static int getDataOffset(long time) {
        return (int) Math.round(1d * time * AUDIO_BYTE_SPERSAMPLE * 2 / 1000000);
    }

    public static long getTimeOffset(int data) {
        return (Math.round(1000000d * (data / 2.0) / AUDIO_BYTE_SPERSAMPLE));
    }
}
