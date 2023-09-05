package com.example.todo.dao;

import com.example.todo.model.Project;

public interface ProjectDao {

    Long insert(final Project project);

    long onDelete(final Long id);

    void open();

    void close();
}
