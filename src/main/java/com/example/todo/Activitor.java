package com.example.todo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todo.controller.ActivitorController;
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
public class Activitor extends AppCompatActivity implements ActivatorService{

    private String selectedList;
    private ActivitorController activitorController;

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
        activitorController = new ActivitorController(this, this);

        menuButton.setOnClickListener(view -> activitorController.onClickMenu(selectedList));
    }

    @Override
    public void goToNavigation(final String selectedList) {
        final Intent intent = new Intent(Activitor.this, NavigationActivity.class);

        intent.putExtra(getString(R.string.navigation_view), selectedList);
        startActivity(intent);
    }
}