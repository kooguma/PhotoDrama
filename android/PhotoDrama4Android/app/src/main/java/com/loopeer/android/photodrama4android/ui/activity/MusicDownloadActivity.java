package com.loopeer.android.photodrama4android.ui.activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import com.fastui.uipattern.IRecycler;
import com.laputapp.http.BaseResponse;
import com.laputapp.ui.adapter.RxRecyclerAdapter;
import com.laputapp.ui.decorator.DividerItemDecoration;
import com.laputapp.utilities.DeviceScreenUtils;
import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.api.service.VoiceService;
import com.loopeer.android.photodrama4android.media.model.MusicClip;
import com.loopeer.android.photodrama4android.media.utils.AudioFetchHelper;
import com.loopeer.android.photodrama4android.model.Category;
import com.loopeer.android.photodrama4android.model.Voice;
import com.loopeer.android.photodrama4android.ui.adapter.MusicDownloadAdapter;
import com.loopeer.android.photodrama4android.utils.Toaster;
import io.reactivex.Flowable;
import java.io.IOException;
import java.util.List;

public class MusicDownloadActivity extends PhotoDramaBaseActivity
    implements IRecycler<Voice>, MusicDownloadAdapter.IMusicDownloadAdapter,
    MediaPlayer.OnPreparedListener {

    private MediaPlayer mMediaPlayer;
    private AudioFetchHelper mAudioFetchHelper;

    private Category mCategory;

    private MusicClip.MusicType mType;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_list);
        mType = (MusicClip.MusicType) getIntent().getSerializableExtra(Navigator.EXTRA_MUSIC_CLIP);
        mCategory = (Category) getIntent().getSerializableExtra(Navigator.EXTRA_CATEGORY);
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(this);
        mAudioFetchHelper = new AudioFetchHelper(this);
    }

    @Override protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getRecyclerManager().getRecyclerView()
            .addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST, 0,
                DeviceScreenUtils.dp2px(0.5f, this)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setCenterTitle(mCategory.name);
    }

    @Override public RxRecyclerAdapter<Voice> createRecyclerViewAdapter() {
        return new MusicDownloadAdapter(this, this);
    }

    @Override
    public Flowable<? extends BaseResponse<List<Voice>>> requestData(String offset, String page, String pageSize) {
        return VoiceService.INSTANCE.voices(mCategory.id);
    }

    @Override public void onMusicDownloadClick(Voice voice, TextView txtProgress) {
        mAudioFetchHelper.getAudio(mType, voice, status -> {
            txtProgress.setText(
                getString(R.string.common_percent_format, status.getPercentNumber()));
        }, throwable -> {
            Toaster.showToast("下载失败：" + throwable.getMessage());
        }, () -> {
            Toaster.showToast("下载完成");
        });
    }

    @Override protected void onResume() {
        super.onResume();
        mMediaPlayer.start();
    }

    @Override protected void onPause() {
        super.onPause();
        mMediaPlayer.pause();
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        mAudioFetchHelper.unSubscribe();
        mMediaPlayer.stop();
        mMediaPlayer.release();
    }

    @Override public void onItemClick(Voice voice) {
        mMediaPlayer.reset();
        try {
            mMediaPlayer.setDataSource(this, Uri.parse(voice.voiceUrl));
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override public void onPrepared(MediaPlayer mp) {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        } else {
            mMediaPlayer.start();
        }
    }
}
