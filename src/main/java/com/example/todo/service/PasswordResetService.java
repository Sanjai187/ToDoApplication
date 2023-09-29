package com.example.todo.service;

import android.widget.EditText;
import android.widget.ImageView;

public interface PasswordResetService {

    void togglePasswordVisibility(final EditText newPasswordEditText, final ImageView newPasswordVisibilityToggle);

    void attemptPasswordReset();
}
