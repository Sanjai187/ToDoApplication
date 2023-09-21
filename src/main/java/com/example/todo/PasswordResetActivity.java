package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.todo.api.AuthenticationService;
import com.example.todo.model.Credential;
import com.google.android.material.snackbar.Snackbar;

public class PasswordResetActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText newHintEditText;
    private EditText oldHintEditText;
    private EditText newPasswordEditText;
    private EditText confirmPasswordEditText;
    private boolean isPasswordVisible;
    private ImageView newPasswordVisibilityToggle;
    private ImageView confirmPasswordVisibilityToggle;
    private AuthenticationService authService;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);

        initializeViews();
        setClickListeners();
    }

    private void initializeViews() {
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

        cancel.setOnClickListener(view -> navigateToSignIn());
        newPasswordVisibilityToggle.setOnClickListener(view -> togglePasswordVisibility(newPasswordEditText, newPasswordVisibilityToggle));
        confirmPasswordVisibilityToggle.setOnClickListener(view -> togglePasswordVisibility(confirmPasswordEditText, confirmPasswordVisibilityToggle));
        resetPassword.setOnClickListener(view -> attemptPasswordReset());
    }

    private void navigateToSignIn() {
        final Intent intent = new Intent(PasswordResetActivity.this, SignInActivity.class);

        startActivity(intent);
    }

    private void togglePasswordVisibility(final EditText passwordField, final ImageView visibilityToggle) {
        if (isPasswordVisible) {
            passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            isPasswordVisible = false;
            visibilityToggle.setImageResource(R.drawable.visibility);
        } else {
            passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            isPasswordVisible = true;
            visibilityToggle.setImageResource(R.drawable.visibility_off);
        }
        passwordField.setSelection(passwordField.getText().length());
    }

    private void attemptPasswordReset() {
        final Credential credential = createCredential();
        if (credential != null) {
            authService = new AuthenticationService("http://192.168.1.3:8080/");
            authService.resetPassword(credential, newHintEditText.getText().toString().trim(),
                    new AuthenticationService.ApiResponseCallBack() {
                        @Override
                        public void onSuccess(final String response) {
                            showSnackbar(getString(R.string.password_updated));
                        }

                        @Override
                        public void onError(final String errorMessage) {
                            showSnackbar(String.format(getString(R.string.request_failed_s), errorMessage));
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
            showSnackbar(getString(R.string.fields_fill));
            return null;
        } else if (!newPassword.equals(confirmPasswordEditText.getText().toString().trim())) {
            showSnackbar(getString(R.string.password_mismatch));
            return null;
        }

        final Credential credential = new Credential();
        credential.setEmail(email);
        credential.setPassword(newPassword);
        credential.setHint(oldHint);
        return credential;
    }

    private void showSnackbar(final String message) {
        final View parentLayout = findViewById(android.R.id.content);
        final int backgroundColor = Color.argb(200, 255, 255, 255);
        final Snackbar snackbar = Snackbar.make(parentLayout, message, Snackbar.LENGTH_LONG);
        snackbar.setTextColor(getResources().getColor(R.color.black));
        snackbar.setBackgroundTint(backgroundColor);
        snackbar.show();
    }
}
