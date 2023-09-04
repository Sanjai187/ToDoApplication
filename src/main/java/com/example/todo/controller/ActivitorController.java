package com.example.todo.controller;

import com.example.todo.Activitor;
import com.example.todo.service.ActivatorService;

public class ActivitorController {

    private final Activitor activitor;
    private final ActivatorService activatorService;

    public ActivitorController(final Activitor activitor, final ActivatorService activatorService) {
        this.activitor = activitor;
        this.activatorService = activatorService;
    }

    public void onClickMenu(final String selectedList) {
        activatorService.goToNavigation(selectedList);
    }
}
