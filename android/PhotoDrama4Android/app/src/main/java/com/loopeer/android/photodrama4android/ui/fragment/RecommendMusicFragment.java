package com.loopeer.android.photodrama4android.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.dynamic.refresher.RefreshHelper;
import com.fastui.uipattern.IPageRecycler;
import com.laputapp.http.BaseResponse;
import com.laputapp.http.CacheResponse;
import com.laputapp.ui.adapter.RxRecyclerAdapter;
import com.laputapp.ui.decorator.DividerItemDecoration;
import com.laputapp.utilities.DeviceScreenUtils;
import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.api.service.CategoryService;
import com.loopeer.android.photodrama4android.media.model.MusicClip;
import com.loopeer.android.photodrama4android.model.Category;
import com.loopeer.android.photodrama4android.model.Voice;
import com.loopeer.android.photodrama4android.ui.adapter.MusicRecommendAdapter;
import io.reactivex.Flowable;
import java.util.List;

public class RecommendMusicFragment extends MovieMakerBaseFragment
    implements IPageRecycler<Category> {

    private MusicClip.MusicType mType;

    public static RecommendMusicFragment newInstance(MusicClip.MusicType type) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Navigator.EXTRA_MUSIC_CLIP, type);
        RecommendMusicFragment fragment = new RecommendMusicFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        mType = (MusicClip.MusicType) getArguments().getSerializable(Navigator.EXTRA_MUSIC_CLIP);
        super.onCreate(savedInstanceState);
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_simple_list, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getRecyclerManager().getRecyclerView()
            .addItemDecoration(
                new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST,
                    DeviceScreenUtils.dp2px(16, getContext()), 0,
                    DeviceScreenUtils.dp2px(0.5f, getContext())));
    }

    @Override public int getExtraItemCount() {
        return 0;
    }

    @Override public RxRecyclerAdapter<Category> createRecyclerViewAdapter() {
        return new MusicRecommendAdapter(getContext(), mType);
    }

    @Override
    public Flowable<? extends CacheResponse<List<Category>>> requestData(String offset, String page, String pageSize) {
        final String type = mType == MusicClip.MusicType.BGM
                            ? CategoryService.TYPE_SOUND_BGM
                            : CategoryService.TYPE_SOUND_EFFECT;
        return CategoryService.INSTANCE.categories(type);
    }

}
