package com.example.todo.controller;

import com.example.todo.Activator;
import com.example.todo.service.ActivatorService;

public class ActivatorController {

    private final Activator activator;
    private final ActivatorService activatorService;

    public ActivatorController(final Activator activator, final ActivatorService activatorService) {
        this.activator = activator;
        this.activatorService = activatorService;
    }

    public void onClickMenu(final String selectedList) {
        activatorService.goToNavigation(selectedList);
    }

    public void onClickSetting() {
        activatorService.navigateToSettings();
    }
}
