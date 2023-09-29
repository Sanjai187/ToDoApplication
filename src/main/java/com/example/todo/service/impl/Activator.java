package com.example.todo.service.impl;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todo.R;
import com.example.todo.controller.ActivatorController;
import com.example.todo.service.ActivatorService;

/**
 *
 * <p>
 * The main activity of the Todo application.
 * </p>
 *
 * @author sanjai
 * @version 1.0
 */
public class Activator extends AppCompatActivity implements ActivatorService {

    private String token;
    private ActivatorController activatorController;

    /**
     * <p>
     * Creation of the main activity
     * </p>
     *
     * @param savedInstanceState A bundle containing the saved state.
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        token = getIntent().getStringExtra(getString(R.string.token));
        final ImageButton menuButton = findViewById(R.id.menuButton);
        final ImageView settingButton = findViewById(R.id.settings);
        activatorController = new ActivatorController(this);

        menuButton.setOnClickListener(view -> activatorController.onClickMenu());
        settingButton.setOnClickListener(view -> activatorController.onClickSetting());
        TypeFaceUtil.applyTypefaceToView(getWindow().getDecorView().findViewById(android.R.id.content));
        TypeFaceUtil.applyTextSizeToView(getWindow().getDecorView().findViewById(android.R.id.content));
        applyColorToComponent();
    }

    /**
     * <p>
     * Navigate to the navigation activity.
     * </p>
     */
    @Override
    public void startNavigationActivity() {
        final Intent intent = new Intent(Activator.this, NavigationActivity.class);

        intent.putExtra(getString(R.string.token), token);
        startActivity(intent);
    }

    /**
     * <p>
     * Navigate to the setting activity.
     * </p>
     */
    @Override
    public void startSettingActivity() {
        final Intent intent = new Intent(Activator.this, SettingActivity.class);

        intent.putExtra(getString(R.string.token), token);
        startActivity(intent);
    }

    /**
     * <p>
     * Apply the selected color theme to UI components.
     * </p>
     */
    private void applyColorToComponent() {
        final int defaultColor = TypeFaceUtil.getSelectedDefaultColor();
        final RelativeLayout layout = findViewById(R.id.relativeLayout);

        if (defaultColor == R.color.green) {
            layout.setBackgroundColor(getResources().getColor(R.color.green));
        } else if (defaultColor == R.color.blue) {
            layout.setBackgroundColor(getResources().getColor(R.color.blue));
        } else if (defaultColor == R.color.Violet) {
            layout.setBackgroundColor(getResources().getColor(R.color.Violet));
        }
    }
}