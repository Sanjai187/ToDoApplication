package com.example.todo.controller;

import com.example.todo.model.Project;
import com.example.todo.service.NavigationService;

public class NavigationController {

    private final NavigationService navigationService;

    public NavigationController(final NavigationService navigationService) {
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
