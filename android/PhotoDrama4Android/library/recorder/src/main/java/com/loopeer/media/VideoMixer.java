package com.loopeer.media;


public class VideoMixer {
    public VideoMixer() {
        NativeLibraryLoader.load();
    }

    public void mix(String input1, String input2, String output) {
        nativeMix(input1, input2, output);
    }

    native void nativeMix(String input1, String input2, String output);

}
