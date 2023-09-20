package com.example.todo.dao;

import com.example.todo.model.Project;

import java.util.List;

public interface ProjectDao {

    Long insert(final Project project);
    long onDelete(final Project project);
    void updateProjectsOrder(final Project project);
    List<Project> getAllProjects();
    void open();
    void close();
}
