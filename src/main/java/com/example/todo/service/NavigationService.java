package com.example.todo.service;

import com.example.todo.model.Project;

public interface NavigationService {

    void goToListPage(final Project project);

    void addProjectList();

    void toggleEditTextVisibility();
}
