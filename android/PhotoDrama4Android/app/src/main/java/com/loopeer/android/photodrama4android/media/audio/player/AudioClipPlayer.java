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
import java.util.concurrent.Executor;

// a wrapper of AudioPlayer
public class AudioClipPlayer {

    private static final String TAG = "AudioClipPlayer";

    private AudioClipPrepareListener mAudioClipPrepareListener;

    private boolean isPrepared = false;

    public void setAudioPrepareListener(AudioClipPrepareListener listener) {
        this.mAudioClipPrepareListener = listener;
    }

    public boolean isPrepared() {
        Log.e(TAG,"isPrepared = " + isPrepared + " audio player is prepared = " + mAudioPlayer.isPrepared());
        return isPrepared && mAudioPlayer != null && mAudioPlayer.isPrepared();
    }

    public interface AudioClipPrepareListener {
        void onAudioClipPrepared(String key);
    }

    private MusicClip mMusicClip;
    private AudioPlayer mAudioPlayer;

    public AudioClipPlayer() {
        this(null, null);
    }

    public AudioClipPlayer(MusicClip clip, AudioClipPrepareListener listener) {
        mMusicClip = clip;
        mAudioClipPrepareListener = listener;
        mAudioPlayer = new AudioPlayer();
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
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,path);
        }
    }

    public void seekTo(int progress) {
        if (checkPlayerNotNull()) {
            mAudioPlayer.seekTo(progress);
        }
    }

    public void prepare() {
        if (checkPlayerNotNull()) {
            loadClip();
        }
    }

    public void play() {
        if (checkPlayerNotNull()) {
            mAudioPlayer.play();
        }
    }

    public void pause() {
        if (checkPlayerNotNull()) {
            mAudioPlayer.pause();
        }
    }

    public void stop() {
        if (checkPlayerNotNull()) {
            mAudioPlayer.stop();
        }
    }

    public void release() {
        if (checkPlayerNotNull()) {
            mAudioPlayer.release();
        }
    }

    public void onProgressChange(int usedTime) {
        if (checkPlayerNotNull()) {
            Log.e(TAG, "onProgressChange used time = " + usedTime);
            if (usedTime < mMusicClip.startTime || usedTime > mMusicClip.getEndTime()) {
                Log.e(TAG, "1");
                if (mAudioPlayer.isPlaying()) {
                    Log.e(TAG, "2" + "audio player is null " + (mAudioPlayer == null) + " isPrepared = " + isPrepared());
                    if (mAudioPlayer != null && isPrepared()) {
                        Log.e(TAG, "3");
                        mAudioPlayer.pause();
                    }
                }
            } else {
                Log.e(TAG, "key = " + mMusicClip.getKey());
                Log.e(TAG, "4" + " isPlaying = " + mAudioPlayer.isPlaying() + " isPause = " +
                    mAudioPlayer.isPause());
                if (!mAudioPlayer.isPlaying() && !mAudioPlayer.isPause()) {
                    Log.e(TAG, "5");
                    seekTo(usedTime);
                    if (mAudioPlayer != null && isPrepared()) {
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

    class FileLoadTask extends AsyncTask<String, Void, byte[]> {
        private final static int BUFFER_SIZE = 4096;

        private long start = 0;

        @Override protected byte[] doInBackground(String... params) {
            byte[] bytes = null;
            File file = new File(params[0]);
            start = System.currentTimeMillis();
            try {
                InputStream is = new FileInputStream(file);
                bytes = inputStreamToByte(is);
            } catch (IOException e) {
                e.printStackTrace();
                isPrepared = false;
                Log.e(TAG, "file load failed : " + e.getMessage());
            }
            Log.e(TAG, "time use = " + (System.currentTimeMillis() - start));
            return bytes;
        }

        @Override protected void onPostExecute(byte[] bytes) {
            try {
                isPrepared = true;
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
                isPrepared = false;
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
