package com.atul.musicplayer.activities.queue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.atul.musicplayer.adapter.QueueAdapter;

public class QueueItemCallback extends ItemTouchHelper.Callback {
    private final QueueItemInterface queueItemInterface;

    public QueueItemCallback(QueueItemInterface queueItemInterface) {
        this.queueItemInterface = queueItemInterface;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        queueItemInterface.onRowMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder instanceof QueueAdapter.MyViewHolder) {
            QueueAdapter.MyViewHolder myViewHolder = (QueueAdapter.MyViewHolder) viewHolder;
            queueItemInterface.onRowSelected(myViewHolder);
        }

        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        if (viewHolder instanceof QueueAdapter.MyViewHolder) {
            QueueAdapter.MyViewHolder myViewHolder = (QueueAdapter.MyViewHolder) viewHolder;
            queueItemInterface.onRowClear(myViewHolder);
        }
    }

    public interface QueueItemInterface {
        void onRowMoved(int fromPosition, int toPosition);

        void onRowSelected(QueueAdapter.MyViewHolder myViewHolder);

        void onRowClear(QueueAdapter.MyViewHolder myViewHolder);
    }
}