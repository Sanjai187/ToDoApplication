package com.example.todo.service.impl;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.example.todo.R;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_activity);

        final ImageView loading = findViewById(R.id.loading);
        new Handler().postDelayed(() -> loading.setVisibility(View.GONE),2800);
        new Handler().postDelayed(() -> {
            final Intent intent = new Intent(SplashActivity.this, SignInActivity.class);

            startActivity(intent);
            finish();
        },3000);
    }
}