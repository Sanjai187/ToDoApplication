package com.example.todo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todo.model.UserProfile;

public class UserProfileActivity extends AppCompatActivity {

    private EditText userTitle;
    private EditText userName;
    private TextView profileIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        final ImageButton backButton = findViewById(R.id.backMenu);
        final Button cancelButton = findViewById(R.id.cancelButton);
        final Button saveButton = findViewById(R.id.saveButton);
        userTitle = findViewById(R.id.editTitle);
        userName = findViewById(R.id.editUserName);
        profileIcon = findViewById(R.id.userProfile);
        final UserProfile userProfile = new UserProfile();

        userProfile.setUserName(getIntent().getStringExtra(getString(R.string.user)));
        userProfile.setTitle(getIntent().getStringExtra(getString(R.string.user_title)));
        userName.setText(userProfile.getUserName());
        userName.getText().clear();
        userTitle.setText(userProfile.getTitle());
        userTitle.getText().clear();
        profileIcon.setText(userProfile.getProfileIcon());
        backButton.setOnClickListener(view -> onBackPressed());
        cancelButton.setOnClickListener(view -> onBackPressed());
        saveButton.setOnClickListener(view -> {
            userProfile.setUserName(userName.getText().toString());
            userProfile.setTitle(userTitle.getText().toString());
            profileIcon.setText(userProfile.getProfileIcon());
            final Intent intent = new Intent();

            intent.putExtra(getString(R.string.user), userProfile.getUserName());
            intent.putExtra(getString(R.string.user_title), userProfile.getTitle());
            intent.putExtra(getString(R.string.user_id), 0L);
            setResult(RESULT_OK, intent);
            finish();
        });
    }
}