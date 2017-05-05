package com.loopeer.android.photodrama4android.media.audio.player;

import android.content.Context;
import android.util.Log;
import com.loopeer.android.photodrama4android.media.model.MusicClip;
import java.util.LinkedList;
import java.util.List;

public class AudioProcessor implements AudioClipPlayer.AudioClipPrepareListener {

    private static final String TAG = "AudioProcessor";

    private List<AudioClipPlayer> mAudioPlayerPool; //背景音乐 + 人声 + 音效
    //private HashMap<String, AudioClipPlayer> mPlayerHashMap;
    //private List<String> mHasNotPrepareClipKeys;

    private AudioProcessorPrepareListener mProcessorPrepareListener;

    public void setProcessorPrepareListener(AudioProcessorPrepareListener listener) {
        this.mProcessorPrepareListener = listener;
    }

    public AudioProcessor(Context context) {
        mAudioPlayerPool = new LinkedList<>();
    }

    public void addClips(List<MusicClip> clips) {
        if (mAudioPlayerPool != null) {
            for (MusicClip clip : clips) {
                AudioClipPlayer player = new AudioClipPlayer(clip, this);
                mAudioPlayerPool.add(player);
            }
        }
    }

    public void addClip(MusicClip clip) {
        if (mAudioPlayerPool != null) {
            AudioClipPlayer player = new AudioClipPlayer(clip, this);
            mAudioPlayerPool.add(player);
        }
    }

    public void prepareMusic() {
        for (AudioClipPlayer player : mAudioPlayerPool) {
            if(!player.isPrepared()) {
                player.prepare();
            }
        }
    }

    public void seekToMusic(int progress) {
        for (AudioClipPlayer player : mAudioPlayerPool) {
            player.seekTo(progress);
        }
    }

    public void pauseMusic() {
        for (AudioClipPlayer player : mAudioPlayerPool) {
            player.pause();
        }
    }

    public void startMusic() {
        for (AudioClipPlayer player : mAudioPlayerPool) {
            player.play();
        }
    }

    // public AudioClipPlayer getAudioPlayerFromPool(Context context, MusicClip clip) {
    //     if (mAudioPlayerPool.isEmpty()) {
    //         return new AudioClipPlayer(clip, this);
    //     } else {
    //         AudioClipPlayer player = mAudioPlayerPool.remove(0);
    //         player.update(clip, this);
    //         return player;
    //     }
    // }

    // public void putAudioPlayerToPool(AudioClipPlayer player) {
    //     mAudioPlayerPool.add(player);
    // }

    // public void updateAudioClipPlayer(Context context, List<MusicClip> clips) {
    //     List<String> removeIngKeys = new ArrayList<>();
    //
    //     for (Map.Entry<String, AudioClipPlayer> entry : mPlayerHashMap.entrySet()) {
    //         if (clipRemovedFromList(entry.getKey(), clips)) {
    //             removeIngKeys.add(entry.getKey());
    //         }
    //     }
    //
    //     putToPool(removeIngKeys);
    //     removeIngKeys.clear();
    //
    //     for (MusicClip clip : clips) {
    //         if (clip.isCreateIng() || TextUtils.isEmpty(clip.path)) continue;
    //         if (!mPlayerHashMap.containsKey(clip.getKey())) {
    //             AudioClipPlayer clipPlayer = getAudioPlayerFromPool(context, clip);
    //             mHasNotPrepareClipKeys.add(clip.getKey());
    //             mPlayerHashMap.put(clip.getKey(), clipPlayer);
    //         }
    //     }
    //
    //     boolean allPlayerPrepared = true;
    //
    //     for (Map.Entry<String, AudioClipPlayer> entry : mPlayerHashMap.entrySet()) {
    //         if (!entry.getValue().isPrepared()) {
    //             allPlayerPrepared = false;
    //         }
    //     }
    //
    //     if (allPlayerPrepared) {
    //         notifyPrepareFinished();
    //     }
    //
    //     for (Map.Entry<String, AudioClipPlayer> entry : mPlayerHashMap.entrySet()) {
    //         entry.getValue().prepare();
    //     }
    // }
    //
    // private void putToPool(List<String> removeIngKeys) {
    //     for (String key : removeIngKeys) {
    //         AudioClipPlayer player = mPlayerHashMap.remove(key);
    //         putAudioPlayerToPool(player);
    //     }
    // }
    //
    // private boolean clipRemovedFromList(String key, List<MusicClip> clips) {
    //     for (MusicClip clip : clips) {
    //         if (clip.getKey().equals(key)) {
    //             return false;
    //         }
    //     }
    //     return true;
    // }

    private void notifyPrepareFinished() {
        if (mProcessorPrepareListener != null) {
            mProcessorPrepareListener.onProcessorPrepared();
        }
    }

    @Override public void onAudioClipPrepared(String key) {
        Log.e(TAG,"KEY = " + key);
        boolean isProcessorPrepared = true;
        for (AudioClipPlayer player : mAudioPlayerPool) {
            if (!player.isPrepared()) {
                isProcessorPrepared = false;
                break;
            }
        }
        if (isProcessorPrepared && mProcessorPrepareListener != null) {
            mProcessorPrepareListener.onProcessorPrepared();
        }
    }

    public interface AudioProcessorPrepareListener {
        void onProcessorPrepared();
    }
}
