package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.todo.controller.NavigationController;
import com.example.todo.dao.ProjectDao;
import com.example.todo.dao.UserDao;
import com.example.todo.dao.impl.ProjectDaoImpl;
import com.example.todo.dao.impl.UserDaoImpl;
import com.example.todo.model.Project;
import com.example.todo.model.ProjectList;
import com.example.todo.model.UserProfile;
import com.example.todo.projectadapter.DragItemHelper;
import com.example.todo.projectadapter.ProjectAdapter;
import com.example.todo.service.NavigationService;

import java.util.List;

public class NavigationActivity extends AppCompatActivity implements NavigationService {

    private List<com.example.todo.model.Project> projects;
    private ProjectAdapter projectAdapter;
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
    private UserDao userDao;
    private UserProfile userProfile;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        getIntent().getStringExtra(getString(R.string.navigation_view));
        final ImageView editButton = findViewById(R.id.editIcon);
        final ImageView backButton = findViewById(R.id.backToMenu);
        final Button addList = findViewById(R.id.addlist);
        editText = findViewById(R.id.projectList);
        addButton = findViewById(R.id.addProject);
        projectList = new ProjectList();
        profileIcon = findViewById(R.id.profileIcon);
        userName = findViewById(R.id.userName);
        userTitle = findViewById(R.id.userTitle);
        navigationController = new NavigationController(this, this, projectList);
        projectDao = new ProjectDaoImpl(this);
        userDao = new UserDaoImpl(this);
        projects = projectList.getAllList();

        loadUserFromDB();
        initRecyclerView();
        backButton.setOnClickListener(view -> onBackPressed());
        addList.setOnClickListener(view -> navigationController.onClickTextVisibility());
        addButton.setOnClickListener(view -> navigationController.onAddProject());
        editButton.setOnClickListener(view -> {
            final Intent intent = new Intent(NavigationActivity.this, UserProfileActivity.class);

            intent.putExtra(getString(R.string.user), userName.getText().toString());
            intent.putExtra(getString(R.string.user_title), userTitle.getText().toString());
            startActivityIfNeeded(intent, REQUEST_CODE);
        });
        TypeFaceUtil.applyFontToView(getWindow().getDecorView().findViewById(android.R.id.content));
        TypeFaceUtil.applyTextSizeToView(getWindow().getDecorView().findViewById(android.R.id.content));
        applyColorToComponent();
    }

    private void initRecyclerView() {
        final RecyclerView recyclerView = findViewById(R.id.nameListView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        projectAdapter = new ProjectAdapter(projects, projectDao);
        final ItemTouchHelper.Callback callback = new DragItemHelper(projectAdapter);
        final ItemTouchHelper touchHelper = new ItemTouchHelper(callback);

        recyclerView.setAdapter(projectAdapter);
        touchHelper.attachToRecyclerView(recyclerView);
        loadProjectsFromDB();
        projectAdapter.setOnItemClickListener(position -> navigationController.onListItemClicked(projectList.getAllList().get(position)));
    }

    private void loadUserFromDB() {
        userProfile = userDao.getUserProfile();

        if (null != userProfile) {
            userName.setText(userProfile.getUserName());
            userTitle.setText(userProfile.getTitle());
            profileIcon.setText(userProfile.getProfileIcon());
            userId = userProfile.getId();
        }
    }

    public void goToListPage(final Project project) {
        final Intent intent = new Intent(NavigationActivity.this, TodoListActivity.class);

        intent.putExtra(getString(R.string.project_id), project.getId());
        intent.putExtra(getString(R.string.project_name), project.getLabel());
        startActivity(intent);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void addProjectList() {
        final String text = editText.getText().toString().trim();

        if (!text.isEmpty()) {
            final Project project = new Project();

            project.setId(++id);
            project.setLabel(text);
            project.setUserId(1L);
            project.setOrder((long) (projectAdapter.getItemCount() + 1));
            projectList.add(project);
            projectDao.insert(project);
            projectAdapter.notifyDataSetChanged();
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

    private void loadProjectsFromDB() {
        projects = projectDao.getAllProjects();

        projectAdapter.clearProjects();
        projectAdapter.addProjects(projects);
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

    private void applyColorToComponent() {
        final int defaultColor = TypeFaceUtil.getSelectedDefaultColor();
        final RelativeLayout relativeLayout = findViewById(R.id.profileView);

        if (defaultColor == R.color.green) {
            relativeLayout.setBackgroundColor(getResources().getColor(R.color.green));
        } else if (defaultColor == R.color.blue) {
            relativeLayout.setBackgroundColor(getResources().getColor(R.color.blue));
        } else if (defaultColor == R.color.Violet) {
            relativeLayout.setBackgroundColor(getResources().getColor(R.color.Violet));
        }
    }
}
