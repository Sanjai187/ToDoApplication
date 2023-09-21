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

import com.example.todo.controller.SearchController;
import com.example.todo.dao.ItemDao;
import com.example.todo.dao.impl.ItemDaoImpl;
import com.example.todo.model.Filter;
import com.example.todo.model.Query;
import com.example.todo.model.Todo;
import com.example.todo.model.TodoList;
import com.example.todo.service.SearchService;
import com.example.todo.todoadapter.ItemTouchHelperCallBack;
import com.example.todo.todoadapter.OnItemClickListener;
import com.example.todo.todoadapter.TodoAdapter;

import java.util.ArrayList;
import java.util.Collections;
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
    private ItemDao itemDao;
    private Long projectId;
    private TodoAdapter todoAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initializeData();
        initializeViews();
        initRecyclerView();
        initializeListeners();
        loadTodoItemsFromDB(projectId);
    }

    private void initializeData() {
        projectId = getIntent().getLongExtra(getString(R.string.project_id), 0L);
        final String selectedList = getIntent().getStringExtra(getString(R.string.search_view));
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
        itemDao = new ItemDaoImpl(this);
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
            public void onCheckBoxClick(final com.example.todo.model.Todo todo) {
                final int position = todoItems.indexOf(todo);

                if (-1 != position) {
                    final com.example.todo.model.Todo updatedItem = todoItems.get(position);

                    updatedItem.setStatus(updatedItem.getStatus() == com.example.todo.model.Todo.Status.COMPLETED
                            ? com.example.todo.model.Todo.Status.NOT_COMPLETED
                            : com.example.todo.model.Todo.Status.COMPLETED);
                }
                itemDao.onUpdateStatus(todo);
                todoAdapter.notifyItemChanged(position);
            }

            @Override
            public void onCloseIconClick(final com.example.todo.model.Todo todo) {
                final int position = todoItems.indexOf(todo);

                if (-1 != position) {
                    final com.example.todo.model.Todo removedItem = todoItems.remove(position);

                    todoList.remove(Long.valueOf(todo.getId()));
                    itemDao.onDelete(Long.valueOf(removedItem.getId()));
                    todoAdapter.notifyItemRemoved(position);
                    updatePageNumber();
                }
            }

            @Override
            public void onItemOrderUpdateListener(Todo fromItem, Todo toItem) {

            }
        });
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
        todoItems = itemDao.getTodoItems(projectId);
        final List<Todo> searchAllItems = new ArrayList<>();

        for (final Todo todo : todoList.getAllList()) {

            if (todo.getName().toLowerCase().contains(newText.toLowerCase())) {
                searchAllItems.add(todo);
            }
        }
        todoItems = searchAllItems;
        currentPage = 1;

        todoAdapter.clearProjects();
        todoAdapter.addProjects(todoItems);
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

                filter.setAttribute("Status");
                switch (position) {
                    case 0: {
                        todoItems = itemDao.getTodoItems(projectId);
                        final List<Todo> allItem = new ArrayList<>();

                        for (final Todo todo : todoList.getAllList()) {
                            filter.setValues(Collections.singletonList("All"));
                            query.setFilter(filter);
                            allItem.add(todo);
                        }
                        todoItems = allItem;
                        currentPage = 1;

                        todoAdapter.clearProjects();
                        todoAdapter.addProjects(todoItems);
                        break;
                    }
                    case 1: {
                        final List<Todo> completedItem = new ArrayList<>();

                        for (final Todo todo : todoList.getAllList()) {

                            if (todo.getStatus() == Todo.Status.COMPLETED) {
                                filter.setValues(Collections.singletonList("Completed"));
                                query.setFilter(filter);
                                completedItem.add(todo);
                            }
                        }
                        todoItems = completedItem;
                        currentPage = 1;

                        todoAdapter.clearProjects();
                        todoAdapter.addProjects(todoItems);
                        break;
                    }
                    case 2: {
                        final List<Todo> notCompletedItem = new ArrayList<>();

                        for (final Todo todo : todoList.getAllList()) {

                            if (todo.getStatus() == Todo.Status.NOT_COMPLETED) {
                                filter.setValues(Collections.singletonList("Not Completed"));
                                query.setFilter(filter);
                                notCompletedItem.add(todo);
                            }
                        }
                        todoItems = notCompletedItem;
                        currentPage = 1;

                        todoAdapter.clearProjects();
                        todoAdapter.addProjects(todoItems);
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

    private void loadTodoItemsFromDB(final Long selectedProjectId) {
        todoItems = itemDao.getTodoItems(selectedProjectId);

        if (null != todoItems) {
            todoAdapter.clearProjects();
            todoAdapter.addProjects(todoItems);
        }
        todoAdapter.updateTodoItems(todoItems);
    }

    @SuppressLint("DefaultLocale")
    private void updatePageNumber() {
        final int totalPage = (int) Math.ceil((double) todoItems.size() / pageSize);

        pageNumber.setText(String.format("%d / %d", currentPage, totalPage));
    }

    private void updateRecyclerView() {
        int startIndex = (currentPage - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, todoItems.size());

        List<com.example.todo.model.Todo> pageItems = todoItems.subList(startIndex, endIndex);
        todoAdapter.updateTodoItems(pageItems);
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