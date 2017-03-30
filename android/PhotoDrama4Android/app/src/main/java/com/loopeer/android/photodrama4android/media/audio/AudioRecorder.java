package com.loopeer.android.photodrama4android.media.audio;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.loopeer.android.photodrama4android.BuildConfig;
import com.loopeer.android.photodrama4android.PhotoDramaApp;
import com.loopeer.android.photodrama4android.media.model.MusicClip;

import java.io.IOException;

import static com.loopeer.android.photodrama4android.utils.PermissionUtils.AUDIO_PERMISSIONS;
import static com.loopeer.android.photodrama4android.utils.PermissionUtils.REQUEST_RECORD_AUDIO_PERMISSION;

public class AudioRecorder {
    private static final String TAG = "AudioRecorder";

    private MediaRecorder mRecorder;

    public boolean startRecording(MusicClip musicClip) {
        if (!hasAudioPermission(PhotoDramaApp.getAppContext())) return false;
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        if (BuildConfig.LOG_ON) {
            Log.e("TAG", "Record File path : " + musicClip.path);
        }
        mRecorder.setOutputFile(musicClip.path);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            return false;
        }
        mRecorder.start();
        return true;
    }

    public void stopRecording() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    public void requestPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.RECORD_AUDIO)) {
            } else {
                ActivityCompat.requestPermissions(activity, AUDIO_PERMISSIONS, REQUEST_RECORD_AUDIO_PERMISSION);
            }
        }
    }
    public boolean onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        return true;
    }

    public void onStop() {
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
    }

    public static boolean hasAudioPermission(Context context) {
        int perm = context.checkCallingOrSelfPermission(AUDIO_PERMISSIONS[0]);
        return perm == PackageManager.PERMISSION_GRANTED;
    }
}
