package com.example.todo.service;

import android.widget.EditText;
import android.widget.ImageView;

public interface SignUpService {

    void togglePasswordVisibility(final EditText newPasswordEditText, final ImageView newPasswordVisibilityToggle);

    void createNewAccount();
}
