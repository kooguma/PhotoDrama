package com.loopeer.android.photodrama4android.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.fastui.uipattern.IPageRecycler;
import com.laputapp.http.BaseResponse;
import com.laputapp.ui.adapter.BaseFooterAdapter;
import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.model.Theme;
import com.loopeer.android.photodrama4android.ui.adapter.DramaSelectAdapter;
import com.loopeer.android.photodrama4android.ui.adapter.OnItemClickListener;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;

public class DramaSelectFragment extends MovieMakerBaseFragment
    implements IPageRecycler<Theme>, OnItemClickListener<Theme> {

    private DramaSelectAdapter mSelectAdapter;

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSelectAdapter = new DramaSelectAdapter(getContext());
        mSelectAdapter.setOnItemClickListener(this);
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_drama_select, container, false);
    }

    @Override public int getExtraItemCount() {
        return 0;
    }

    @Override public BaseFooterAdapter<Theme> createRecyclerViewAdapter() {
        if (mSelectAdapter == null) {
            mSelectAdapter = new DramaSelectAdapter(getContext());
            mSelectAdapter.setOnItemClickListener(this);
        }
        return mSelectAdapter;
    }

    @Override
    public Observable<BaseResponse<List<Theme>>> requestData(String page, String pageSize) {
        List<Theme> dramas = new ArrayList<>();
        Theme drama = new Theme();
        drama.image
            = "http://i2.hdslb.com/bfs/archive/d88114babdbe68083378a896fece513d07fb7a46.jpg";
        dramas.add(drama);
        dramas.add(drama);
        dramas.add(drama);
        dramas.add(drama);
        dramas.add(drama);
        dramas.add(drama);
        getRecyclerManager().onCacheLoaded(dramas);
        return null;
    }

    @Override public void onItemClick(Theme drama) {
        Navigator.startDramaDetailActivity(getContext());
    }
}
