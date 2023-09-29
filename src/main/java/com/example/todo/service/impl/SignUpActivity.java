package com.example.todo.service.impl;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todo.R;
import com.example.todo.api.impl.AuthenticationService;
import com.example.todo.controller.SignUpController;
import com.example.todo.model.Credential;
import com.example.todo.model.UserProfile;
import com.example.todo.service.SignUpService;
import com.google.android.material.snackbar.Snackbar;

public class SignUpActivity extends AppCompatActivity implements SignUpService{

    private EditText userName;
    private EditText userEmail;
    private EditText userTitle;
    private EditText hint;
    private EditText userPassword;
    private EditText confirmPassword;
    private boolean isPasswordVisible;
    private ImageView passwordVisibility;
    private ImageView confirmPasswordVisibility;
    private SignUpController signUpController;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        userName = findViewById(R.id.signUpName);
        userEmail = findViewById(R.id.signUpEmail);
        userTitle = findViewById(R.id.usertitle);
        hint = findViewById(R.id.hint);
        userPassword = findViewById(R.id.signUpPassword);
        confirmPassword = findViewById(R.id.signUpConfirmPassword);
        passwordVisibility = findViewById(R.id.passwordVisibility);
        confirmPasswordVisibility = findViewById(R.id.confirmPasswordVisibility);
        signUpController = new SignUpController((SignUpService) this);
        final Button createAccount = findViewById(R.id.createAccount);
        final TextView signIn = findViewById(R.id.signInTextView);

        signIn.setOnClickListener(view -> {
            final Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);

            startActivity(intent);
        });
        passwordVisibility.setOnClickListener(view -> signUpController.onClickPasswordVisibility(userPassword, passwordVisibility));
        confirmPasswordVisibility.setOnClickListener(view -> signUpController.onClickPasswordVisibility(confirmPassword, confirmPasswordVisibility));
        createAccount.setOnClickListener(view -> signUpController.onClickCreateAccount());

    }

    public void createNewAccount() {
        final UserProfile userProfile = new UserProfile();
        final Credential credential = new Credential();
        final String password = confirmPassword.getText().toString().trim();

        userProfile.setUserName(userName.getText().toString().trim());
        userProfile.setEmail(userEmail.getText().toString().trim());
        userProfile.setTitle(userTitle.getText().toString().trim());
        credential.setEmail(userEmail.getText().toString().trim());
        credential.setPassword(userPassword.getText().toString().trim());
        credential.setHint(hint.getText().toString().trim());

        if (TextUtils.isEmpty(userProfile.getUserName()) || TextUtils.isEmpty(
                credential.getEmail()) || TextUtils.isEmpty(userProfile.getTitle()) ||
                TextUtils.isEmpty(credential.getPassword())) {
            showSnackBar(getString(R.string.fields_fill));
        } else if (! password.equals(credential.getPassword())) {
            showSnackBar(getString(R.string.password_mismatch));
        }
        final AuthenticationService authenticationService =
                new AuthenticationService(getString(R.string.base_url));

        authenticationService.signUp(userProfile, credential,
                new AuthenticationService.ApiResponseCallBack() {
            @Override
            public void onSuccess(final String response) {
                showSnackBar(getString(R.string.account));
            }

            @Override
            public void onError(final String errorMessage) {
                showSnackBar(String.format(getString(R.string.request_failed_s), errorMessage));
            }
        });
        finish();
    }

    public void togglePasswordVisibility(final EditText password, final ImageView visibility) {
        if (isPasswordVisible) {
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            isPasswordVisible = false;

            visibility.setImageResource(R.drawable.visibility);
        } else {
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            isPasswordVisible = true;

            visibility.setImageResource(R.drawable.visibility_off);
        }
        password.setSelection(password.getText().length());
    }

    private void showSnackBar(final String message) {
        final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT);

        snackbar.show();
    }
}