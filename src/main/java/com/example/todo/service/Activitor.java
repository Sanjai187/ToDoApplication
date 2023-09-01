package com.example.todo.service;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.todo.R;
import com.example.todo.controller.ActivatorController;
import com.example.todo.model.Project;
import com.example.todo.model.ProjectList;
import com.example.todo.model.UserProfile;

import java.util.List;

/**
 *
 * <p>
 * Representing the main activity of the Todo application
 * </p>
 *
 * @author sanjai
 * @version 1.0
 */
public class Activitor extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ArrayAdapter<Project> arrayAdapter;
    private ActivatorController activatorController;
    private static Long id = 0L;
    private static final int REQUEST_CODE = 1;
    private TextView profileIcon;
    private TextView userName;
    private TextView userTitle;
    private Long userId;

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

        final ImageView editButton = findViewById(R.id.editIcon);
        final Button addList = findViewById(R.id.addlist);
        final ImageButton menuButton = findViewById(R.id.menuButton);
        final ListView listView = findViewById(R.id.nameListView);
        final EditText editText = findViewById(R.id.projectList);
        final Button addButton = findViewById(R.id.addProject);
        final ProjectList projectList = new ProjectList();
        final List<Project> list = projectList.getAllList();
        drawerLayout = findViewById(R.id.Layout);
        profileIcon = findViewById(R.id.profileIcon);
        userName = findViewById(R.id.userName);
        userTitle = findViewById(R.id.userTitle);
        activatorController = new ActivatorController(this, projectList);
        arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, list);

        listView.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
        menuButton.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));
        addList.setOnClickListener(view -> {
            if (editText.getVisibility() == View.GONE) {
                editText.setVisibility(View.VISIBLE);
                addButton.setVisibility(View.VISIBLE);
            } else {
                editText.setVisibility(View.GONE);
                addButton.setVisibility(View.GONE);
            }
        });
        addButton.setOnClickListener(view -> {
            final String text = editText.getText().toString().trim();

            if (!text.isEmpty()) {
                final Project project = new Project(text);

                project.setId(id);
                project.setLabel(text);
                project.setUserId(userId);
                projectList.add(project);
                arrayAdapter.notifyDataSetChanged();
                editText.getText().clear();
            }
        });
        editButton.setOnClickListener(view -> {
            final Intent intent = new Intent(Activitor.this, UserProfileActivity.class);

            intent.putExtra(getString(R.string.user), userName.getText().toString());
            intent.putExtra(getString(R.string.user_title), userTitle.getText().toString());
            startActivityIfNeeded(intent, REQUEST_CODE);
        });
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            final Project selectedProject = projectList.getAllList().get(i);

            activatorController.onListItemClicked(selectedProject);
        });
        listView.setOnItemLongClickListener((adapterView, view, i, l) -> {
            final Project selectedProject = projectList.getAllList().get(i);

            activatorController.onListItemLongClicked(selectedProject);
            return true;
        });
    }

    /**
     * <p>
     * Removes a project from the project list
     * </p>
     *
     * @param project The position of the project to be removed
     */
    public void removeList(final Project project) {
        arrayAdapter.remove(project);
        arrayAdapter.notifyDataSetChanged();
    }

    public void goToListPage(final Project project) {
        final Intent intent = new Intent(Activitor.this, ChildProject.class);

        intent.putExtra(getString(R.string.project_id), project.getId());
        intent.putExtra(getString(R.string.project_name), project.getLabel());
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            final UserProfile userProfile = new UserProfile();

            userId = data.getLongExtra(getString(R.string.user_id), 0L);
            userProfile.setUserName(data.getStringExtra(getString(R.string.user)));
            userProfile.setTitle(data.getStringExtra(getString(R.string.user_title)));
            userName.setText(userProfile.getUserName());
            userTitle.setText(userProfile.getTitle());
            profileIcon.setText(userProfile.getProfileIcon());
        }
    }

//    /**
//     * <p>
//     * Displays a dialog box for adding a new project name
//     * </p>
//     */
//    public void addNameDialog() {
//        final EditText text = new EditText(this);
//
//        text.setInputType(InputType.TYPE_CLASS_TEXT);
//        new AlertDialog.Builder(this).setTitle(R.string.add_name).setView(text).setPositiveButton(R.string.ok, (dialog, which) -> {
//            final String name = text.getText().toString().trim();
//
//            activatorController.onNameAdded(name, ++id, userId);
//            arrayAdapter.notifyDataSetChanged();
//        }).setNegativeButton(R.string.cancel, null).create().show();
//    }
}