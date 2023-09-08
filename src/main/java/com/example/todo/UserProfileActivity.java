package com.example.todo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todo.dao.UserDao;
import com.example.todo.dao.impl.UserDaoImpl;
import com.example.todo.model.UserProfile;

public class UserProfileActivity extends AppCompatActivity {

    private EditText userTitle;
    private EditText userName;
    private TextView profileIcon;
    private UserDao userDao;
    private UserProfile userProfile;

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
        userDao = new UserDaoImpl(this);
        userProfile = userDao.getUserProfile();

        if (null != userProfile) {
            userName.setText(userProfile.getUserName());
            userTitle.setText(userProfile.getTitle());
            profileIcon.setText(userProfile.getProfileIcon());
        } else {
            userProfile = new UserProfile();

            userProfile.setUserName(getIntent().getStringExtra(getString(R.string.user)));
            userProfile.setTitle(getIntent().getStringExtra(getString(R.string.user_title)));
        }
        backButton.setOnClickListener(view -> onBackPressed());
        cancelButton.setOnClickListener(view -> onBackPressed());
        saveButton.setOnClickListener(view -> {
            userProfile.setUserName(userName.getText().toString());
            userProfile.setTitle(userTitle.getText().toString());
            profileIcon.setText(userProfile.getProfileIcon());
            final Intent intent = new Intent();
            final long userId = null != userProfile.getId() ? userDao.onUpdate(userProfile) : userDao.insert(userProfile);

            userProfile.setId(userId);
            intent.putExtra(getString(R.string.user), userProfile.getUserName());
            intent.putExtra(getString(R.string.user_title), userProfile.getTitle());
            setResult(RESULT_OK, intent);
            finish();
        });
        TypeFaceUtil.applyFontToView(getWindow().getDecorView().findViewById(android.R.id.content));
        TypeFaceUtil.applyTextSizeToView(getWindow().getDecorView().findViewById(android.R.id.content));
        applyColorToComponent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        userDao.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        userDao.close();
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
}