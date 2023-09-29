package com.example.todo.controller;

import android.widget.EditText;
import android.widget.ImageView;

import com.example.todo.service.PasswordResetService;

public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(final PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    public void onClickPasswordVisibility(final EditText newPasswordEditText, final ImageView newPasswordVisibilityToggle) {
        passwordResetService.togglePasswordVisibility(newPasswordEditText, newPasswordVisibilityToggle);
    }

    public void onClickPasswordReset() {
        passwordResetService.attemptPasswordReset();
    }
}
