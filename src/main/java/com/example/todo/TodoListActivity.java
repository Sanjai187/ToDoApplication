package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.example.todo.api.AuthenticationService;
import com.example.todo.api.TodoItemService;
import com.example.todo.controller.TodoController;
import com.example.todo.dao.ItemDao;
import com.example.todo.dao.impl.ItemDaoImpl;
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
import java.util.Collections;
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
    private String selectedList;
    private String projectId;
    private Long id = 0L;
    private List<com.example.todo.model.Todo> todoItems;
    private int currentPage = 1;
    private TextView pageNumber;
    private int pageSize = 5;
    private ItemDao itemDao;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list);

        initializeData();
        initializeViews();
        initializeListeners();
    }

    private void initializeData() {
        projectId = getIntent().getStringExtra(getString(R.string.project_id));
        selectedList = getIntent().getStringExtra(getString(R.string.project_name));
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
        itemDao = new ItemDaoImpl(this);
        todoItems = todoList.getAllList();
        backButton = findViewById(R.id.backButton1);
        search = findViewById(R.id.search);
        addList = findViewById(R.id.addButton);
        addButton = findViewById(R.id.button);
        recyclerView = findViewById(R.id.recyclerView);
        editText = findViewById(R.id.editText);
        filterSpinner = findViewById(R.id.filter);
        pageNumber = findViewById(R.id.pageCount);
        previous = findViewById(R.id.prev_page);
        next = findViewById(R.id.next_page);
    }

    private void initializeListeners() {
        backButton = findViewById(R.id.backButton1);
        addButton = findViewById(R.id.button);
        search = findViewById(R.id.search);
        addList = findViewById(R.id.addButton);
        previous = findViewById(R.id.prev_page);
        next = findViewById(R.id.next_page);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        todoAdapter = new TodoAdapter(todoItems);

        recyclerView.setAdapter(todoAdapter);
        final ItemTouchHelper.Callback callback = new ItemTouchHelperCallBack(todoAdapter);
        final ItemTouchHelper touchHelper = new ItemTouchHelper(callback);

        touchHelper.attachToRecyclerView(recyclerView);
        loadTodoItemsFromDataBase();
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
//                final int position = todoItems.indexOf(todo);
//
//                if (-1 != position) {
//                    final Todo updatedItem = todoItems.get(position);
//
//                    updatedItem.setStatus(updatedItem.getStatus() == Todo.Status.COMPLETED ? Todo.Status.NOT_COMPLETED : Todo.Status.COMPLETED);
//                }
//                itemDao.onUpdateStatus(todo);
//                todoAdapter.notifyItemChanged(position);
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
        TypeFaceUtil.applyFontToView(getWindow().getDecorView().findViewById(android.R.id.content));
        TypeFaceUtil.applyTextSizeToView(getWindow().getDecorView().findViewById(android.R.id.content));
        applyColorToComponent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        itemDao.open();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (null != todoItems) {
            for (final Todo todo : todoItems) {
                itemDao.onUpdateStatus(todo);
            }
        }
        itemDao.close();
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
            todo.setStatus(Todo.Status.NOT_COMPLETED);
            todo.setOrder((long) (todoAdapter.getItemCount() + 1));
            final TodoItemService itemService = new TodoItemService(getString(R.string.base_url), token);

            itemService.create(todo.getName(), projectId, new AuthenticationService.ApiResponseCallBack() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onSuccess(final String responseBody) {
                    showSnackBar(getString(R.string.todo_item_created));
                    todoList.add(todo);
                    todoItems = todoList.getAllList();

//                    todoAdapter.clearProjects();
                    todoAdapter.addProjects(todoItems);
                }

                @Override
                public void onError(final String errorMessage) {
                    showSnackBar(errorMessage);
                }
            });
//            updateRecyclerView();
            updatePageNumber();
            editText.getText().clear();
        }
    }

    private void updateItemOrder(final Todo fromItem, final Todo toItem) {
        final TodoItemService itemService = new TodoItemService(getString(R.string.base_url), token);

        itemService.updateOrder(fromItem, new AuthenticationService.ApiResponseCallBack() {
            @Override
            public void onSuccess(final String responseBody) {}

            @Override
            public void onError(final String errorMessage) {
                showSnackBar(errorMessage);
            }
        });
        itemService.updateOrder(toItem, new AuthenticationService.ApiResponseCallBack() {
            @Override
            public void onSuccess(String responseBody) {}

            @Override
            public void onError(String errorMessage) {
                showSnackBar(errorMessage);
            }
        });
    }

    private void removeTodoItem(final Todo todoItem) {
        final TodoItemService itemService = new TodoItemService(getString(R.string.base_url), token);

        itemService.delete(todoItem.getId(), new AuthenticationService.ApiResponseCallBack() {
            @Override
            public void onSuccess(String responseBody) {
                showSnackBar(getString(R.string.removed_project));
            }

            @Override
            public void onError(String errorMessage) {
                showSnackBar(errorMessage);
            }
        });
    }

    private void loadTodoItemsFromDataBase() {
        final TodoItemService itemService = new TodoItemService(getString(R.string.base_url), token);

        itemService.getAll(new AuthenticationService.ApiResponseCallBack() {
            @Override
            public void onSuccess(final String responseBody) {
                todoItems = parseItemsFromJson(responseBody);
                if (! todoItems.isEmpty()) {
                    todoList.setAllItems(todoItems);
                    updateRecyclerView();
                    updatePageNumber();
                } else {
                    pageNumber.setVisibility(View.GONE);
                    next.setVisibility(View.GONE);
                    previous.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(final String errorMessage) {
                showSnackBar(errorMessage);
            }
        });
    }

    private List<Todo> parseItemsFromJson(final String responseBody) {
        final List<Todo> todoItemList = new ArrayList<>();

        try {
            final JSONObject responseJson = new JSONObject(responseBody);
            final JSONArray data = responseJson.getJSONArray(getString(R.string.data));

            for (int i = 0; i < data.length(); i++) {
                final JSONObject projectJson = data.getJSONObject(i);

                if (projectId.equals(projectJson.getString(getString(R.string.id)))) {
                    final Todo todoItem = new Todo(projectJson.getString(
                            getString(R.string.Name)));

                    todoItem.setId(projectJson.getString(getString(R.string.id)));
                    todoItem.setParentId(projectId);
                    todoItem.setOrder((long) projectJson.getInt(getString(R.string.sort_order)));
                    todoItemList.add(todoItem);
                }
            }
            Collections.sort(todoItemList, new Comparator<Todo>() {
                @Override
                public int compare(final Todo item1, final Todo item2) {
                    return Long.compare(item1.getOrder(), item2.getOrder());
                }
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return todoItemList;
    }

    @SuppressLint("ResourceAsColor")
    private void showSnackBar(final String message) {
        final View view = findViewById(android.R.id.content);
        final Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);

        snackbar.setTextColor(getResources().getColor(R.color.black));
        snackbar.setBackgroundTint(R.color.gray);
        snackbar.show();
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
//                updateRecyclerView();
                updatePageNumber();
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void navigateToSearchActivity() {
        final Intent intent = new Intent(TodoListActivity.this, SearchActivity.class);

        intent.putExtra(getString(R.string.search_view), selectedList);
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
            loadTodoItemsFromDataBase();
            updatePageNumber();
        }
    }

    @Override
    public void navigateToPreviousPage() {
        if (currentPage > 1) {
            currentPage--;
            loadTodoItemsFromDataBase();
            updatePageNumber();
        }
    }

    @SuppressLint("DefaultLocale")
    private void updatePageNumber() {
        final int totalPage = (int) Math.ceil((double) todoItems.size() / pageSize);
        pageNumber.setText(String.format("%d / %d", currentPage, totalPage));
    }

    private void updateRecyclerView() {
        final int startIndex = (currentPage - 1) * pageSize;
        final int endIndex = Math.min(startIndex + pageSize, todoItems.size());
        final List<Todo> pageItems = todoItems.subList(startIndex, endIndex);

        todoAdapter.updateTodoItems(pageItems);
//        todoAdapter.clearProjects();
//        todoAdapter.addProjects(pageItems);
    }

//    private void loadTodoItemsFromDatabase(final String selectedProjectId) {
//        todoItems = itemDao.getTodoItems(selectedProjectId);
//
//        if (null != todoItems) {
//            todoAdapter.clearProjects();
//            todoAdapter.addProjects(todoItems);
//        }
//        todoAdapter.updateTodoItems(todoItems);
//    }

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