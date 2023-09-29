package com.example.todo.controller;

import com.example.todo.service.ActivatorService;

public class ActivatorController {

    private final ActivatorService activatorService;

    public ActivatorController(final ActivatorService activatorService) {
        this.activatorService = activatorService;
    }

    public void onClickMenu() {
        activatorService.startNavigationActivity();
    }

    public void onClickSetting() {
        activatorService.startSettingActivity();
    }
}
