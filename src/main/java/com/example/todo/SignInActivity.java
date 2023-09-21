package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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

import com.example.todo.api.AuthenticationService;
import com.example.todo.model.Credential;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

public class SignInActivity extends AppCompatActivity {

    private EditText userEmail;
    private EditText userPassword;
    private ImageView passwordVisibilityToggle;
    private boolean isPasswordVisible;
    private AuthenticationService authenticationService;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        final TextView signUp = findViewById(R.id.signUpTextView);
        final TextView forgotPassword = findViewById(R.id.forgotPassword);
        final Button signIn = findViewById(R.id.signInButton);
        passwordVisibilityToggle = findViewById(R.id.passwordVisibilityToggle);
        userEmail = findViewById(R.id.emailEditText);
        userPassword = findViewById(R.id.passwordEditText);

        signUp.setOnClickListener(view -> {
            final Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);

            startActivity(intent);
        });
        forgotPassword.setOnClickListener(view -> {
            final Intent intent = new Intent(SignInActivity.this, PasswordResetActivity.class);

            startActivity(intent);
        });
        passwordVisibilityToggle.setOnClickListener(view -> togglePasswordActivity());
        signIn.setOnClickListener(view -> {
            final Credential credential = new Credential();

            credential.setEmail(userEmail.getText().toString().trim());
            credential.setPassword(userPassword.getText().toString().trim());

            if (TextUtils.isEmpty(credential.getEmail())
                    || TextUtils.isEmpty(credential.getPassword())) {
                showSnackBar(getString(R.string.fields_fill));
            } else {
                authenticationService = new AuthenticationService("http://192.168.1.3:8080/");

                authenticationService.signIn(credential, new AuthenticationService.ApiResponseCallBack() {
                    @Override
                    public void onSuccess(final String response) throws JSONException {
                        showSnackBar(getString(R.string.sign_in_successfully));
                        final JSONObject jsonObject = new JSONObject(response);
                        final JSONObject object = jsonObject.getJSONObject(getString(R.string.data));
                        final String token = object.getString(getString(R.string.token));

                        new Handler().postDelayed(() -> {
                            final Intent intent = new Intent(SignInActivity.this,
                                    Activator.class);

                            intent.putExtra(getString(R.string.token), token);
                            startActivity(intent);
                        }, 200);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        showSnackBar(String.format(getString(R.string.request_failed_s), errorMessage));
                    }
                });
            }
        });
    }

    private void togglePasswordActivity() {
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

    @SuppressLint("ResourceAsColor")
    private void showSnackBar(final String message) {
        final View view = findViewById(android.R.id.content);
        final Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);

        snackbar.setTextColor(getResources().getColor(R.color.black));
        snackbar.setBackgroundTint(R.color.white);
        snackbar.show();
    }
}