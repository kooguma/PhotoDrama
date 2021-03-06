package com.loopeer.android.photodrama4android.ui.activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import com.fastui.uipattern.IRecycler;
import com.laputapp.http.BaseResponse;
import com.laputapp.rx.RxBus;
import com.laputapp.ui.adapter.RxRecyclerAdapter;
import com.laputapp.ui.decorator.DividerItemDecoration;
import com.laputapp.utilities.DeviceScreenUtils;
import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.analytics.Analyst;
import com.loopeer.android.photodrama4android.api.ResponseObservable;
import com.loopeer.android.photodrama4android.api.service.VoiceService;
import com.loopeer.android.photodrama4android.event.MusicDownLoadSuccessEvent;
import com.loopeer.android.photodrama4android.media.model.MusicClip;
import com.loopeer.android.photodrama4android.media.utils.AudioFetchHelper;
import com.loopeer.android.photodrama4android.model.Category;
import com.loopeer.android.photodrama4android.model.Voice;
import com.loopeer.android.photodrama4android.ui.adapter.MusicDownloadAdapter;
import com.loopeer.android.photodrama4android.ui.adapter.OnItemClickListener;
import com.loopeer.android.photodrama4android.utils.FileManager;
import com.loopeer.android.photodrama4android.utils.Toaster;
import io.reactivex.Flowable;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class MusicDownloadActivity extends PhotoDramaBaseActivity
    implements IRecycler<Voice>,
    MediaPlayer.OnPreparedListener, OnItemClickListener<Voice> {

    private MediaPlayer mMediaPlayer;
    private Category mCategory;
    private MusicDownloadAdapter mMusicDownloadAdapter;
    private MusicClip.MusicType mType;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_list);
        mType = (MusicClip.MusicType) getIntent().getSerializableExtra(Navigator.EXTRA_MUSIC_CLIP);
        mCategory = (Category) getIntent().getSerializableExtra(Navigator.EXTRA_CATEGORY);
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(this);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public RxRecyclerAdapter<Voice> createRecyclerViewAdapter() {
        mMusicDownloadAdapter = new MusicDownloadAdapter(this, mType);;
        mMusicDownloadAdapter.setListener(this);
        return mMusicDownloadAdapter;
    }

    @Override
    public Flowable<? extends BaseResponse<List<Voice>>> requestData(String offset, String page, String pageSize) {
        return VoiceService.INSTANCE.voices(mCategory.id);
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
        mMusicDownloadAdapter.onDestroy();
        mMediaPlayer.stop();
        mMediaPlayer.release();
    }

    @Override public void onItemClick(Voice voice) {
        mMediaPlayer.reset();
        try {
            String voicePath = mType == MusicClip.MusicType.BGM ?
                               FileManager.getInstance().getAudioBgmPath(this, voice) :
                               FileManager.getInstance().getAudioEffectPath(this, voice);
            File voiceFile = new File(voicePath);

            if (voiceFile.exists()) {
                mMediaPlayer.setDataSource(this, Uri.fromFile(voiceFile));
                mMediaPlayer.prepareAsync();
            } else {
                // TODO: 2017/5/24   在线播放调研
                // mMediaPlayer.setDataSource(this, Uri.parse(voice.voiceUrl));
            }
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
