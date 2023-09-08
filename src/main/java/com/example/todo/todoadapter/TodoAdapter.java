package com.example.todo.todoadapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.R;
import com.example.todo.TypeFaceUtil;
import com.example.todo.dao.ItemDao;
import com.example.todo.model.Todo;

import java.util.Collections;
import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder>
        implements ItemTouchHelper {

    private List<Todo> todo;
    private ItemDao itemDao;
    private OnItemClickListener listener;

    public TodoAdapter(final List<Todo> todo, final ItemDao itemDao) {
        this.todo = todo;
        this.itemDao = itemDao;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.todo_item_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Todo todoItem = todo.get(position);
        final Typeface typeface = TypeFaceUtil.getSelectedTypeFace();
        final float fontSize = TypeFaceUtil.getSelectedFontSize();

        if (null != typeface) {
            holder.todoTextView.setTypeface(typeface);
        } else if (0 != fontSize){
            holder.todoTextView.setTextSize(fontSize);
        }

        holder.bind(todoItem, listener);
    }

    @Override
    public int getItemCount() {
        return todo.size();
    }

    public void setOnClickListener(final OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onItemMove(final int fromPosition, final int toPosition) {
        final Todo fromList = todo.get(fromPosition);
        final Todo toList = todo.get(toPosition);

        Collections.swap(todo, fromPosition, toPosition);
        fromList.setOrder((long) (toPosition + 1));
        toList.setOrder((long) (fromPosition + 1));
        notifyItemMoved(fromPosition, toPosition);
        itemDao.updateTodoItemOrder(fromList);
        itemDao.updateTodoItemOrder(toList);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView todoTextView;
        private final CheckBox checkBox;
        private final ImageButton todoRemoveButton;

        public ViewHolder(final View itemView) {
            super(itemView);
            todoTextView = itemView.findViewById(R.id.todoTextView);
            checkBox = itemView.findViewById(R.id.todoCheckBox);
            todoRemoveButton = itemView.findViewById(R.id.todoRemoveButton);
        }

        public void bind(final Todo todoItem, final OnItemClickListener listener) {
            checkBox.setChecked(todoItem.getStatus() == Todo.Status.COMPLETED);
            todoTextView.setText(todoItem.getLabel());
            todoTextView.setTextColor(todoItem.getStatus() == Todo.Status.COMPLETED ? Color.RED : Color.BLACK);
            checkBox.setOnClickListener(v -> listener.onCheckBoxClick(todoItem));
            todoRemoveButton.setOnClickListener(v -> listener.onCloseIconClick(todoItem));
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateTodoItems(List<Todo> updatedItems) {
        this.todo = updatedItems;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clearProjects() {
        this.todo.clear();
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addProjects(final List<Todo> newProjects) {
        this.todo.addAll(newProjects);
        notifyDataSetChanged();
    }
}
