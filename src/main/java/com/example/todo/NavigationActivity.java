package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.todo.controller.NavigationController;
import com.example.todo.dao.ProjectDao;
import com.example.todo.dao.impl.ProjectDaoImpl;
import com.example.todo.model.Project;
import com.example.todo.model.ProjectList;
import com.example.todo.model.UserProfile;
import com.example.todo.service.NavigationService;

public class NavigationActivity extends AppCompatActivity implements NavigationService {

    private ArrayAdapter<Project> arrayAdapter;
    private NavigationController navigationController;
    private static Long id = 0L;
    private static final int REQUEST_CODE = 1;
    private TextView profileIcon;
    private TextView userName;
    private TextView userTitle;
    private Button addButton;
    private Long userId;
    private EditText editText;
    private ProjectList projectList;
    private ProjectDao projectDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        final String selectedList = getIntent().getStringExtra(getString(R.string.navigation_view));
        final ImageView editButton = findViewById(R.id.editIcon);
        final Button addList = findViewById(R.id.addlist);
        final ListView listView = findViewById(R.id.nameListView);
        editText = findViewById(R.id.projectList);
        addButton = findViewById(R.id.addProject);
        projectList = new ProjectList();
        profileIcon = findViewById(R.id.profileIcon);
        userName = findViewById(R.id.userName);
        userTitle = findViewById(R.id.userTitle);
        navigationController = new NavigationController(this, this, projectList);
        projectDao = new ProjectDaoImpl(this);
        arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, projectList.getAllList());

        listView.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
        addList.setOnClickListener(view -> navigationController.onClickTextVisibility());
        addButton.setOnClickListener(view -> navigationController.onAddProject());
        editButton.setOnClickListener(view -> {
            final Intent intent = new Intent(NavigationActivity.this, UserProfileActivity.class);

            intent.putExtra(getString(R.string.user), userName.getText().toString());
            intent.putExtra(getString(R.string.user_title), userTitle.getText().toString());
            startActivityIfNeeded(intent, REQUEST_CODE);
        });
        listView.setOnItemClickListener((adapterView, view, i, l) -> navigationController.onListItemClicked(projectList.getAllList().get(i)));
        listView.setOnItemLongClickListener((adapterView, view, i, l) -> {
            navigationController.onListItemLongClicked(projectList.getAllList().get(i));
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
        final Intent intent = new Intent(NavigationActivity.this, TodoActivity.class);

        intent.putExtra(getString(R.string.project_id), project.getId());
        intent.putExtra(getString(R.string.project_name), project.getLabel());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        projectDao.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        projectDao.close();
    }

    @Override
    public void addProjectList() {
        final String text = editText.getText().toString().trim();

        if (!text.isEmpty()) {
            final Project project = new Project(text);

            project.setId(id);
            project.setLabel(text);
            project.setUserId(userId);
            project.setOrder(id);
            projectList.add(project);
            projectDao.insert(project);

            arrayAdapter.notifyDataSetChanged();
            editText.getText().clear();
        }
    }

    public void toggleEditTextVisibility() {
        int visibility = editText.getVisibility() == View.GONE ? View.VISIBLE : View.GONE;
        editText.setVisibility(visibility);
        addButton.setVisibility(visibility);
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
}
