package com.loopeer.android.photodrama4android.media.audio;


import android.content.Context;

import com.loopeer.android.photodrama4android.media.model.MusicClip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicProcessor implements MusicClipPlayer.MusicClipPlayerLister {

    public HashMap<String, MusicClipPlayer> mClipPlayerHashMap;
    public List<String> mHasNotPrepareClipKeys;
    private ProcessorPrepareListener mProcessorPrepareListener;

    public MusicProcessor() {
        mClipPlayerHashMap = new HashMap<>();
        mHasNotPrepareClipKeys = new ArrayList<>();
    }

    public void setProcessorPrepareListener(ProcessorPrepareListener processorPrepareListener) {
        mProcessorPrepareListener = processorPrepareListener;
    }

    public void updateMusicClipPlayer(Context context, List<MusicClip> clips) {
        for (MusicClip clip: clips) {
            if (!mClipPlayerHashMap.containsKey(clip.getKey())) {
                MusicClipPlayer clipPlayer = new MusicClipPlayer(context, clip, this);
                mHasNotPrepareClipKeys.add(clip.getKey());
                mClipPlayerHashMap.put(clip.getKey(), clipPlayer);
            }
        }

        for (Map.Entry<String, MusicClipPlayer> entry : mClipPlayerHashMap.entrySet()) {
            entry.getValue().preparePlayer();
        }
        if (clips.isEmpty()) {
            notifyPrepareFinished();
        }
    }


    public void releasePlayer() {
        for (Map.Entry<String, MusicClipPlayer> entry : mClipPlayerHashMap.entrySet()) {
            entry.getValue().releasePlayer();
        }
    }

    public void onProgressChange(int time) {
        for (Map.Entry<String, MusicClipPlayer> entry : mClipPlayerHashMap.entrySet()) {
            entry.getValue().onProgressChange(time);
        }
    }

    public void seekToMusic(int progress) {
        for (Map.Entry<String, MusicClipPlayer> entry : mClipPlayerHashMap.entrySet()) {
            entry.getValue().seekToMusic(progress);
        }
    }

    public void pauseMusic() {
        for (Map.Entry<String, MusicClipPlayer> entry : mClipPlayerHashMap.entrySet()) {
            entry.getValue().pauseMusic();
        }
    }

    public void startMusic() {
        for (Map.Entry<String, MusicClipPlayer> entry : mClipPlayerHashMap.entrySet()) {
            entry.getValue().startMusic();
        }
    }

    private void notifyPrepareFinished() {
        if (mProcessorPrepareListener != null)
            mProcessorPrepareListener.musicPrepareFinished();
    }

    @Override
    public void onMusicClipPlayerPrepared(String key) {
        if (mHasNotPrepareClipKeys.contains(key)) {
            mHasNotPrepareClipKeys.remove(key);
        }
        if (mHasNotPrepareClipKeys.size() == 0) {
            notifyPrepareFinished();
        }
    }

    public interface ProcessorPrepareListener{
        void musicPrepareFinished();
    }
}
