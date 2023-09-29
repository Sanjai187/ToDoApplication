package com.example.todo.service.impl;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todo.R;
import com.example.todo.api.impl.AuthenticationService;
import com.example.todo.controller.PasswordResetController;
import com.example.todo.model.Credential;
import com.example.todo.service.PasswordResetService;
import com.google.android.material.snackbar.Snackbar;

public class PasswordResetActivity extends AppCompatActivity implements PasswordResetService {

    private EditText emailEditText;
    private EditText newHintEditText;
    private EditText oldHintEditText;
    private EditText newPasswordEditText;
    private EditText confirmPasswordEditText;
    private boolean isPasswordVisible;
    private ImageView newPasswordVisibilityToggle;
    private ImageView confirmPasswordVisibilityToggle;
    private PasswordResetController passwordResetController;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);

        initializeViews();
        setClickListeners();
    }

    private void initializeViews() {
        passwordResetController = new PasswordResetController((PasswordResetService) this);
        emailEditText = findViewById(R.id.emailEdit);
        newHintEditText = findViewById(R.id.newHint);
        oldHintEditText = findViewById(R.id.oldHint);
        newPasswordEditText = findViewById(R.id.newPassword);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEdit);
        newPasswordVisibilityToggle = findViewById(R.id.newPasswordToggle);
        confirmPasswordVisibilityToggle = findViewById(R.id.confirmPasswordToggle);
    }

    private void setClickListeners() {
        final Button cancel = findViewById(R.id.cancelToCreatePassword);
        final Button resetPassword = findViewById(R.id.resetPassword);

        cancel.setOnClickListener(view -> onBackPressed());
        newPasswordVisibilityToggle.setOnClickListener(view -> passwordResetController.onClickPasswordVisibility(newPasswordEditText, newPasswordVisibilityToggle));
        confirmPasswordVisibilityToggle.setOnClickListener(view -> passwordResetController.onClickPasswordVisibility(confirmPasswordEditText, confirmPasswordVisibilityToggle));
        resetPassword.setOnClickListener(view -> passwordResetController.onClickPasswordReset());
    }

    public void togglePasswordVisibility(final EditText editText, final ImageView visibility) {
        if (isPasswordVisible) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            isPasswordVisible = false;
            visibility.setImageResource(R.drawable.visibility);
        } else {
            editText.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
            isPasswordVisible = true;
            visibility.setImageResource(R.drawable.visibility_off);
        }
        editText.setSelection(editText.getText().length());
    }

    public void attemptPasswordReset() {
        final Credential credential = createCredential();
        if (credential != null) {
            final AuthenticationService authService = new AuthenticationService(getString(R.string.base_url));
            authService.resetPassword(credential, newHintEditText.getText().toString().trim(),
                    new AuthenticationService.ApiResponseCallBack() {
                        @Override
                        public void onSuccess(final String response) {
                            showSnackBar(getString(R.string.password_updated));
                        }

                        @Override
                        public void onError(final String errorMessage) {
                            showSnackBar(String.format(getString(R.string.request_failed_s), errorMessage));
                        }
                    });
            finish();
        }
    }

    private Credential createCredential() {
        final String email = emailEditText.getText().toString().trim();
        final String newPassword = newPasswordEditText.getText().toString().trim();
        final String oldHint = oldHintEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(oldHint)
                || TextUtils.isEmpty(newHintEditText.getText().toString().trim())) {
            showSnackBar(getString(R.string.fields_fill));
            return null;
        } else if (!newPassword.equals(confirmPasswordEditText.getText().toString().trim())) {
            showSnackBar(getString(R.string.password_mismatch));
            return null;
        }

        final Credential credential = new Credential();
        credential.setEmail(email);
        credential.setPassword(newPassword);
        credential.setHint(oldHint);
        return credential;
    }

    private void showSnackBar(final String message) {
        final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT);

        snackbar.show();
    }
}
