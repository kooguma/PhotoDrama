package com.loopeer.android.photodrama4android.ui.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.databinding.ActivityTestMusicSelectedBinding;
import com.loopeer.android.photodrama4android.media.model.Drama;
import com.loopeer.android.photodrama4android.media.model.MusicClip;

import java.io.IOException;

import static com.loopeer.android.photodrama4android.Navigator.REQUEST_CODE_DRAMA_SOUND_EFFECT_SELECT;


//TODO
public class TestMusicSelectedActivity extends MovieMakerBaseActivity {

    private static final String TAG = "TestMusicSelectedActivi";

    private Drama mDrama;
    private ActivityTestMusicSelectedBinding mBinding;
    private MediaPlayer mPlayer;
    private MusicClip mMusicClip;
    private int mRequestCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_test_music_selected);
        mDrama = (Drama) getIntent().getSerializableExtra(Navigator.EXTRA_DRAMA);

        mRequestCode = getIntent().getIntExtra(Navigator.EXTRA_REQUEST_CODE, REQUEST_CODE_DRAMA_SOUND_EFFECT_SELECT);
        //TODO
        mPlayer = new MediaPlayer();
    }

    public void onLoadClick(View view) {
        if (TextUtils.isEmpty(mBinding.editText.getText().toString())) return;
        try {
            mPlayer.reset();
            mPlayer.setDataSource(mBinding.editText.getText().toString());
            mPlayer.prepareAsync();
            mPlayer.setOnPreparedListener(mp -> {
                if (mRequestCode == REQUEST_CODE_DRAMA_SOUND_EFFECT_SELECT) {
                    mMusicClip = new MusicClip(0, MusicClip.MusicType.SOUND_EFFECT);
                    mMusicClip.path = mBinding.editText.getText().toString();
                    mMusicClip.musicStartOffset = 0;
                    mMusicClip.musicSelectedLength = mPlayer.getDuration();
                    mMusicClip.showTime = mMusicClip.musicSelectedLength;
                } else {
                    mMusicClip = new MusicClip(0, MusicClip.MusicType.BGM);
                    mMusicClip.path = mBinding.editText.getText().toString();
                    mMusicClip.musicStartOffset = 1000;
                    mMusicClip.musicSelectedLength = mPlayer.getDuration();
                    mMusicClip.showTime = mMusicClip.musicSelectedLength + 1000;
                }
                Log.e(TAG, mBinding.editText.getText().toString() +  " : " + mMusicClip.musicSelectedLength);
                mPlayer.start();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        if (item.getItemId() == R.id.menu_done) {
            Intent intent = new Intent();
            intent.putExtra(Navigator.EXTRA_MUSIC_CLIP, mMusicClip);
            setResult(RESULT_OK, intent);
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
