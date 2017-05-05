package com.loopeer.android.photodrama4android.media.audio.player;

import android.os.AsyncTask;
import android.util.Log;
import com.loopeer.android.photodrama4android.BuildConfig;
import com.loopeer.android.photodrama4android.media.model.MusicClip;
import com.loopeer.android.photodrama4android.media.recorder.MediaAudioDecoder;
import com.loopeer.android.photodrama4android.utils.FileManager;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

// a wrapper of AudioPlayer
public class AudioClipPlayer {

    private static final String TAG = "AudioClipPlayer";

    public static class State {
        public static final int UNPREPARED = 0x00001; //未就绪
        public static final int PREPARED = 0x00010; //已经就绪
        public static final int PLAYING = 0x00100; //播放中
        public static final int PAUSE = 0x01000; //暂停
        public static final int STOP = 0x10000; //
    }

    private AudioClipPrepareListener mAudioClipPrepareListener;

    private int mState;

    public void setAudioPrepareListener(AudioClipPrepareListener listener) {
        this.mAudioClipPrepareListener = listener;
    }

    public interface AudioClipPrepareListener {
        void onAudioClipPrepared(String key);
    }

    private MusicClip mMusicClip;
    private AudioPlayer mAudioPlayer;
    private MediaAudioDecoder mDecoder;

    public AudioClipPlayer() {
        this(null, null);
    }

    public AudioClipPlayer(MusicClip clip, AudioClipPrepareListener listener) {
        mMusicClip = clip;
        mAudioClipPrepareListener = listener;
        mAudioPlayer = new AudioPlayer();
        mDecoder = new MediaAudioDecoder(mMusicClip, null);
    }

    public void update(MusicClip clip, AudioClipPrepareListener listener) {
        mMusicClip = clip;
        mAudioClipPrepareListener = listener;
    }

    public void setMusicClip(MusicClip clip) {
        mMusicClip = clip;
    }

    private void loadClip() {
        if (mMusicClip != null) {
            FileLoadTask task = new FileLoadTask();
            String path = FileManager.getInstance().getDecodeAudioFilePath(mMusicClip);
            Log.e(TAG, "decode audio file path = " + path);
            task.execute(path);
        }
    }

    public void seekTo(int progress) {
        if (checkPlayerNotNull()) {
            mAudioPlayer.seekTo(progress);
        }
    }

    public boolean isPrepared() {
        return mState == State.PREPARED;
    }

    public boolean isPlaying() {
        return mState == State.PLAYING;
    }

    public boolean isPause() {
        return mState == State.PAUSE;
    }

    public boolean isStop() {
        return mState == State.STOP;
    }

    public void prepare() {
        if (checkPlayerNotNull()) {
            checkState(State.UNPREPARED);
            loadClip();
        }
    }

    public void play() {
        if (checkPlayerNotNull()) {
            checkState(State.PLAYING | State.PREPARED | State.PAUSE);
            mState = State.PLAYING;
            mAudioPlayer.play();
        }
    }

    public void pause() {
        if (checkPlayerNotNull()) {
            checkState(State.PAUSE | State.PREPARED | State.PLAYING);
            mState = State.PAUSE;
            mAudioPlayer.pause();
        }
    }

    public void stop() {
        if (checkPlayerNotNull()) {
            mState = State.STOP;
            mAudioPlayer.stop();
        }
    }

    public void release() {
        if (checkPlayerNotNull()) {
            mAudioPlayer.release();
            mState = State.UNPREPARED;
        }
    }

    public void onProgressChange(int usedTime) {
        if (checkPlayerNotNull()) {
            Log.e(TAG, "onProgressChange used time = " + usedTime);
            if (usedTime < mMusicClip.startTime || usedTime > mMusicClip.getEndTime()) {
                Log.e(TAG, "1");
                if (mAudioPlayer.isPlaying()) {
                    Log.e(TAG, "2");
                    if (mAudioPlayer != null && isPrepared()) {
                        Log.e(TAG, "3");
                        mAudioPlayer.pause();
                    }
                }
            } else {
                Log.e(TAG,"key = " + mMusicClip.getKey());
                Log.e(TAG, "4" + " isPlaying = " + mAudioPlayer.isPlaying() + " isPause = " + isPause());
                if (!mAudioPlayer.isPlaying()  && !isPause()) {
                    Log.e(TAG, "5");
                    seekTo(usedTime);
                    if (mAudioPlayer!= null && isPrepared()) {
                        Log.e(TAG, "6");
                        mAudioPlayer.play();
                    }
                }
            }
        }
    }

    private boolean checkPlayerNotNull() {
        return mAudioPlayer != null;
    }

    private void checkState(int stateExpected) {
        // if ((mState & stateExpected) > 0) {
        //     throw new IllegalStateException("current state: " + mState);
        // }
    }

    class FileLoadTask extends AsyncTask<String, Void, byte[]> {
        private final static int BUFFER_SIZE = 4096;

        @Override protected byte[] doInBackground(String... params) {
            byte[] bytes = null;
            File file = new File(params[0]);
            mDecoder.updateDecoder(mMusicClip, null);
            try {
                //decode
                mDecoder.decode();
                InputStream is = new FileInputStream(file);
                bytes = inputStreamToByte(is);
            } catch (IOException e) {
                e.printStackTrace();
                mState = State.UNPREPARED;
                Log.e(TAG, "file load failed : " + e.getMessage());
            }
            return bytes;
        }

        @Override protected void onPostExecute(byte[] bytes) {
            try {
                mState = State.PREPARED;
                mAudioPlayer.setDataSource(bytes);
                mAudioPlayer.prepare();
                if (mAudioClipPrepareListener != null) {
                    mAudioClipPrepareListener.onAudioClipPrepared(mMusicClip.getKey());
                }
                if (BuildConfig.DEBUG) {
                    if (bytes != null) {
                        Log.e(TAG, "file load finish: " + " bytes.length = " + bytes.length);
                    } else {
                        Log.e(TAG, "file load finish: bytes = null");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                mState = State.UNPREPARED;
                Log.e(TAG, "file load failed : " + e.getMessage());
            }
        }

        private byte[] inputStreamToByte(InputStream in) throws IOException {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] data = new byte[BUFFER_SIZE];
            int count = -1;
            while ((count = in.read(data, 0, BUFFER_SIZE)) != -1) {
                outStream.write(data, 0, count);
            }
            return outStream.toByteArray();
        }
    }
}
