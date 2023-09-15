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
import com.example.todo.dao.CredentialDao;
import com.example.todo.dao.impl.CredentialDaoImpl;
import com.example.todo.model.Credential;
import com.google.android.material.snackbar.Snackbar;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ForgetActivity extends AppCompatActivity {

    private EditText userEmail;
    private EditText newHint;
    private EditText oldHint;
    private EditText newPassword;
    private EditText confirmPassword;
    private CredentialDao credentialDao;
    private boolean isPasswordVisible;
    private ImageView newPasswordToggle;
    private ImageView confirmPasswordToggle;
    private AuthenticationService authenticationService;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);

        final Button cancel = findViewById(R.id.cancelToCreatePassword);
        final Button resetPassword = findViewById(R.id.resetPassword);
        newPasswordToggle = findViewById(R.id.newPasswordToggle);
        confirmPasswordToggle = findViewById(R.id.confirmPasswordToggle);
        userEmail = findViewById(R.id.emailEdit);
        newHint = findViewById(R.id.newHint);
        oldHint = findViewById(R.id.oldHint);
        newPassword = findViewById(R.id.newPassword);
        confirmPassword = findViewById(R.id.confirmPasswordEdit);
        credentialDao = new CredentialDaoImpl(this);

        cancel.setOnClickListener(view -> {
            final Intent intent = new Intent(ForgetActivity.this,
                    SignInActivity.class);

            startActivity(intent);
        });
        newPasswordToggle.setOnClickListener(view -> togglePasswordActivity(newPassword,
                newPasswordToggle));
        confirmPasswordToggle.setOnClickListener(view -> togglePasswordActivity(confirmPassword,
                confirmPasswordToggle));
        resetPassword.setOnClickListener(view -> {
            final Credential credential = new Credential();
            final String password = confirmPassword.getText().toString().trim();
            final String hint = newHint.getText().toString().trim();

            credential.setEmail(userEmail.getText().toString().trim());
            credential.setPassword(newPassword.getText().toString().trim());
            credential.setHint(oldHint.getText().toString().trim());

            if (TextUtils.isEmpty(credential.getEmail()) || TextUtils.isEmpty(credential.getPassword())
                    || TextUtils.isEmpty(credential.getHint()) || TextUtils.isEmpty(hint)) {
                showSnackBar(getString(R.string.fields_fill));
            } else if(! password.equals(credential.getPassword())) {
                showSnackBar(getString(R.string.password_mismatch));
            } else {
                authenticationService = new AuthenticationService("http://192.168.1.3:8080/");

                authenticationService.resetPassword(credential, hint, new AuthenticationService.ApiResponseCallBack() {
                    @Override
                    public void onSuccess(final String response) {
                        showSnackBar(getString(R.string.password_updated));
                    }

                    @Override
                    public void onError(final String errorMessage) {
                        showSnackBar(String.format(getString(R.string.request_failed_s), errorMessage));
                    }
                });
//                final boolean emailExists = credentialDao.checkEmailExists(credential.getEmail());
//
//                if (emailExists) {
//                    final long updatedCredentialRows = credentialDao.updatePassword(credential);
//
//                    if (0 < updatedCredentialRows) {
//                        showSnackBar(getString(R.string.password_update));
//                        userEmail.setText("");
//                        newPassword.setText("");
//                        confirmPassword.setText("");
//                        finish();
//                    } else {
//                        showSnackBar(getString(R.string.fail));
//                    }
//                } else {
//                    showSnackBar(getString(R.string.invalid_email));
//                    userEmail.setText("");
//                    newPassword.setText("");
//                    confirmPassword.setText("");
//                }
            }
        });
    }

    private String hashPassword(final String password) {
        try {
            final MessageDigest messageDigest = MessageDigest.getInstance("MD5");

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

    private void togglePasswordActivity(final EditText password, final ImageView visibilityToggle) {
        if (isPasswordVisible) {
            password.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            isPasswordVisible = false;

            visibilityToggle.setImageResource(R.drawable.visibility);
        } else {
            password.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
            isPasswordVisible = true;

            visibilityToggle.setImageResource(R.drawable.visibility_off);
        }
        password.setSelection(password.getText().length());
    }

    private void showSnackBar(final String message) {
        final View parentLayout = findViewById(android.R.id.content);
        final int backGroundColor = Color.argb(200, 255, 255, 255);
        final Snackbar snackbar = Snackbar.make(parentLayout, message, Snackbar.LENGTH_LONG);

        snackbar.setTextColor(getResources().getColor(R.color.black));
        snackbar.setBackgroundTint(backGroundColor);
        snackbar.show();
    }
}
