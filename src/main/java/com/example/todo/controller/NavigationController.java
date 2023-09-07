package com.example.todo.controller;

import com.example.todo.NavigationActivity;
import com.example.todo.model.Project;
import com.example.todo.model.ProjectList;
import com.example.todo.service.NavigationService;

public class NavigationController {

    private final ProjectList projectList;
    private final NavigationActivity activitor;
    private final NavigationService navigationService;

    public NavigationController(final NavigationActivity activitor, final NavigationService navigationService, final ProjectList projectList) {
        this.activitor = activitor;
        this.projectList = projectList;
        this.navigationService = navigationService;
    }

    public void onListItemClicked(final Project project) {
        navigationService.goToListPage(project);
    }

    public void onAddProject() {
        navigationService.addProjectList();
    }


    public void onClickTextVisibility() {
        navigationService.toggleEditTextVisibility();
    }
}
