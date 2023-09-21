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
import com.example.todo.model.Todo;

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
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.todo_item_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Todo todoItem = todoList.get(position);
        final Typeface typeface = TypeFaceUtil.getSelectedTypeFace();
        final float fontSize = TypeFaceUtil.getSelectedFontSize();

        if (null != typeface) {
            holder.todoTextView.setTypeface(typeface);
        } else if (0 != fontSize){
            holder.todoTextView.setTextSize(fontSize);
        }

        holder.bind(todoItem, onItemClickListener);
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
    public void updateTodoItems(List<Todo> updatedItems) {
        this.todoList = updatedItems;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clearProjects() {
        this.todoList.clear();
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addProjects(final List<Todo> newProjects) {
        todoList.clear();
        todoList.addAll(newProjects);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView todoTextView;
        private final CheckBox checkBox;
        private final ImageButton todoRemoveButton;

        public ViewHolder(final View itemView) {
            super(itemView);
            todoTextView = itemView.findViewById(R.id.todoTextView);
            checkBox = itemView.findViewById(R.id.todoCheckBox);
            todoRemoveButton = itemView.findViewById(R.id.todoRemoveButton);
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                final int position = getAdapterPosition();

                if (position != RecyclerView.NO_POSITION) {
                    final Todo todoItem = todoList.get(position);

                    todoItem.setChecked();
                    todoItem.setStatus(isChecked ? Todo.Status.COMPLETED
                            : Todo.Status.NOT_COMPLETED);
                    todoTextView.setTextColor(todoItem.getStatus() == Todo.Status.COMPLETED
                            ? Color.GRAY : Color.BLACK);
                    onItemClickListener.onCheckBoxClick(todoItem);
                }
            });
            todoRemoveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        final Todo todoItem = todoList.get(position);

                        todoList.remove(todoItem);
                        notifyItemRemoved(position);
                        onItemClickListener.onCloseIconClick(todoItem);
                    }
                }
            });
        }

        public void bind(final Todo todoItem, final OnItemClickListener listener) {
            checkBox.setChecked(todoItem.getStatus() == Todo.Status.COMPLETED);
            todoTextView.setText(todoItem.getName());
            todoTextView.setTextColor(todoItem.getStatus() == Todo.Status.COMPLETED ? Color.RED : Color.BLACK);
            checkBox.setOnClickListener(v -> listener.onCheckBoxClick(todoItem));
            todoRemoveButton.setOnClickListener(v -> listener.onCloseIconClick(todoItem));
        }
    }
}
