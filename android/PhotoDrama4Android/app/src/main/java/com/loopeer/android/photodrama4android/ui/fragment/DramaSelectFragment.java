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
import com.loopeer.android.photodrama4android.api.service.SeriesService;
import com.loopeer.android.photodrama4android.api.service.ThemeService;
import com.loopeer.android.photodrama4android.model.Advert;
import com.loopeer.android.photodrama4android.model.Series;
import com.loopeer.android.photodrama4android.model.Theme;
import com.loopeer.android.photodrama4android.ui.adapter.DramaSelectAdapter;
import com.loopeer.android.photodrama4android.ui.adapter.OnItemClickListener;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.Flowable;

public class DramaSelectFragment extends MovieMakerBaseFragment
    implements IPageRecycler<Series> {

    private static final String KEY_CATEGORY_ID = "CATEGORY_ID";
    private static final String KEY_ADVERTS = "ADVERTS";

    private DramaSelectAdapter mSelectAdapter;
    private String mCategoryId;
    private List<Advert> mAdverts;

    public static DramaSelectFragment newDramaSelectFragment(String categoryId, ArrayList<Advert> adverts) {
        DramaSelectFragment fragment = new DramaSelectFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_CATEGORY_ID, categoryId);
        bundle.putParcelableArrayList(KEY_ADVERTS, adverts);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        mCategoryId = getArguments().getString(KEY_CATEGORY_ID);
        mAdverts = getArguments().getParcelableArrayList(KEY_ADVERTS);
        mSelectAdapter = new DramaSelectAdapter(getContext());
        mSelectAdapter.setAdverts(mAdverts);
        super.onCreate(savedInstanceState);
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_drama_select, container, false);
    }

    @Override public int getExtraItemCount() {
        return 0;
    }

    @Override public BaseFooterAdapter<Series> createRecyclerViewAdapter() {
        if (mSelectAdapter == null) {
            mSelectAdapter = new DramaSelectAdapter(getContext());
        }
        return mSelectAdapter;
    }

    @Override
    public Flowable<? extends BaseResponse<List<Series>>> requestData(String offset, String page, String pageSize) {
        return SeriesService.INSTANCE.list(mCategoryId, page, pageSize);
    }
}
