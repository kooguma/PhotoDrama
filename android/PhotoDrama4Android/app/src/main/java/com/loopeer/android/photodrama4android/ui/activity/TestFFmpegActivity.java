package com.loopeer.android.photodrama4android.ui.activity;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.databinding.ActivityTestFfmpegBinding;
import com.loopeer.android.photodrama4android.utils.FileManager;
import com.loopeer.media.VideoMixer;
import com.loopeer.media.VideoMuxer;

public class TestFFmpegActivity extends AppCompatActivity {

    private ActivityTestFfmpegBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_test_ffmpeg);
    }

    public void onBtnClick(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
               /* new VideoMixer().mix(
                        "/storage/emulated/0/talk/download/car.wav"
                        *//*mBinding.editPath1.getText().toString()*//*
                        , "/storage/emulated/0/talk/download/wind.wav"*//*mBinding.editPath2.getText().toString()*//*
                        , FileManager.getInstance().createNewAudioWAVFile());*/
                new VideoMuxer().muxing(
                        "/storage/emulated/0/qqmusic/song/Eliza Doolittle - Rollerblades [mqms2].mp3"
                        , FileManager.getInstance().createNewAudioFile());
            }
        }).start();
    }
}
