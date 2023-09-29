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
import com.example.todo.model.Todo;
import com.example.todo.service.impl.TypeFaceUtil;

import java.util.Collections;
import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder> {

    private List<Todo> todoList;
    private OnItemClickListener onItemClickListener;

    public TodoAdapter(final List<Todo> todoList) {
        this.todoList = todoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_item_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
        final Todo todoItem = todoList.get(position);
        final Typeface typeface = TypeFaceUtil.getSelectedTypeface();
        final float fontSize = TypeFaceUtil.getSelectedTextSize();

        if (null != typeface) {
            viewHolder.textView.setTypeface(typeface);
        } else if (0 != fontSize){
            viewHolder.textView.setTextSize(fontSize);
        }

        viewHolder.bind(todoItem, onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public void setOnClickListener(final OnItemClickListener onClickListener) {
        this.onItemClickListener = onClickListener;
    }


    public void onItemMove(final int fromPosition, final int toPosition) {
        final Todo fromItem = todoList.get(fromPosition);
        final Todo toItem = todoList.get(toPosition);

        Collections.swap(todoList, fromPosition, toPosition);
        fromItem.setOrder((long) (toPosition + 1));
        toItem.setOrder((long) (fromPosition + 1));
        onItemClickListener.onItemOrderUpdateListener(fromItem, toItem);
        notifyItemMoved(fromPosition, toPosition);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateTodoItems(final List<Todo> updatedItems) {
        this.todoList = updatedItems;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clearProjects() {
        this.todoList.clear();
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addTodoItem(final List<Todo> newProjects) {
        this.todoList = newProjects;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView;
        private final CheckBox checkBox;
        private final ImageButton removeButton;

        public ViewHolder(final View view) {
            super(view);
            textView = view.findViewById(R.id.textView);
            checkBox = view.findViewById(R.id.checkBox);
            removeButton = view.findViewById(R.id.removeButton);
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                final int position = getAdapterPosition();

                if (position != RecyclerView.NO_POSITION) {
                    final Todo todoItem = todoList.get(position);

                    todoItem.setChecked();
                    todoItem.setStatus(isChecked ? Todo.Status.COMPLETED : Todo.Status.NOT_COMPLETED);
                    textView.setTextColor(todoItem.getStatus() == Todo.Status.COMPLETED ? Color.GRAY : Color.BLACK);
                    onItemClickListener.onCheckBoxClick(todoItem);
                }
            });
            removeButton.setOnClickListener(v -> {
                final int position = getAdapterPosition();

                if (position != RecyclerView.NO_POSITION) {
                    final Todo todoItem = todoList.get(position);

                    todoList.remove(todoItem);
                    notifyItemRemoved(position);
                    onItemClickListener.onCloseIconClick(todoItem);
                }
            });
        }

        public void bind(final Todo todoItem, final OnItemClickListener listener) {
            checkBox.setChecked(todoItem.getStatus() == Todo.Status.COMPLETED);
            textView.setText(todoItem.getName());
            textView.setTextColor(todoItem.getStatus() == Todo.Status.COMPLETED ? Color.GRAY : Color.BLACK);
            checkBox.setOnClickListener(v -> listener.onCheckBoxClick(todoItem));
            removeButton.setOnClickListener(v -> listener.onCloseIconClick(todoItem));
        }
    }
}
