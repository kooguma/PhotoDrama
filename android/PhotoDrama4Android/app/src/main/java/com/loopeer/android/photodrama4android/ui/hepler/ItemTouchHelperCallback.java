
package com.loopeer.android.photodrama4android.ui.hepler;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import android.view.View;
import android.widget.LinearLayout;
import com.loopeer.android.photodrama4android.R;
import com.loopeer.android.photodrama4android.ui.adapter.BGMDownloadAdapter;
import com.loopeer.itemtouchhelperextension.ItemTouchHelperExtension;

public class ItemTouchHelperCallback extends ItemTouchHelperExtension.Callback {

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        BGMDownloadAdapter.MusicItemViewHolder holder
                = (BGMDownloadAdapter.MusicItemViewHolder) viewHolder;
        if (holder.itemView.findViewById(R.id.layout_controller).getVisibility() == View.VISIBLE) {
            return makeMovementFlags(0, 0);
        }
        return makeMovementFlags(0, ItemTouchHelper.START);

    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder
            , float dX, float dY, int actionState, boolean isCurrentlyActive) {
        BGMDownloadAdapter.MusicItemViewHolder holder
                = (BGMDownloadAdapter.MusicItemViewHolder) viewHolder;
        if (holder.itemView.findViewById(R.id.layout_controller).getVisibility() == View.VISIBLE) {
            return;
        }
        if (dY != 0 && dX == 0) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
        if (viewHolder instanceof BGMDownloadAdapter.MusicItemViewHolder) {
            holder.getContentView().setTranslationX(dX);
        }
    }
}
