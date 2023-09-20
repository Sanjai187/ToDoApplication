package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
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

import com.example.todo.api.AuthenticationService;
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
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NavigationActivity extends AppCompatActivity implements NavigationService {

    private List<Project> projects;
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
    private String token;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        token = getIntent().getStringExtra(getString(R.string.token));
        final DrawerLayout layout = findViewById(R.id.drawerLayout);
        final ImageView menuButton = findViewById(R.id.menuButton);
        final ImageView settingButton = findViewById(R.id.settings);
        final ImageView editButton = findViewById(R.id.editIcon);
        final Button addList = findViewById(R.id.addlist);
        final ImageView logOut = findViewById(R.id.signOut);
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

        initRecyclerView();
        menuButton.setOnClickListener(view -> layout.openDrawer(GravityCompat.START));
        addList.setOnClickListener(view -> navigationController.onClickTextVisibility());
        addButton.setOnClickListener(view -> navigationController.onAddProject());
        settingButton.setOnClickListener(view -> {
            final Intent intent = new Intent(NavigationActivity.this, SettingActivity.class);

            startActivity(intent);
        });
        logOut.setOnClickListener(view -> {
            final Intent intent = new Intent(NavigationActivity.this, SignInActivity.class);

            startActivity(intent);
        });
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
        loadProjectFromDB();
        projectAdapter.setOnItemClickListener(new ProjectAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final int position) {
                final Project selectedProject = projectList.getAllList().get(position);

                navigationController.onListItemClicked(selectedProject);
            }

            @Override
            public void onRemoveButtonClick(final int position) {
                final Project projectToRemove = projects.get(position);

                removeProject(projectToRemove);
            }
        });
    }

//    private void loadUserFromDB() {
//        final UserProfile userProfile = userDao.getUserProfile();
//        final UserProfile userProfile = userDao.getUserDetails(email);
//
//        if (null != userProfile) {
//            userName.setText(userProfile.getUserName());
//            userTitle.setText(userProfile.getTitle());
//            profileIcon.setText(userProfile.getProfileIcon());
//            userId = userProfile.getId();
//        }
//    }

    private void loadProjectFromDB(){
        final AuthenticationService authenticationService = new AuthenticationService("http://192.168.1.3:8080/", token);

        authenticationService.getAllProject(new AuthenticationService.ApiResponseCallBack() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(final String response) {
                projects = getAllProjects(response);

                projectAdapter.clearProjects();
                projectAdapter.addProjects(projects);
            }

            @Override
            public void onError(final String errorMessage) {
                showSnackBar(String.format(getString(R.string.request_failed_s), errorMessage));
            }
        });
    }

    private List<Project> getAllProjects(final String responseBody) {
        final List<Project> projects = new ArrayList<>();

        try {
            final JSONObject jsonObject = new JSONObject(responseBody);
            final JSONArray data = jsonObject.getJSONArray(getString(R.string.data));

            for (int i = 0; i <= data.length(); i++) {
                final JSONObject jsonObjects = data.getJSONObject(1);
                final String projectId = jsonObjects.getString(getString(R.string.id));
                final String projectName = jsonObjects.getString(getString(R.string.Name));
                final String description = jsonObjects.getString(getString(R.string.description));

                final Project project = new Project();

                project.setId(projectId);
                project.setName(projectName);
                project.setDescription(description);
                projects.add(project);
            }
        } catch (JSONException exception) {
            throw new RuntimeException(exception);
        }
        return projects;
    }

    private void removeProject(final Project project) {
        final AuthenticationService authenticationService = new AuthenticationService("http://192.168.1.3:8080/", token);

        authenticationService.delete(project.getId(), new AuthenticationService.ApiResponseCallBack() {
            @Override
            public void onSuccess(final String response){
                showSnackBar(getString(R.string.removed_project));
            }

            @Override
            public void onError(final String errorMessage) {
                showSnackBar(errorMessage);
            }
        });
    }

    public void goToListPage(final Project project) {
        final Intent intent = new Intent(NavigationActivity.this, TodoListActivity.class);

        intent.putExtra(getString(R.string.project_id), project.getId());
        intent.putExtra(getString(R.string.project_name), project.getName());
        startActivity(intent);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void addProjectList() {
        final String text = editText.getText().toString().trim();

        if (!text.isEmpty()) {
            final Project project = new Project();

//            project.setId(++id);
            project.setName(text);
            project.setDescription("Description");
//            project.setUserId(userId);
//            project.setOrder((long) (projectAdapter.getItemCount() + 1));
            projectList.add(project);
//            projectDao.insert(project);
            final AuthenticationService authenticationService = new AuthenticationService("http://192.168.1.3:8080/", token);

            authenticationService.createProject(project, new AuthenticationService.ApiResponseCallBack() {
                @Override
                public void onSuccess(final String response) {
                    showSnackBar(getString(R.string.project_created));

                    projectAdapter.notifyDataSetChanged();
                }

                @Override
                public void onError(final String errorMessage) {
                    showSnackBar(String.format(getString(R.string.request_failed_s), errorMessage));
                }
            });
            projectAdapter.notifyDataSetChanged();
            editText.getText().clear();
        }
    }

    @SuppressLint("ResourceAsColor")
    private void showSnackBar(final String message) {
        final View view = findViewById(android.R.id.content);
        final Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);

        snackbar.setTextColor(getResources().getColor(R.color.black));
        snackbar.setBackgroundTint(R.color.gray);
        snackbar.show();
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
        final RelativeLayout relativeLayout1 = findViewById(R.id.relativeLayout);

        if (defaultColor == R.color.green) {
            relativeLayout.setBackgroundColor(getResources().getColor(R.color.green));
            relativeLayout1.setBackgroundColor(getResources().getColor(R.color.green));
        } else if (defaultColor == R.color.blue) {
            relativeLayout.setBackgroundColor(getResources().getColor(R.color.blue));
            relativeLayout1.setBackgroundColor(getResources().getColor(R.color.blue));
        } else if (defaultColor == R.color.Violet) {
            relativeLayout.setBackgroundColor(getResources().getColor(R.color.Violet));
            relativeLayout1.setBackgroundColor(getResources().getColor(R.color.Violet));
        }
    }
}