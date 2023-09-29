package com.example.todo.service.impl;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.R;
import com.example.todo.api.impl.AuthenticationService;
import com.example.todo.api.impl.TodoItemService;
import com.example.todo.controller.TodoController;
import com.example.todo.model.Todo;
import com.example.todo.model.TodoList;
import com.example.todo.service.TodoService;
import com.example.todo.todoadapter.ItemTouchHelperCallBack;
import com.example.todo.todoadapter.OnItemClickListener;
import com.example.todo.todoadapter.TodoAdapter;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TodoListActivity extends AppCompatActivity implements TodoService{

    private RecyclerView recyclerView;
    private TodoAdapter todoAdapter;
    private TodoController todoController;
    private TodoList todoList;
    private EditText editText;
    private Button addButton;
    private ImageView backButton;
    private ImageView search;
    private ImageView addList;
    private ImageView previous;
    private ImageView next;
    private Spinner filterSpinner;
    private String projectId;
    private String selectedList;
    private  int currentPage = 1;
    private List<Todo> todoItems;
    private TextView pageNumber;
    private int pageSize;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list);

        initializeData();
        initializeViews();
        initRecyclerView();
    }

    private void initializeData() {
        selectedList = getIntent().getStringExtra(getString(R.string.project_name));
        projectId = getIntent().getStringExtra(getString(R.string.project_id));
        token = getIntent().getStringExtra(getString(R.string.token));
        todoList = new TodoList();
        todoItems = todoList.getAllList();

        if (selectedList != null) {
            final TextView textView = findViewById(R.id.textView);

            textView.setText(selectedList);
        }
    }

    private void initializeViews() {
        todoController = new TodoController(this);
        backButton = findViewById(R.id.backButton1);
        search = findViewById(R.id.search);
        addList = findViewById(R.id.addButton);
        addButton = findViewById(R.id.button);
        recyclerView = findViewById(R.id.recyclerView);
        editText = findViewById(R.id.editText);
        filterSpinner = findViewById(R.id.filter);
        pageNumber = findViewById(R.id.pageCount);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        todoAdapter = new TodoAdapter(todoItems);

        recyclerView.setAdapter(todoAdapter);
        final ItemTouchHelper.Callback callback = new ItemTouchHelperCallBack(todoAdapter);
        final ItemTouchHelper touchHelper = new ItemTouchHelper(callback);

        touchHelper.attachToRecyclerView(recyclerView);
        loadTodoItemsFromDB();
        pageSize = Integer.parseInt(filterSpinner.getSelectedItem().toString());
        backButton.setOnClickListener(view -> onBackPressed());
        addButton.setOnClickListener(view -> todoController.onAddItem());
        search.setOnClickListener(view -> todoController.goToSearchActivity());
        addList.setOnClickListener(view -> todoController.onClickAddVisibility());
        next.setOnClickListener(view -> todoController.onClickNextPage());
        previous.setOnClickListener(view -> todoController.onclickPreviousPage());
        todoController.setupFilterSpinner();
        todoAdapter.setOnClickListener(new OnItemClickListener() {
            @Override
            public void onCheckBoxClick(final Todo todo) {
                updateItemStatus(todo);
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onCloseIconClick(final Todo todo) {
                final int getTotalPages = (int) Math.ceil((double) todoItems.size() / pageSize);

                removeTodoItem(todo);
                todoItems.remove(todo);
                todoAdapter.notifyDataSetChanged();

                if (currentPage > getTotalPages) {
                    currentPage = getTotalPages;
                }
                updateRecyclerView();
                updatePageNumber();
            }

            @Override
            public void onItemOrderUpdateListener(final Todo fromItem, final Todo toItem) {
                updateItemOrder(fromItem, toItem);
            }
        });
        TypeFaceUtil.applyTypefaceToView(getWindow().getDecorView().findViewById(android.R.id.content));
        TypeFaceUtil.applyTextSizeToView(getWindow().getDecorView().findViewById(android.R.id.content));
        applyColorToComponent();
    }

    /**
     * <p>
     * Add a new item to the todo list
     * </p>
     */
    @SuppressLint("NotifyDataSetChanged")
    public void onAddTodoItem() {
        final String text = editText.getText().toString().trim();

        if (!text.isEmpty()) {
            final Todo todo = new Todo(text);

            todo.setParentId(projectId);
            todo.setName(text);
            todo.setStatus(Todo.Status.NOT_COMPLETED);
            todo.setOrder((long) (todoAdapter.getItemCount() + 1));
            final TodoItemService todoItemService = new TodoItemService(getString(R.string.base_url), token);

            todoItemService.create(todo.getName(), projectId, new AuthenticationService.ApiResponseCallBack() {
                @Override
                public void onSuccess(final String responseBody) {
                    showSnackBar(getString(R.string.todo_item_created));
                    todoList.add(todo);
                    todoItems = todoList.getAllList();

                    pageNumber.setVisibility(View.VISIBLE);
                    previous.setVisibility(View.VISIBLE);
                    next.setVisibility(View.VISIBLE);
                    todoAdapter.addTodoItem(todoItems);
                }

                @Override
                public void onError(final String errorMessage) {
                    showSnackBar(errorMessage);
                }
            });
            updateRecyclerView();
            updatePageNumber();
            editText.getText().clear();
        }
    }

    private void loadTodoItemsFromDB() {
        final TodoItemService todoItemService = new TodoItemService(getString(R.string.base_url), token);

        todoItemService.getAll(new AuthenticationService.ApiResponseCallBack() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(final String responseBody) {
                todoItems = todoItemsFromDB(responseBody);

                if (! todoItems.isEmpty()) {
                    todoList.setAllItems(todoItems);
                    updateRecyclerView();
                    todoAdapter.notifyDataSetChanged();
                    updatePageNumber();
                } else {
                    pageNumber.setVisibility(View.GONE);
                    previous.setVisibility(View.GONE);
                    next.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(final String errorMessage) {
                showSnackBar(errorMessage);
            }
        });
    }

    private List<Todo> todoItemsFromDB(final String responseBody) {
        final List<Todo> todoList = new ArrayList<>();

        try {
            final JSONObject responseJson = new JSONObject(responseBody);
            final JSONArray data = responseJson.getJSONArray(getString(R.string.data));

            for (int i = 0; i < data.length(); i++) {
                final JSONObject jsonObject = data.getJSONObject(i);

                if (null != projectId && projectId.equals(jsonObject.getString(getString(R.string.project_id)))) {
                    final Todo todoItem = new Todo(jsonObject.getString(getString(R.string.Name)));

                    todoItem.setId(jsonObject.getString(getString(R.string.id)));
                    todoItem.setParentId(projectId);
                    todoItem.setOrder((long) jsonObject.getInt(getString(R.string.sort_order)));
                    todoList.add(todoItem);
                }
            }
            todoList.sort(Comparator.comparingLong(Todo::getOrder));
        } catch (JSONException exception) {
            throw new RuntimeException(exception);
        }

        return todoList;
    }

    private void updateItemOrder(final Todo fromItem, final Todo toItem) {
        final TodoItemService todoItemService = new TodoItemService(getString(R.string.base_url), token);

        todoItemService.updateOrder(fromItem, new AuthenticationService.ApiResponseCallBack() {
            @Override
            public void onSuccess(final String responseBody) {}

            @Override
            public void onError(final String errorMessage) {
                showSnackBar(errorMessage);
            }
        });
        todoItemService.updateOrder(toItem, new AuthenticationService.ApiResponseCallBack() {
            @Override
            public void onSuccess(final String responseBody) {}

            @Override
            public void onError(final String errorMessage) {
                showSnackBar(errorMessage);
            }
        });
    }

    private void updateItemStatus(final Todo todo) {
        final TodoItemService todoItemService = new TodoItemService(getString(R.string.base_url), token);

        todoItemService.updateStatus(todo, new AuthenticationService.ApiResponseCallBack() {
            @Override
            public void onSuccess(final String responseBody) {}

            @Override
            public void onError(final String errorMessage) {
                showSnackBar(errorMessage);
            }
        });
    }

    private void removeTodoItem(final Todo todoItem) {
        final TodoItemService todoItemService = new TodoItemService(getString(R.string.base_url), token);

        todoItemService.delete(todoItem.getId(), new AuthenticationService.ApiResponseCallBack() {
            @Override
            public void onSuccess(final String response) {
                showSnackBar(getString(R.string.removed_project));
            }

            @Override
            public void onError(final String errorMessage) {
                showSnackBar(errorMessage);
            }
        });
    }

    @Override
    public void setupFilterSpinner() {
        final ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.page_filter, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(spinnerAdapter);
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int i, final long id) {
                pageSize = Integer.parseInt(parent.getItemAtPosition(i).toString());
                final int getTotalPages = (int) Math.ceil((double) todoItems.size() / pageSize);

                if (currentPage > getTotalPages) {
                    currentPage = getTotalPages;
                }
                updateRecyclerView();
                updatePageNumber();
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void startSearchActivity() {
        final Intent intent = new Intent(TodoListActivity.this, SearchActivity.class);

        intent.putExtra(getString(R.string.project_id), projectId);
        intent.putExtra(getString(R.string.search_view), selectedList);
        intent.putExtra(getString(R.string.token), token);
        startActivity(intent);
    }

    @Override
    public void toggleAddListVisibility() {
        final int visibility = editText.getVisibility() == View.GONE ? View.VISIBLE : View.GONE;
        editText.setVisibility(visibility);
        addButton.setVisibility(visibility);
        filterSpinner.setVisibility(visibility);
    }

    @Override
    public void navigateToNextPage() {
        if ((currentPage * pageSize) < todoItems.size()) {
            currentPage++;
            loadTodoItemsFromDB();
            updateRecyclerView();
            updatePageNumber();
        }
    }

    @Override
    public void navigateToPreviousPage() {
        if (currentPage > 1) {
            currentPage--;
            loadTodoItemsFromDB();
            updateRecyclerView();
            updatePageNumber();
        }
    }

    @SuppressLint("DefaultLocale")
    private void updatePageNumber() {
        final int totalPage = (int) Math.ceil((double) todoItems.size() / pageSize);

        pageNumber.setText(String.format("%d / %d", currentPage, totalPage));
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateRecyclerView() {
        int startIndex = (currentPage - 1) * pageSize;
        final int endIndex = Math.min(startIndex + pageSize, todoItems.size());

        if (0 > startIndex) {
            startIndex = 0;
        }
        final List<Todo> pageItems = todoItems.subList(startIndex, endIndex);
        todoAdapter.updateTodoItems(pageItems);
        todoAdapter.notifyDataSetChanged();
    }

    private void showSnackBar(final String message) {
        final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT);

        snackbar.show();
    }

    private void applyColorToComponent() {
        final int defaultColor = TypeFaceUtil.getSelectedDefaultColor();
        final RelativeLayout layout = findViewById(R.id.subList);
        final RelativeLayout relativeLayout = findViewById(R.id.pagination);

        if (defaultColor == R.color.green) {
            layout.setBackgroundColor(getResources().getColor(R.color.green));
            relativeLayout.setBackgroundColor(getResources().getColor(R.color.green));
        } else if (defaultColor == R.color.blue) {
            layout.setBackgroundColor(getResources().getColor(R.color.blue));
            relativeLayout.setBackgroundColor(getResources().getColor(R.color.blue));
        } else if (defaultColor == R.color.Violet) {
            layout.setBackgroundColor(getResources().getColor(R.color.Violet));
            relativeLayout.setBackgroundColor(getResources().getColor(R.color.Violet));
        }
    }
}