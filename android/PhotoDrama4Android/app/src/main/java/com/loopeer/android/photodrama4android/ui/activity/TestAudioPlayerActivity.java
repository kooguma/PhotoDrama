package com.loopeer.android.photodrama4android.ui.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.loopeer.andebug.LogEntry;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.databinding.ActivityTestAudioPlayerBinding;
import com.loopeer.android.photodrama4android.media.audio.player.AudioProcessor;
import com.loopeer.android.photodrama4android.media.model.MusicClip;
import com.loopeer.android.photodrama4android.media.recorder.MediaAudioDecoder;
import com.loopeer.android.photodrama4android.media.recorder.MediaDecoder;
import com.loopeer.android.photodrama4android.media.utils.ZipUtils;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TestAudioPlayerActivity extends PhotoDramaBaseActivity {
    private final static int BUFFER_SIZE = 4096;

    private AudioProcessor mAudioProcessor;

    private ActivityTestAudioPlayerBinding mBinding;

    SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");

    private long mStart1 = 3000;
    private long mDuration1;

    private boolean isPrepared;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_test_audio_player);
        setupAudioPlayers();
    }

    private void setupAudioPlayers() {
        mAudioProcessor = new AudioProcessor(this);
        mAudioProcessor.setProcessorPrepareListener(
            new AudioProcessor.AudioProcessorPrepareListener() {
                @Override public void onProcessorPrepared() {
                    Log.e("TAG","processor prepared !");
                    isPrepared = true;
                }
            });

        File file = new File("/storage/emulated/0/photodrama/drama/drama_322");

        Flowable.fromCallable(() -> ZipUtils.xmlToDrama(file.getAbsolutePath()))
            .doOnNext(drama -> {
                if (drama != null && drama.audioGroup != null) {
                    List<MusicClip> clips = drama.audioGroup.musicClips;
                    Log.e("TAG", "clip size = " + clips.size());
                    List<MediaAudioDecoder> decoders = new ArrayList<>();
                    for (MusicClip clip : clips) {
                        MediaAudioDecoder decoder = new MediaAudioDecoder(clip,
                            new MediaDecoder.DecodeProgressCallback() {
                                @Override public void onFinish() {
                                    Log.e("TAG","clip key = " + clip.getKey());
                                    //mAudioProcessor.addClip(clip);
                                }
                            });
                        decoders.add(decoder);
                    }

                    for (MediaAudioDecoder decoder : decoders) {
                        decoder.decode();
                    }

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
    //}

    public void musicPlay(View view) {
        if (isPrepared) {
            mAudioProcessor.startMusic();
        }
    }

    public void musicPause(View view) {
        if (isPrepared) {
            mAudioProcessor.pauseMusic();
        }
    }

    public void musicSeekTo(View view) {
        if (isPrepared) {
            //primePlaySize = 14176
            //bytes length = 6000640
            //total time = 34000
            //1000 bad
            //2000 good
            //3000 good
            //4000 bad  0.11764706  748485 ;0.11764706 720133;0.11764706 720133
            //5000 bad  0.14705883 910799 ; 0.14705883  896623 ; 0.14705883 896623;
            //6000 good p1 = 0.1764706 f1 = 1058936 ; p2 = 0.1764706 f2 = 1058936 ; p3 = 0.1764706 f3 = 1073112;
            mAudioProcessor.seekToMusic(4000);
        }
    }

}
