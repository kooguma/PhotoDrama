package com.loopeer.android.photodrama4android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.laputapp.model.BaseModel;
import com.laputapp.ui.adapter.BaseFooterAdapter;
import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.analytics.Analyst;
import com.loopeer.android.photodrama4android.databinding.ListItemDramaSelectAdBinding;
import com.loopeer.android.photodrama4android.databinding.ListItemDramaSelectBinding;
import com.loopeer.android.photodrama4android.model.Advert;
import com.loopeer.android.photodrama4android.model.Series;
import com.loopeer.android.photodrama4android.ui.viewholder.DataBindingViewHolder;
import java.util.ArrayList;
import java.util.List;
import com.loopeer.android.photodrama4android.utils.FileManager;

import static com.loopeer.android.photodrama4android.utils.Toaster.showToast;

public class DramaSelectAdapter<T extends BaseModel> extends BaseFooterAdapter<BaseModel> {

    private static final int ADVERT_INDEX = 2;

    private List<Advert> mAdverts = new ArrayList<>();

    public DramaSelectAdapter(Context context) {
        super(context);
    }

    @Override public void setData(List<BaseModel> data) {
        if (mAdverts.size() != 0) {
            for (int i = 0, j = 0; i < data.size(); i++) {
                if (i % 10 == ADVERT_INDEX) {
                    data.add(i + 1, mAdverts.get(j));
                }
            }
        }
        super.setData(data);
    }

    public void setAdverts(List<Advert> adverts) {
        mAdverts.clear();
        mAdverts.addAll(adverts);
    }

    @Override public int getItemViewType(int position) {
        BaseModel model = getDatas().get(position);
        if (model instanceof Series) {
            return R.layout.list_item_drama_select;
        } else if (model instanceof Advert) {
            return R.layout.list_item_drama_select_ad;
        } else {
            return super.getItemViewType(position);
        }
    }

    @Override
    public void bindItem(BaseModel model, int var2, RecyclerView.ViewHolder holder) {
        if (model instanceof Series) {
            Series series = (Series) model;
            ListItemDramaSelectBinding binding
                = ((DataBindingViewHolder<ListItemDramaSelectBinding>) holder).binding;
            binding.setSeries(series);
            binding.container.setOnClickListener(
                v -> {
                    if (FileManager.hasExternalStoragePermission(getContext())) {
                        Navigator.startDramaDetailActivity(getContext(), series);
                    } else {
                        showToast(R.string.common_storage_permission_fail);
                    }
                }
            );
            binding.btnUseDrama.setOnClickListener(l -> {
                Analyst.dramaDetailClick(series.id);
                if (FileManager.hasExternalStoragePermission(getContext())) {
                    Analyst.dramaUseClick(series.id);
                    Navigator.startDramaDetailActivity(getContext(), series);
                } else {
                    showToast(R.string.common_storage_permission_fail);
                }

            });
            binding.executePendingBindings();
        }

        if (model instanceof Advert) {
            Advert advert = (Advert) model;
            ListItemDramaSelectAdBinding binding
                = ((DataBindingViewHolder<ListItemDramaSelectAdBinding>) holder).binding;
            binding.container.setOnClickListener(l -> {
                if (advert.relType == Advert.REL_TYPE_URL) {
                    Analyst.myStartListADClick(advert.id);
                    Navigator.startWebActivity(getContext(), advert.relValue,
                        R.string.label_detail);
                } else if (advert.relType == Advert.REL_TYPE_SERIES) {
                    Navigator.startDramaDetailActivity(getContext(), advert.relValue);
                }
            });
            binding.setAdvert(advert);
            binding.executePendingBindings();
        }
    }

    @Override
    public RecyclerView.ViewHolder createItemHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(getContext()).inflate(viewType, parent, false);
        return new DataBindingViewHolder<>(v);
    }

}
