package com.example.todo.service.impl;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.todo.controller.SignInController;
import com.example.todo.model.Credential;
import com.example.todo.service.SignInService;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

public class SignInActivity extends AppCompatActivity implements SignInService {

    private EditText userEmail;
    private EditText userPassword;
    private ImageView passwordVisibilityToggle;
    private boolean isPasswordVisible;
    private SignInController signInController;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        final TextView signUp = findViewById(R.id.signUpTextView);
        final TextView forgotPassword = findViewById(R.id.forgotPassword);
        final Button signInButton = findViewById(R.id.signInButton);

        passwordVisibilityToggle = findViewById(R.id.passwordVisibilityToggle);
        userEmail = findViewById(R.id.emailEditText);
        userPassword = findViewById(R.id.passwordEditText);
        signInController = new SignInController(this);

        signUp.setOnClickListener(view -> startSignUpActivity());
        forgotPassword.setOnClickListener(view -> startPasswordResetActivity());
        passwordVisibilityToggle.setOnClickListener(view -> signInController.onClickPasswordVisibility());
        signInButton.setOnClickListener(view -> signInController.onClickSignIn());
    }

    private void startSignUpActivity() {
        final Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);

        startActivity(intent);
    }

    private void startPasswordResetActivity() {
        final Intent intent = new Intent(SignInActivity.this, PasswordResetActivity.class);

        startActivity(intent);
    }

    public void signIn() {
        final Credential credential = new Credential();

        credential.setEmail(userEmail.getText().toString().trim());
        credential.setPassword(userPassword.getText().toString().trim());

        if (TextUtils.isEmpty(credential.getEmail()) || TextUtils.isEmpty(credential.getPassword())) {
            showSnackBar(getString(R.string.fields_fill));
        } else {
            final AuthenticationService authenticationService = new AuthenticationService(getString(R.string.base_url));

            authenticationService.signIn(credential, new AuthenticationService.ApiResponseCallBack() {
                @Override
                public void onSuccess(final String response) throws JSONException {
                    handleSignInSuccess(response);
                }

                @Override
                public void onError(String errorMessage) {
                    showSnackBar(String.format(getString(R.string.request_failed_s), errorMessage));
                }
            });
        }
    }

    private void handleSignInSuccess(String response) throws JSONException {
        showSnackBar(getString(R.string.sign_in_successfully));
        final JSONObject jsonObject = new JSONObject(response);
        final JSONObject object = jsonObject.getJSONObject(getString(R.string.data));
        final String token = object.getString(getString(R.string.token));

        new Handler().postDelayed(() -> startActivatorActivity(token), 200);
    }

    private void startActivatorActivity(String token) {
        final Intent intent = new Intent(SignInActivity.this, Activator.class);

        intent.putExtra(getString(R.string.token), token);
        startActivity(intent);
    }

    public void togglePasswordVisibility() {
        if (isPasswordVisible) {
            userPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            isPasswordVisible = false;
            passwordVisibilityToggle.setImageResource(R.drawable.visibility);
        } else {
            userPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            isPasswordVisible = true;
            passwordVisibilityToggle.setImageResource(R.drawable.visibility_off);
        }
        userPassword.setSelection(userPassword.getText().length());
    }

    private void showSnackBar(final String message) {
        final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT);

        snackbar.show();
    }
}
