package com.example.todo.todoadapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemTouchHelperCallBack extends androidx.recyclerview.widget.ItemTouchHelper.Callback {

    private final ItemTouchHelper adapter;

    public ItemTouchHelperCallBack(final ItemTouchHelper adapter) {
        this.adapter = adapter;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        final int dragFlags = androidx.recyclerview.widget.ItemTouchHelper.UP | androidx.recyclerview.widget.ItemTouchHelper.DOWN;

        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public boolean onMove(@NonNull final RecyclerView recyclerView,
                          @NonNull final RecyclerView.ViewHolder source,
                          @NonNull final RecyclerView.ViewHolder target) {
        adapter.onItemMove(source.getAdapterPosition(), target.getAdapterPosition());

        return true;
    }

    @Override
    public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, final int direction) {}

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }
}
