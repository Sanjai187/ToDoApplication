package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.todo.api.AuthenticationService;
import com.example.todo.dao.CredentialDao;
import com.example.todo.dao.impl.CredentialDaoImpl;
import com.example.todo.model.Credential;
import com.google.android.material.snackbar.Snackbar;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SignInActivity extends AppCompatActivity {

    private CredentialDao credentialDao;
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
        credentialDao = new CredentialDaoImpl(this);

        signUp.setOnClickListener(view -> {
            final Intent intent = new Intent(SignInActivity.this,
                    SignUpActivity.class);

            startActivity(intent);
        });
        forgotPassword.setOnClickListener(view -> {
            final Intent intent = new Intent(SignInActivity.this,
                    ForgetActivity.class);

            startActivity(intent);
        });
        passwordVisibilityToggle.setOnClickListener(view -> togglePasswordActivity());
        signIn.setOnClickListener(view -> {
            final Credential credential = new Credential();
            final String hashPassword = hashPassword(userPassword.getText().toString().trim());

            credential.setEmail(userEmail.getText().toString().trim());
            credential.setPassword(hashPassword);

            if (TextUtils.isEmpty(credential.getEmail())
                    || TextUtils.isEmpty(credential.getPassword())) {
                showSnackBar(getString(R.string.fields_fill));
            } else {
                authenticationService = new AuthenticationService("http://192.168.1.3:8080/");

                authenticationService.signIn(credential, new AuthenticationService.ApiResponseCallBack() {
                    @Override
                    public void onSuccess(final String response) {
                        showSnackBar(getString(R.string.sign_in_successfully));
                    }

                    @Override
                    public void onError(String errorMessage) {
                        showSnackBar(String.format(getString(R.string.request_failed_s), errorMessage));
                    }
                });
//                final boolean isAuthenticated = credentialDao.checkCredentials(loginDetail);
//
//                if (isAuthenticated) {
//                    showSnackBar(getString(R.string.sign_in_successfully));
//                    new Handler().postDelayed(() -> {
//                        final Intent intent = new Intent(SignInActivity.this,
//                                Activator.class);
//
//                        startActivity(intent);
//                    }, 300);
//                } else {
//                    showSnackBar(getString(R.string.invalid_details));
//                    userEmail.setText("");
//                    userPassword.setText("");
//                }
            }
        });
    }

    private String hashPassword(final String password) {
        try {
            final MessageDigest messageDigest = MessageDigest.getInstance(getString(R.string.md5));

            messageDigest.update(password.getBytes());

            final byte[] bytes = messageDigest.digest();

            final StringBuilder stringBuilder = new StringBuilder();
            for (final byte aByte : bytes) {
                stringBuilder.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }

            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    private void togglePasswordActivity() {
        if (isPasswordVisible) {
            userPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            isPasswordVisible = false;

            passwordVisibilityToggle.setImageResource(R.drawable.visibility);
        } else {
            userPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
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
        snackbar.setBackgroundTint(R.color.gray);
        snackbar.show();
    }
}