package com.example.todo.dao;

import com.example.todo.model.Todo;

import java.util.List;

public interface ItemDao {

    Long insert(final Todo todo);
    long onDelete(final Long id);
    void onUpdateStatus(final Todo todo);
    List<Todo> getTodoItems(final Long projectId);
    void updateTodoItemOrder(final Todo todo);
    void open();
    void close();
}
