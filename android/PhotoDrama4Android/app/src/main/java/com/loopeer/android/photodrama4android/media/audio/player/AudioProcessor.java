package com.loopeer.android.photodrama4android.media.audio.player;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.loopeer.android.photodrama4android.media.audio.MusicClipPlayer;
import com.loopeer.android.photodrama4android.media.model.MusicClip;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AudioProcessor implements AudioClipPlayer.AudioClipPrepareListener {

    private static final String TAG = "AudioProcessor";

    private List<AudioClipPlayer> mAudioPlayerPool; //背景音乐 + 人声 + 音效
    public HashMap<String, AudioClipPlayer> mClipPlayerHashMap;
    public List<String> mHasNotPrepareClipKeys;

    private AudioProcessorPrepareListener mProcessorPrepareListener;

    public void setProcessorPrepareListener(AudioProcessorPrepareListener listener) {
        this.mProcessorPrepareListener = listener;
    }

    public AudioProcessor(Context context) {
        mClipPlayerHashMap = new HashMap<>();
        mHasNotPrepareClipKeys = new ArrayList<>();
        mAudioPlayerPool = new LinkedList<>();
    }

    public AudioClipPlayer getAudioClipPlayerFromPool(MusicClip clip) {
        if (mAudioPlayerPool.isEmpty()) {
            return new AudioClipPlayer(clip, this);
        } else {
            AudioClipPlayer player = mAudioPlayerPool.remove(0);
            player.update(clip, this);
            return player;
        }
    }

    public void putAudioClipPlayerToPool(AudioClipPlayer player) {
        mAudioPlayerPool.add(player);
    }

    public void updateAudioClipPlayer(Context context, List<MusicClip> clips) {
        List<String> removeIngKeys = new ArrayList<>();
        for (Map.Entry<String, AudioClipPlayer> entry : mClipPlayerHashMap.entrySet()) {
            //含有则不移除
            if (clipRemovedFromList(entry.getKey(), clips)) {
                removeIngKeys.add(entry.getKey());
            }
        }

        putToPool(removeIngKeys);

        removeIngKeys.clear();

        for (MusicClip clip : clips) {
            if (clip.isCreateIng() || TextUtils.isEmpty(clip.path)) continue;
            if (!mClipPlayerHashMap.containsKey(clip.getKey())) {
                AudioClipPlayer clipPlayer = getAudioClipPlayerFromPool(clip);
                mHasNotPrepareClipKeys.add(clip.getKey());
                mClipPlayerHashMap.put(clip.getKey(), clipPlayer);
            }
        }

        boolean allPlayerPrepared = true;

        for (Map.Entry<String, AudioClipPlayer> entry : mClipPlayerHashMap.entrySet()) {
            if (!entry.getValue().isPrepared()) {
                allPlayerPrepared = false;
            }
        }

        if (allPlayerPrepared) {
            notifyPrepareFinished();
        }

        for (Map.Entry<String, AudioClipPlayer> entry : mClipPlayerHashMap.entrySet()) {
            entry.getValue().prepare();
        }
    }

    private void putToPool(List<String> removeIngKeys) {
        for (String key :
            removeIngKeys) {
            AudioClipPlayer player = mClipPlayerHashMap.remove(key);
            putAudioClipPlayerToPool(player);
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

    public void addClips(List<MusicClip> clips) {
        if (mAudioPlayerPool != null) {
            mAudioPlayerPool.clear();
            for (MusicClip clip : clips) {
                AudioClipPlayer player = new AudioClipPlayer(clip, this);
                mAudioPlayerPool.add(player);
                player.prepare();
            }
        }
    }

    public void addClip(MusicClip clip) {
        if (mAudioPlayerPool != null) {
            AudioClipPlayer player = new AudioClipPlayer(clip, this);
            mAudioPlayerPool.add(player);
            player.prepare();
        }
    }

    public void onProgressChange(int usedTime) {
        for (Map.Entry<String, AudioClipPlayer> entry : mClipPlayerHashMap.entrySet()) {
            entry.getValue().onProgressChange(usedTime);
        }
    }

    public void seekToMusic(int progress) {
        for (Map.Entry<String, AudioClipPlayer> entry : mClipPlayerHashMap.entrySet()) {
            entry.getValue().seekTo(progress);
        }
    }

    public void pauseMusic() {
        for (Map.Entry<String, AudioClipPlayer> entry : mClipPlayerHashMap.entrySet()) {
            entry.getValue().pause();
        }
    }

    public void stopMusic(){
        for (Map.Entry<String, AudioClipPlayer> entry : mClipPlayerHashMap.entrySet()) {
            entry.getValue().stop();
        }
    }

    public void startMusic() {
        Log.e("tag", "time stamp : " + System.currentTimeMillis());
        for (Map.Entry<String, AudioClipPlayer> entry : mClipPlayerHashMap.entrySet()) {
            entry.getValue().play();
        }
    }

    public void releasePlayer() {
        for (Map.Entry<String, AudioClipPlayer> entry : mClipPlayerHashMap.entrySet()) {
            entry.getValue().release();
        }
        for (AudioClipPlayer player :
            mAudioPlayerPool) {
            player.release();
        }
    }

    private void notifyPrepareFinished() {
        if (mProcessorPrepareListener != null) {
            mProcessorPrepareListener.onProcessorPrepared();
        }
    }

    @Override public void onAudioClipPrepared(String key) {
        if (mHasNotPrepareClipKeys.contains(key)) {
            mHasNotPrepareClipKeys.remove(key);
        }
        if (mHasNotPrepareClipKeys.size() == 0) {
            notifyPrepareFinished();
        }
    }

    public interface AudioProcessorPrepareListener {
        void onProcessorPrepared();
    }
}
