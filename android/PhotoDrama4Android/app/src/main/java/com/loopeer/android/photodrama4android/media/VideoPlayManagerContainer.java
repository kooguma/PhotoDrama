package com.loopeer.android.photodrama4android.media;


import android.app.Activity;
import android.content.Context;

import java.util.LinkedHashMap;

public class VideoPlayManagerContainer {

    public LinkedHashMap<String, VideoPlayerManager> mVideoPlayerManagerLinkedHashMap = new LinkedHashMap<>();

    private static volatile VideoPlayManagerContainer sDefaultInstance;

    private VideoPlayManagerContainer() {
    }

    public static VideoPlayManagerContainer getDefault() {
        if (sDefaultInstance == null) {
            synchronized (VideoPlayManagerContainer.class) {
                if (sDefaultInstance == null) {
                    sDefaultInstance = new VideoPlayManagerContainer();
                }
            }
        }
        return sDefaultInstance;
    }

    public VideoPlayerManager getVideoPlayerManager(Context context) {
        if (mVideoPlayerManagerLinkedHashMap.containsKey(getContextKey(context)))
            return mVideoPlayerManagerLinkedHashMap.get(getContextKey(context));
        return null;
    }

    public void putVideoManager(Context context, VideoPlayerManager videoPlayerManager) {
        mVideoPlayerManagerLinkedHashMap.put(getContextKey(context), videoPlayerManager);
    }

    public String getContextKey(Context context) {
        return ((Activity)context).getClass().toString();
    }

    public void bitmapLoadReady(Context context) {
        VideoPlayerManager manager = VideoPlayManagerContainer.getDefault().getVideoPlayerManager(context);
        if (manager != null)
            manager.bitmapLoadReady();
    }

    public void onFinish(Context context) {
        VideoPlayerManager manager = mVideoPlayerManagerLinkedHashMap.get(getContextKey(context));
        if (manager != null) mVideoPlayerManagerLinkedHashMap.remove(getContextKey(context));
    }

    public void subtitleLoadReady(Context context) {
        VideoPlayerManager manager = VideoPlayManagerContainer.getDefault().getVideoPlayerManager(context);
        if (manager != null)
            manager.subtitleLoadReady();
    }
}
