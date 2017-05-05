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
        for (AudioClipPlayer player : mAudioPlayerPool) {
            player.onProgressChange(usedTime);
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

    public void releasePlayer() {
        for (AudioClipPlayer player : mAudioPlayerPool) {
            player.release();
        }
    }

    @Override public void onAudioClipPrepared(String key) {
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
