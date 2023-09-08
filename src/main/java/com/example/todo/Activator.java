package com.example.todo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todo.controller.ActivatorController;
import com.example.todo.service.ActivatorService;

/**
 *
 * <p>
 * Representing the main activity of the Todo application
 * </p>
 *
 * @author sanjai
 * @version 1.0
 */
public class Activator extends AppCompatActivity implements ActivatorService{

    private String selectedList;
    private ActivatorController activatorController;

    /**
     * <p>
     * Creation of the main activity
     * </p>
     *
     * @param savedInstanceState Refers the saved instance of the state
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageButton menuButton = findViewById(R.id.menuButton);
        final ImageView settingButton = findViewById(R.id.settings);
        activatorController = new ActivatorController(this, this);

        menuButton.setOnClickListener(view -> activatorController.onClickMenu(selectedList));
        settingButton.setOnClickListener(view -> activatorController.onClickSetting());
        TypeFaceUtil.applyFontToView(getWindow().getDecorView().findViewById(android.R.id.content));
        TypeFaceUtil.applyTextSizeToView(getWindow().getDecorView().findViewById(android.R.id.content));
        applyColorToComponent();
    }

    @Override
    public void goToNavigation(final String selectedList) {
        final Intent intent = new Intent(Activator.this, NavigationActivity.class);

        intent.putExtra(getString(R.string.navigation_view), selectedList);
        startActivity(intent);
    }

    @Override
    public void navigateToSettings() {
        final Intent intent = new Intent(Activator.this, SettingActivity.class);

        intent.putExtra(getString(R.string.settingview), selectedList);
        startActivity(intent);
    }

    private void applyColorToComponent() {
        final int defaultColor = TypeFaceUtil.getSelectedDefaultColor();
        final RelativeLayout layout = findViewById(R.id.relativeLayout);

        if (defaultColor == R.color.Violet) {
            layout.setBackgroundColor(getResources().getColor(R.color.Violet));
        } else if (defaultColor == R.color.blue) {
            layout.setBackgroundColor(getResources().getColor(R.color.blue));
        } else if (defaultColor == R.color.green) {
            layout.setBackgroundColor(getResources().getColor(R.color.green));
        }
    }
}