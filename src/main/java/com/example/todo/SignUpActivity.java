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
import com.example.todo.dao.UserDao;
import com.example.todo.dao.impl.CredentialDaoImpl;
import com.example.todo.dao.impl.UserDaoImpl;
import com.example.todo.model.Credential;
import com.example.todo.model.UserProfile;
import com.google.android.material.snackbar.Snackbar;

public class SignUpActivity extends AppCompatActivity {

    private UserDao userDao;
    private CredentialDao credentialDao;
    private EditText userName;
    private EditText userEmail;
    private EditText userTitle;
    private EditText hint;
    private EditText userPassword;
    private EditText confirmPassword;
    private boolean isPasswordVisible;
    private ImageView passwordVisibility;
    private ImageView confirmPasswordVisibility;
    private AuthenticationService authenticationService;

    @SuppressLint("MissingInflatedId")
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
        final Button createAccount = findViewById(R.id.createAccount);
        final TextView signIn = findViewById(R.id.signInTextView);
        userDao = new UserDaoImpl(this);
        credentialDao = new CredentialDaoImpl(this);

        signIn.setOnClickListener(view -> {
            final Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);

            startActivity(intent);
        });
        passwordVisibility.setOnClickListener(view -> togglePasswordActivity(userPassword, passwordVisibility));
        confirmPasswordVisibility.setOnClickListener(view -> togglePasswordActivity(confirmPassword, confirmPasswordVisibility));
        createAccount.setOnClickListener(view -> {
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
                    credential.getEmail()) || TextUtils.isEmpty(userProfile.getTitle()) || TextUtils.isEmpty(credential.getPassword())) {
                showSnackBar(getString(R.string.fields_fill));
            } else if (! password.equals(credential.getPassword())) {
                showSnackBar(getString(R.string.password_mismatch));
            }
            authenticationService = new AuthenticationService("http://192.168.1.3:8080/");

            authenticationService.signUp(userProfile, credential, new AuthenticationService.ApiResponseCallBack() {
                @Override
                public void onSuccess(final String response) {
                    showSnackBar(getString(R.string.account));
                    final long userId = userDao.insert(userProfile);

                    if (-1 != userId) {
                        showSnackBar(getString(R.string.account));
                        userName.setText("");
                        userEmail.setText("");
                        userPassword.setText("");
                        confirmPassword.setText("");
                    } else {
                        showSnackBar(getString(R.string.fail));
                    }
                }

                @Override
                public void onError(final String errorMessage) {
                    showSnackBar(String.format(getString(R.string.request_failed_s), errorMessage));
                }
            });
            finish();
        });
    }

    private void togglePasswordActivity(final EditText password, final ImageView visibility) {
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

    @SuppressLint("ResourceAsColor")
    private void showSnackBar(final String message) {
        final View view = findViewById(android.R.id.content);
        final Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);

        snackbar.setTextColor(getResources().getColor(R.color.black));
        snackbar.setBackgroundTint(R.color.gray);
        snackbar.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        userDao.open();
        credentialDao.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        userDao.close();
        credentialDao.close();
    }
}