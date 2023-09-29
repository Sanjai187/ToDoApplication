package com.example.todo.controller;

import com.example.todo.service.SignInService;

public class SignInController {

    private final SignInService service;

    public SignInController(final SignInService service) {
        this.service = service;
    }

    public void onClickPasswordVisibility() {
        service.togglePasswordVisibility();
    }

    public void onClickSignIn() {
        service.signIn();
    }
}
