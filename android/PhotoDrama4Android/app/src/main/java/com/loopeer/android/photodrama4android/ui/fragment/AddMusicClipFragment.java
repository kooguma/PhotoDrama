package com.loopeer.android.photodrama4android.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.fastui.uipattern.IPageRecycler;
import com.laputapp.http.BaseResponse;
import com.laputapp.ui.adapter.RxRecyclerAdapter;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.api.service.VoiceService;
import com.loopeer.android.photodrama4android.model.Voice;
import com.loopeer.android.photodrama4android.ui.adapter.MusicClipAddAdapter;
import com.loopeer.android.photodrama4android.ui.widget.MusicClipView;
import io.reactivex.Flowable;
import java.util.List;

public class AddMusicClipFragment extends MovieMakerBaseFragment
    implements IPageRecycler<Voice>, MusicClipAddAdapter.IMusicAdapter {

    private MusicClipAddAdapter mAdapter;

    public static AddMusicClipFragment newInstance() {
        return new AddMusicClipFragment();
    }


    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_music_clip, container, false);
    }

    @Override public int getExtraItemCount() {
        return 0;
    }

    @Override public RxRecyclerAdapter<Voice> createRecyclerViewAdapter() {
        MusicClipAddAdapter adapter = new MusicClipAddAdapter(getContext());
        adapter.setIMusicAdapter(this);
        return adapter;
    }

    @Override
    public Flowable<? extends BaseResponse<List<Voice>>> requestData(String page, String pageSize) {
        return VoiceService.INSTANCE.voices("1");
    }

    @Override public void onAddAudioClick(Voice voice) {

    }

    @Override public void onDownloadClick(Voice voice, View v) {

    }

    @Override public void onAudioPlayClick(String path, MusicClipView musicClipView) {

    }

    @Override public void onAudioPauseClick(String path, MusicClipView musicClipView) {

    }
}
