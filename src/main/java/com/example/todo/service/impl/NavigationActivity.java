package com.example.todo.service.impl;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.R;
import com.example.todo.api.impl.AuthenticationService;
import com.example.todo.api.impl.ProjectListService;
import com.example.todo.controller.NavigationController;
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
import java.util.Comparator;
import java.util.List;

public class NavigationActivity extends AppCompatActivity implements NavigationService {

    private List<Project> projects;
    private ProjectAdapter projectAdapter;
    private NavigationController navigationController;
    private static final int REQUEST_CODE = 1;
    private TextView profileIcon;
    private TextView userName;
    private TextView userTitle;
    private Button addProject;
    private EditText projectName;
    private ProjectList projectList;
    private UserProfile userProfile;
    private ServiceFactoryImpl serviceFactory;
    private String token;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        token = getIntent().getStringExtra(getString(R.string.token));
        final ImageView backButton = findViewById(R.id.backToMenu);
        final ImageView editButton = findViewById(R.id.editIcon);
        final Button addList = findViewById(R.id.addlist);
        final ImageView logOut = findViewById(R.id.signOut);
        projectName = findViewById(R.id.projectList);
        addProject = findViewById(R.id.addProject);
        projectList = new ProjectList();
        profileIcon = findViewById(R.id.profileIcon);
        userName = findViewById(R.id.userName);
        userTitle = findViewById(R.id.userTitle);
        navigationController = new NavigationController(this);
        serviceFactory = new ServiceFactoryImpl();
        userProfile = new UserProfile();
        projects = projectList.getAllList();

        initRecyclerView();
        getUserDetails();
        loadProjectFromDB();
        backButton.setOnClickListener(view -> onBackPressed());
        addList.setOnClickListener(view -> navigationController.onClickTextVisibility());
        addProject.setOnClickListener(view -> navigationController.onAddProject());
        logOut.setOnClickListener(view -> startSignInActivity());
        editButton.setOnClickListener(view -> startUserProfileActivity());
        TypeFaceUtil.applyTypefaceToView(getWindow().getDecorView().findViewById(android.R.id.content));
        TypeFaceUtil.applyTextSizeToView(getWindow().getDecorView().findViewById(android.R.id.content));
        applyColorToComponent();
    }

    private void initRecyclerView() {
        final RecyclerView recyclerView = findViewById(R.id.nameListView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        projectAdapter = new ProjectAdapter(projects);
        final ItemTouchHelper.Callback callback = new DragItemHelper(projectAdapter);
        final ItemTouchHelper touchHelper = new ItemTouchHelper(callback);

        recyclerView.setAdapter(projectAdapter);
        touchHelper.attachToRecyclerView(recyclerView);
        projectAdapter.setOnItemClickListener(new ProjectAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final int position) {
                navigationController.onListItemClicked(projectList.getAllList().get(position));
            }

            @Override
            public void onRemoveButtonClick(final int position) {
                removeProject(projects.get(position));
            }

            @Override
            public void onProjectOrderUpdateListener(final Project fromProject, final Project toProject) {
                updateProjectOrder(fromProject, toProject);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void addProjectList() {
        final String text = projectName.getText().toString().trim();

        if (!text.isEmpty()) {
            final Project project = new Project();

            project.setName(text);
            project.setDescription(getString(R.string.Description));
            projectList.add(project);
            final ProjectListService projectListService = new ProjectListService(getString(R.string.base_url), token);

            projectListService.create(project, new AuthenticationService.ApiResponseCallBack() {
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
            projectName.getText().clear();
        }
    }

    private void getUserDetails() {
        final AuthenticationService authenticationService = new AuthenticationService(getString(R.string.base_url), token);

        authenticationService.getUserDetail(new AuthenticationService.ApiResponseCallBack() {
            @Override
            public void onSuccess(final String response) {
                setUserDetails(response);
            }

            @Override
            public void onError(final String errorMessage) {
                showSnackBar(errorMessage);
            }
        });
    }

    private void setUserDetails(final String response) {
        try {
            final JSONObject jsonObject = new JSONObject(response);
            final JSONObject data = jsonObject.getJSONObject(getString(R.string.data));

            userProfile.setId(data.getString(getString(R.string.id)));
            userProfile.setUserName(data.getString(getString(R.string.Name)));
            userProfile.setTitle(data.getString(getString(R.string.Title)));
            userProfile.setEmail(data.getString(getString(R.string.Email)));
            userName.setText(userProfile.getUserName());
            userTitle.setText(userProfile.getTitle());
            profileIcon.setText(userProfile.getProfileIcon());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadProjectFromDB(){
        final ProjectListService projectListService = new ProjectListService(getString(R.string.base_url), token);

        projectListService.getAll(new AuthenticationService.ApiResponseCallBack() {
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

            for (int i = 0; i < data.length(); i++) {
                final JSONObject jsonObjects = data.getJSONObject(i);
                final JSONObject additionalAttributes = jsonObjects.getJSONObject(getString(R.string.additional_attributes));

                if (userProfile.getId().equals(additionalAttributes.getString(getString(R.string.created)))) {
                    final Project project = new Project();

                    project.setId(jsonObjects.getString(getString(R.string.id)));
                    project.setName(jsonObjects.getString(getString(R.string.Name)));
                    project.setOrder((long) jsonObjects.getInt(getString(R.string.sort_order)));
                    projects.add(project);
                }
            }
            projects.sort(Comparator.comparingLong(Project::getOrder));
        } catch (JSONException exception) {
            throw new RuntimeException(exception);
        }
        return projects;
    }

    private void removeProject(final Project project) {
        final ProjectListService projectListService = new ProjectListService(getString(R.string.base_url), token);

        projectListService.delete(project.getId(), new AuthenticationService.ApiResponseCallBack() {
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

    private void updateProjectOrder(final Project fromProject, final Project toProject) {
        final ProjectListService projectListService = new ProjectListService(getString(R.string.base_url), token);

        projectListService.updateOrder(fromProject, new AuthenticationService.ApiResponseCallBack() {
            @Override
            public void onSuccess(final String responseBody) {}

            @Override
            public void onError(final String errorMessage) {
                showSnackBar(errorMessage);
            }
        });
        projectListService.updateOrder(toProject, new AuthenticationService.ApiResponseCallBack() {
            @Override
            public void onSuccess(final String responseBody) {}

            @Override
            public void onError(final String errorMessage) {
                showSnackBar(errorMessage);
            }
        });
    }

    private void startSignInActivity() {
        final Intent intent = new Intent(NavigationActivity.this, SignInActivity.class);

        startActivity(intent);
    }

    private void startUserProfileActivity() {
        final Intent intent = new Intent(NavigationActivity.this, UserProfileActivity.class);

        intent.putExtra(getString(R.string.token), token);
        startActivityIfNeeded(intent, REQUEST_CODE);
    }

    public void startTodoActivity(final Project project) {
        final Intent intent = new Intent(NavigationActivity.this, TodoListActivity.class);

        intent.putExtra(getString(R.string.project_id), project.getId());
        intent.putExtra(getString(R.string.project_name), project.getName());
        intent.putExtra(getString(R.string.token), token);
        startActivity(intent);
    }

    public void toggleEditTextVisibility() {
        final int visibility = projectName.getVisibility() == View.GONE ? View.VISIBLE : View.GONE;
        projectName.setVisibility(visibility);
        addProject.setVisibility(visibility);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            final UserProfile userProfile = new UserProfile();

            userProfile.setUserName(intent.getStringExtra(getString(R.string.user)));
            userProfile.setTitle(intent.getStringExtra(getString(R.string.user_title)));
            userName.setText(userProfile.getUserName());
            userTitle.setText(userProfile.getTitle());
            profileIcon.setText(userProfile.getProfileIcon());
        }
    }

    private void showSnackBar(final String message) {
        final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT);

        snackbar.show();
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