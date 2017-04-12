package com.loopeer.media;

public class FFmpegNativeLibrary {
    static {
        NativeLibraryLoader.load();
    }

    public static native void avRegisterAll();

}
