package com.loopeer.android.photodrama4android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.laputapp.ui.adapter.BaseFooterAdapter;
import com.loopeer.android.photodrama4android.Navigator;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.databinding.ListItemDramaSelectBinding;
import com.loopeer.android.photodrama4android.model.Theme;
import com.loopeer.android.photodrama4android.ui.viewholder.DataBindingViewHolder;

public class DramaSelectAdapter<T extends Theme> extends BaseFooterAdapter<T> {

    private OnItemClickListener<Theme> mOnItemClickListener;

    public DramaSelectAdapter(Context context) {
        super(context);
    }

    @Override public void bindItem(Theme theme, int var2, RecyclerView.ViewHolder holder) {
        ListItemDramaSelectBinding binding
            = ((DataBindingViewHolder<ListItemDramaSelectBinding>) holder).binding;

        binding.setTheme(theme);
        binding.imageDrama.setOnClickListener(v -> {
//            Navigator.startDramaDetailActivity(getContext(),theme);

        });
        binding.layoutBottom.setOnClickListener(v -> {
            Navigator.startDramaDetailActivity(getContext(),theme);
        });
        binding.btnUseDrama.setOnClickListener(l ->{
            Navigator.startDramaEditActivity(getContext(),theme);
        });
        binding.executePendingBindings();
    }

    @Override public RecyclerView.ViewHolder createItemHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.list_item_drama_select,parent,false);
        return new DataBindingViewHolder<>(v);
    }

}
