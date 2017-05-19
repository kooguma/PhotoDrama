package com.loopeer.android.photodrama4android.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.fastui.uipattern.IPageRecycler;
import com.laputapp.http.BaseResponse;
import com.laputapp.ui.adapter.BaseFooterAdapter;
import com.laputapp.ui.adapter.RxRecyclerAdapter;
import com.laputapp.ui.decorator.DividerItemDecoration;
import com.laputapp.utilities.DeviceScreenUtils;
import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.media.model.MusicClip;
import com.loopeer.android.photodrama4android.model.Voice;
import com.loopeer.android.photodrama4android.ui.hepler.ItemTouchHelperCallback;
import com.loopeer.android.photodrama4android.ui.adapter.BGMDownloadAdapter;
import com.loopeer.android.photodrama4android.ui.adapter.EffectDownloadAdapter;
import com.loopeer.android.photodrama4android.ui.hepler.MediaPlayerWrapper;
import com.loopeer.android.photodrama4android.ui.widget.MusicClipView;
import com.loopeer.android.photodrama4android.utils.FileManager;
import com.loopeer.itemtouchhelperextension.ItemTouchHelperExtension;

import io.reactivex.Flowable;
import java.io.File;
import java.util.HashMap;
import java.util.List;

public class MyDownloadMusicFragment extends MovieMakerBaseFragment
    implements IPageRecycler<Voice>,
    BGMDownloadAdapter.IMusicAdapter {

    private MediaPlayerWrapper mPlayerWrapper;

    private MusicClip.MusicType mType;

    public static MyDownloadMusicFragment newInstance(MusicClip.MusicType type) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Navigator.EXTRA_MUSIC_CLIP, type);
        MyDownloadMusicFragment fragment = new MyDownloadMusicFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        mType = (MusicClip.MusicType) getArguments().getSerializable(Navigator.EXTRA_MUSIC_CLIP);
        mPlayerWrapper = new MediaPlayerWrapper(getContext());
        super.onCreate(savedInstanceState);
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_simple_list, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getRecyclerManager().getRecyclerView()
            .addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL_LIST, 0,
                DeviceScreenUtils.dp2px(0.5f, getContext())));
        ItemTouchHelperCallback callback = new ItemTouchHelperCallback();
        ItemTouchHelperExtension itemTouchHelperExtension = new ItemTouchHelperExtension(callback);
        itemTouchHelperExtension.attachToRecyclerView(getRecyclerManager().getRecyclerView());
        //        mAdapter.setItemTouchHelperExtension(mItemTouchHelper);
    }

    @Override public int getExtraItemCount() {
        return 0;
    }

    @Override public RxRecyclerAdapter<Voice> createRecyclerViewAdapter() {
        if (mType == MusicClip.MusicType.BGM) {
            BGMDownloadAdapter adapter = new BGMDownloadAdapter(getContext());
            adapter.setIMusicAdapter(this);
            return adapter;
        } else {
            EffectDownloadAdapter adapter = new EffectDownloadAdapter(getContext());
            return adapter;
        }
    }

    @Override
    public Flowable<? extends BaseResponse<List<Voice>>> requestData(String offset, String page, String pageSize) {
        getRecyclerManager().onCacheLoaded(FileManager.getInstance().getAudioFiles());
        return null;
    }

    @Override public void onMusicAddClick(Voice voice) {
        MusicClip clip = mPlayerWrapper.generateMusicClip(voice, mType);
    }

    @Override
    public void onControllerVisibilityChange(TextView txtStart, TextView txtCur, TextView txtEnd) {
        mPlayerWrapper.updateController(txtStart, txtCur, txtEnd);
    }

    @Override public void onMusicPlayClick(String path, MusicClipView musicClipView) {
        if (mPlayerWrapper.isAlreadyPrepared(new File(path))) {
            mPlayerWrapper.start();
        } else {
            if (mPlayerWrapper.isPlaying()) {
                mPlayerWrapper.reset();
            }
            mPlayerWrapper.updateMusicClipView(musicClipView);
            mPlayerWrapper.updateDataSource(new File(path));
            mPlayerWrapper.startAsync();
        }
    }

    @Override public void onMusicPauseClick(String path, MusicClipView musicClipView) {
        if (mPlayerWrapper.isPlaying()) {
            mPlayerWrapper.updateMusicClipView(musicClipView);
            mPlayerWrapper.pause();
        }
    }

    @Override public void onPause() {
        super.onPause();
        //mPlayerWrapper.pause();
    }

    @Override public void onDestroy() {
        super.onDestroy();
        //mPlayerWrapper.destroy();
    }

}
