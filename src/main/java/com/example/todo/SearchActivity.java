package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.todo.api.AuthenticationService;
import com.example.todo.api.TodoItemService;
import com.example.todo.controller.SearchController;
import com.example.todo.model.Filter;
import com.example.todo.model.Query;
import com.example.todo.model.Todo;
import com.example.todo.model.TodoList;
import com.example.todo.service.SearchService;
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

public class SearchActivity extends AppCompatActivity implements SearchService {

    private SearchController searchController;
    private TodoList todoList;
    private ImageView backButton;
    private SearchView searchView;
    private Spinner spinner;
    private Spinner filterSpinner;
    private List<Todo> todoItems;
    private int currentPage = 1;
    private TextView pageNumber;
    private ImageView previous;
    private ImageView next;
    private int pageSize = 5;
    private Query query;
    private String projectId;
    private TodoAdapter todoAdapter;
    private String token;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initializeData();
        initializeViews();
        initRecyclerView();
        initializeListeners();
    }

    private void initializeData() {
        final String selectedList = getIntent().getStringExtra(getString(R.string.search_view));
        projectId = getIntent().getStringExtra(getString(R.string.project_id));
        token = getIntent().getStringExtra(getString(R.string.token));
        todoList = new TodoList();
        todoItems = todoList.getAllList();
        query = todoList.getQuery();

        if (selectedList != null) {
            TextView textView = findViewById(R.id.textView);
            textView.setText(selectedList);
        }
    }

    private void initializeViews() {
        searchController = new SearchController(this);
        backButton = findViewById(R.id.backButton1);
        searchView = findViewById(R.id.searchbar);
        spinner = findViewById(R.id.statusbutton);
        filterSpinner = findViewById(R.id.filter);
        pageNumber = findViewById(R.id.pageCount);
        previous = findViewById(R.id.prev_page);
        next = findViewById(R.id.next_page);
    }

    private void initializeListeners() {
        backButton.setOnClickListener(view -> onBackPressed());
        next.setOnClickListener(view -> navigateToNextPage());
        previous.setOnClickListener(view -> navigateToPreviousPage());

        searchController.onClickSpinner();
        searchController.onClickSearchView();
        searchController.onClickFilterSpinner();
        loadTodoItemsFromDB();
        updatePageNumber();
        TypeFaceUtil.applyFontToView(getWindow().getDecorView().findViewById(android.R.id.content));
        TypeFaceUtil.applyTextSizeToView(getWindow().getDecorView().findViewById(android.R.id.content));
        applyColorToComponent();
    }

    private void initRecyclerView() {
        final RecyclerView recyclerView = findViewById(R.id.recyclerViewList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        todoAdapter = new TodoAdapter(todoItems);

        recyclerView.setAdapter(todoAdapter);
        final ItemTouchHelper.Callback callback = new ItemTouchHelperCallBack(todoAdapter);
        final ItemTouchHelper touchHelper = new ItemTouchHelper(callback);

        touchHelper.attachToRecyclerView(recyclerView);
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
            public void onSuccess(final String responseBody) {
                showSnackBar(getString(R.string.removed_project));
            }

            @Override
            public void onError(final String errorMessage) {
                showSnackBar(errorMessage);
            }
        });
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

//                if (null != projectId && projectId.equals(jsonObject.getString(getString(R.string.project_id)))) {
                    final Todo todoItem = new Todo(jsonObject.getString(getString(R.string.Name)));

                    todoItem.setId(jsonObject.getString(getString(R.string.id)));
                    todoItem.setParentId(projectId);
                    todoItem.setOrder((long) jsonObject.getInt(getString(R.string.sort_order)));
                    todoList.add(todoItem);
                }
//            }
            todoList.sort(Comparator.comparingLong(Todo::getOrder));
        } catch (JSONException exception) {
            throw new RuntimeException(exception);
        }

        return todoList;
    }

    private void showSnackBar(final String message) {
        final View view = findViewById(android.R.id.content);
        final Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);

        snackbar.show();
    }

    @Override
    public void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                filterTableLayout(query.toLowerCase());
                return true;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                filterTableLayout(newText.toLowerCase());
                return true;
            }
        });
    }

    /**
     * <p>
     * Filter the table based on the entered text
     * </p>
     *
     * @param newText representing filter the table based on the entered new text
     */
    private void filterTableLayout(final String newText) {
        query.setSearch(newText);
        final List<Todo> searchAllItems = new ArrayList<>();

        for (final Todo todo : todoList.getAllList()) {

            if (todo.getName().toLowerCase().contains(newText.toLowerCase())) {
                searchAllItems.add(todo);
            }
        }
        todoItems = searchAllItems;
        currentPage = 1;

        todoAdapter.clearProjects();
        todoAdapter.addTodoItem(todoItems);
    }

    @Override
    public void setupSpinner () {
        final ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.filter_options, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> adapterView, final View view, final int position, final long l) {
                final Filter filter = new Filter();

                filter.setAttribute(getString(R.string.status));
                switch (position) {
                    case 0: {
                        final List<Todo> allItem = new ArrayList<>();

                        for (final Todo todo : todoList.getAllList()) {
                            filter.setValues(Collections.singletonList(getString(R.string.all)));
                            query.setFilter(filter);
                            allItem.add(todo);
                        }
                        todoItems = allItem;
                        currentPage = 1;

                        todoAdapter.clearProjects();
                        todoAdapter.addTodoItem(todoItems);
                        break;
                    }
                    case 1: {
                        final List<Todo> completedItem = new ArrayList<>();

                        for (final Todo todo : todoList.getAllList()) {

                            if (todo.getStatus() == Todo.Status.COMPLETED) {
                                filter.setValues(Collections.singletonList(getString(R.string.completed)));
                                query.setFilter(filter);
                                completedItem.add(todo);
                            }
                        }
                        todoItems = completedItem;
                        currentPage = 1;

                        todoAdapter.clearProjects();
                        todoAdapter.addTodoItem(todoItems);
                        break;
                    }
                    case 2: {
                        final List<Todo> notCompletedItem = new ArrayList<>();

                        for (final Todo todo : todoList.getAllList()) {

                            if (todo.getStatus() == Todo.Status.NOT_COMPLETED) {
                                filter.setValues(Collections.singletonList(getString(R.string.not_completed)));
                                query.setFilter(filter);
                                notCompletedItem.add(todo);
                            }
                        }
                        todoItems = notCompletedItem;
                        currentPage = 1;

                        todoAdapter.clearProjects();
                        todoAdapter.addTodoItem(todoItems);
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(final AdapterView<?> adapterView) {
            }
        });
    }

    @Override
    public void setupFilterSpinner() {
        final ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.page_filter,  android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(spinnerAdapter);
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view,
                                       final int i, final long id) {
                pageSize = Integer.parseInt(parent.getItemAtPosition(i).toString());
                final int getTotalPages = (int) Math.ceil((double) todoItems.size() / pageSize);

                if (currentPage > getTotalPages) {
                    currentPage = getTotalPages;
                }
                updateRecyclerView();
                updatePageNumber();
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {}
        });
    }

    private void navigateToNextPage() {
        if ((currentPage * pageSize) < todoItems.size()) {
            currentPage++;
            updateRecyclerView();
            updatePageNumber();
        }
    }

    private void navigateToPreviousPage() {
        if (currentPage > 1) {
            currentPage--;
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
        final int startIndex = (currentPage - 1) * pageSize;
        final int endIndex = Math.min(startIndex + pageSize, todoItems.size());
        final List<Todo> pageItems = todoItems.subList(startIndex, endIndex);

        todoAdapter.updateTodoItems(pageItems);
        todoAdapter.notifyDataSetChanged();
    }

    private void applyColorToComponent() {
        final int defaultColor = TypeFaceUtil.getSelectedDefaultColor();
        final RelativeLayout layout = findViewById(R.id.searchView);
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