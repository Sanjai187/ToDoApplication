package com.example.todo.dao;

import com.example.todo.model.Todo;

public interface ItemDao {

    Long insert(final Todo todo);

    long onDelete(final Long id);

    void onUpdate(final Todo todo);

    void open();

    void close();
}
