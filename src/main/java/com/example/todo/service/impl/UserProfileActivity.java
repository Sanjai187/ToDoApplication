package com.example.todo.service.impl;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todo.R;
import com.example.todo.api.impl.AuthenticationService;
import com.example.todo.model.UserProfile;
import com.google.android.material.snackbar.Snackbar;

public class UserProfileActivity extends AppCompatActivity {

    private EditText userTitle;
    private EditText userName;
    private TextView profileIcon;
    private UserProfile userProfile;
    private String token;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        final ImageButton backButton = findViewById(R.id.backMenu);
        final Button cancelButton = findViewById(R.id.cancelButton);
        final Button saveButton = findViewById(R.id.saveButton);
        userTitle = findViewById(R.id.editTitle);
        userName = findViewById(R.id.editUserName);
        profileIcon = findViewById(R.id.userProfile);
        token = getIntent().getStringExtra(getString(R.string.token));
        userProfile = new UserProfile();

        backButton.setOnClickListener(view -> onBackPressed());
        cancelButton.setOnClickListener(view -> onBackPressed());
        saveButton.setOnClickListener(view -> {
            final AuthenticationService authenticationService = new AuthenticationService(getString(R.string.base_url), token);

            if (null != userProfile) {
                final String name = null != userProfile.getUserName() ? userProfile.getUserName() : "";
                final String title = null != userProfile.getTitle() ? userProfile.getTitle() : "";

                if (!name.equals(userName.getText().toString()) || !title.equals(userTitle.getText().toString())) {
                    userProfile.setUserName(userName.getText().toString());
                    userProfile.setTitle(userTitle.getText().toString());
                    profileIcon.setText(userProfile.getProfileIcon());
                    final Intent intent = new Intent();
                    authenticationService.updateUserDetail(userProfile, new AuthenticationService.ApiResponseCallBack() {
                        @Override
                        public void onSuccess(final String response) {
                            showSnackBar(getString(R.string.update_successfully));
                            intent.putExtra(getString(R.string.user), userProfile.getUserName());
                            intent.putExtra(getString(R.string.user_title), userProfile.getTitle());
                            setResult(RESULT_OK, intent);
                            finish();
                        }

                        @Override
                        public void onError(final String errorMessage) {
                            showSnackBar(errorMessage);
                        }
                    });
                }
            }
        });
        TypeFaceUtil.applyTypefaceToView(getWindow().getDecorView().findViewById(android.R.id.content));
        TypeFaceUtil.applyTextSizeToView(getWindow().getDecorView().findViewById(android.R.id.content));
        applyColorToComponent();
    }

    private void applyColorToComponent() {
        final int defaultColor = TypeFaceUtil.getSelectedDefaultColor();
        final LinearLayout layout = findViewById(R.id.linerLayout);

        if (defaultColor == R.color.green) {
            layout.setBackgroundColor(getResources().getColor(R.color.green));
        } else if (defaultColor == R.color.blue) {
            layout.setBackgroundColor(getResources().getColor(R.color.blue));
        } else if (defaultColor == R.color.Violet) {
            layout.setBackgroundColor(getResources().getColor(R.color.Violet));
        }
    }

    private void showSnackBar(final String message) {
        final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT);

        snackbar.show();
    }
}