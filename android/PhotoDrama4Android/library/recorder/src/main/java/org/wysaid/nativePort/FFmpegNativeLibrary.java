package org.wysaid.nativePort;

public class FFmpegNativeLibrary {
    static {
        NativeLibraryLoader.load();
    }

    public static native void avRegisterAll();

}
