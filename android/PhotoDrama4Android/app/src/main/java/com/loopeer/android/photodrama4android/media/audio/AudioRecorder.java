package com.loopeer.android.photodrama4android.media.audio;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.loopeer.android.photodrama4android.BuildConfig;
import com.loopeer.android.photodrama4android.media.model.MusicClip;

import java.io.IOException;

import static com.loopeer.android.photodrama4android.utils.PermissionUtils.AUDIO_PERMISSIONS;
import static com.loopeer.android.photodrama4android.utils.PermissionUtils.REQUEST_RECORD_AUDIO_PERMISSION;

public class AudioRecorder {
    private static final String TAG = "AudioRecorder";

    private MediaRecorder mRecorder;
    private boolean permissionToRecordAccepted = false;

    public boolean startRecording(MusicClip musicClip) {
        if (!permissionToRecordAccepted) return false;
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
        } else {
            permissionToRecordAccepted = true;
        }
    }
    public boolean onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        return permissionToRecordAccepted;
    }

    public void onStop() {
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
    }
}
