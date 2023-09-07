package com.example.todo.todoadapter;

import com.example.todo.model.Todo;

public interface OnItemClickListener {

    void onCheckBoxClick(final Todo todoItem);
    void onCloseIconClick(final Todo todoItem);
}
