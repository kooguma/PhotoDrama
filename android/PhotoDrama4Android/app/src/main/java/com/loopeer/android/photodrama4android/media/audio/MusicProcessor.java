package com.loopeer.android.photodrama4android.media.audio;


import android.content.Context;
import android.text.TextUtils;

import com.loopeer.android.photodrama4android.media.model.MusicClip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicProcessor implements MusicClipPlayer.MusicClipPlayerLister {

    public List<MusicClipPlayer> mMusicClipPlayerPool;
    public HashMap<String, MusicClipPlayer> mClipPlayerHashMap;
    public List<String> mHasNotPrepareClipKeys;
    private ProcessorPrepareListener mProcessorPrepareListener;

    public MusicProcessor() {
        mClipPlayerHashMap = new HashMap<>();
        mHasNotPrepareClipKeys = new ArrayList<>();
        mMusicClipPlayerPool = new ArrayList<>();
    }

    public void setProcessorPrepareListener(ProcessorPrepareListener processorPrepareListener) {
        mProcessorPrepareListener = processorPrepareListener;
    }

    public MusicClipPlayer getMediaPlayerFromPool(Context context, MusicClip clip) {
        if (mMusicClipPlayerPool.isEmpty()) {
            return new MusicClipPlayer(context, clip, this);
        } else {
            MusicClipPlayer player = mMusicClipPlayerPool.remove(0);
            player.update(context, clip, this);
            return player;
        }
    }

    public void putMediaPlayerToPool(MusicClipPlayer musicClipPlayer) {
        mMusicClipPlayerPool.add(musicClipPlayer);
    }

    public void updateMusicClipPlayer(Context context, List<MusicClip> clips) {
        List<String> removeIngKeys = new ArrayList<>();
        for (Map.Entry<String, MusicClipPlayer> entry : mClipPlayerHashMap.entrySet()) {
            if (clipRemovedFromList(entry.getKey(), clips)) {
                removeIngKeys.add(entry.getKey());
            }
        }
        putToPool(removeIngKeys);
        removeIngKeys.clear();
        for (MusicClip clip: clips) {
            if (clip.isCreateIng() || TextUtils.isEmpty(clip.path)) continue;
            if (!mClipPlayerHashMap.containsKey(clip.getKey())) {
                MusicClipPlayer clipPlayer = getMediaPlayerFromPool(context, clip);
                mHasNotPrepareClipKeys.add(clip.getKey());
                mClipPlayerHashMap.put(clip.getKey(), clipPlayer);
            }
        }

        boolean allPlayerPrepared = true;
        for (Map.Entry<String, MusicClipPlayer> entry : mClipPlayerHashMap.entrySet()) {
            if (entry.getValue().isNotPrepare()) {
                allPlayerPrepared = false;
            }
        }
        if (allPlayerPrepared) {
            notifyPrepareFinished();
        }
        for (Map.Entry<String, MusicClipPlayer> entry : mClipPlayerHashMap.entrySet()) {
            entry.getValue().preparePlayer();
        }

    }

    private void putToPool(List<String> removeIngKeys) {
        for (String key :
                removeIngKeys) {
            MusicClipPlayer player = mClipPlayerHashMap.remove(key);
            putMediaPlayerToPool(player);
        }
    }

    private boolean clipRemovedFromList(String key, List<MusicClip> clips) {
        for (MusicClip clip :
                clips) {
            if (clip.getKey().equals(key)) {
                return false;
            }
        }
        return true;
    }


    public void releasePlayer() {
        for (Map.Entry<String, MusicClipPlayer> entry : mClipPlayerHashMap.entrySet()) {
            entry.getValue().releasePlayer();
        }
        for (MusicClipPlayer player :
                mMusicClipPlayerPool) {
            player.releasePlayer();
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
