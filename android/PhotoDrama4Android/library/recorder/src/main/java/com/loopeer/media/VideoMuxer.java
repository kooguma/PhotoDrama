package com.loopeer.media;


public class VideoMuxer {

    public VideoMuxer() {
        NativeLibraryLoader.load();
    }

    public void muxing(String videoPath, String audioPath) {
        nativeConvert(videoPath, audioPath);
    }

    native void nativeConvert(String videoPath, String audioPath);
}
