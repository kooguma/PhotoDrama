package com.loopeer.android.photodrama4android.ui.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.databinding.ActivityTestAudioPlayerBinding;
import com.loopeer.android.photodrama4android.media.audio.player.DramaAudioPlayer;
import com.loopeer.android.photodrama4android.media.model.MusicClip;
import com.loopeer.android.photodrama4android.media.recorder.MediaAudioDecoder;
import com.loopeer.android.photodrama4android.media.recorder.MediaDecoder;
import com.loopeer.android.photodrama4android.media.utils.ZipUtils;
import com.loopeer.android.photodrama4android.utils.FileManager;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

public class TestAudioPlayerActivity extends PhotoDramaBaseActivity {
    private final static int BUFFER_SIZE = 4096;

    private DramaAudioPlayer mDramaAudioPlayer;

    private ActivityTestAudioPlayerBinding mBinding;

    SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");

    private long mStart1;
    private long mDuration1;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_test_audio_player);
        setupAudioPlayers();
    }

    private void setupAudioPlayers() {
        mDramaAudioPlayer = new DramaAudioPlayer(this);

        File file = new File("/storage/emulated/0/photodrama/drama/drama_322");
        if (file.exists()) {
            Flowable.fromCallable(() -> ZipUtils.xmlToDrama(file.getAbsolutePath()))
                .doOnNext(drama -> {
                    if (drama != null && drama.audioGroup != null) {
                        List<MusicClip> clips = drama.audioGroup.getBgmClips();
                        Log.e("TAG", "size = " + clips.size());
                        MusicClip clip1 = clips.get(0);
                        MediaAudioDecoder decoder1 = new MediaAudioDecoder(clip1,
                            new MediaDecoder.DecodeProgressCallback() {
                                @Override public void onFinish() {
                                    Log.e("TAG", "decoder1 finish");
                                    String path = FileManager.getInstance()
                                        .getDecodeAudioFilePath(clip1);
                                    Log.e("TAG", "path = " + path);
                                    try {
                                        mDramaAudioPlayer.setBGMDataSource(path);
                                        mDramaAudioPlayer.setRecordDataSource(path);
                                        mDramaAudioPlayer.setEffectPlayer(path);
                                        mDramaAudioPlayer.prepare();
                                    Log.e("TAG","total time = " + mDramaAudioPlayer.getTotalTime());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        decoder1.decode();
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe();

        }
        /*        mAudioPlayer1 = new AudioPlayer();
        mAudioPlayer2 = new AudioPlayer();

        mAudioPlayer1.setPlaybackPositionUpdateListener(
            new AudioTrack.OnPlaybackPositionUpdateListener() {
                @Override public void onMarkerReached(AudioTrack track) {
                }

                @Override public void onPeriodicNotification(AudioTrack track) {
                    mStart1 = (mStart1 + 1000);
                    String start = formatter.format(mStart1);
                    Log.e("TAG", "start = " + start);
                    mBinding.txt1Start.setText(start);
                    mDuration1 = mDuration1 - 1000;
                    String end = formatter.format(mDuration1);
                    mBinding.txt1End.setText("-" + end);
                    int progress = (int) (mStart1 * 100.0f / mAudioPlayer1.getTotalTime());
                    mBinding.sbMusic1.setProgress(progress);
                    Log.e("TAG", "progress = " + progress);
                }
            });

        try {
            byte[] data;
            data = getPCMData(R.raw.music3);
            mAudioPlayer1.setDataSource(data);
            mAudioPlayer1.prepare();
            mAudioPlayer2.setDataSource(data);
            mAudioPlayer2.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mDuration1 = mAudioPlayer1.getTotalTime();
        String hms = formatter.format(mDuration1);
        //set total time
        mBinding.txt1Start.setText("00:00");
        mBinding.txt1End.setText("-" + hms);*/
    }

    public void musicPlay(View view) {
        mDramaAudioPlayer.play();
    }

    public void musicPause(View view) {
        mDramaAudioPlayer.pause();
    }

    public void musicSeekTo(View view) {
        mDramaAudioPlayer.seekTo(2);
    }

}
