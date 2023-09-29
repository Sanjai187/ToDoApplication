package com.example.todo.controller;

import android.widget.EditText;
import android.widget.ImageView;

import com.example.todo.service.SignUpService;

public class SignUpController {

    private final SignUpService service;

    public SignUpController(final SignUpService service) {
        this.service = service;
    }

    public void onClickPasswordVisibility(final EditText newPasswordEditText, final ImageView newPasswordVisibilityToggle) {
        service.togglePasswordVisibility(newPasswordEditText, newPasswordVisibilityToggle);
    }

    public void onClickCreateAccount() {
        service.createNewAccount();
    }
}
