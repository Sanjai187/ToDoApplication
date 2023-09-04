package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.todo.controller.SearchController;
import com.example.todo.model.Filter;
import com.example.todo.model.Query;
import com.example.todo.model.Todo;
import com.example.todo.model.TodoList;
import com.example.todo.service.SearchService;

import java.util.Collections;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements SearchService {

    private SearchController searchController;
    private TodoList todoList;
    private EditText editText;
    private TableLayout layout;
    private ImageView backButton;
    private String selectedList;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initializeData();
        initializeViews();
        initializeListeners();
        loadTodoList(selectedList);
    }

    private void initializeViews() {
        searchController = new SearchController(this,this);
        layout = findViewById(R.id.tableLayout);
        editText = findViewById(R.id.editText);
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
    }

    private void initializeData() {
        selectedList = getIntent().getStringExtra(getString(R.string.search_view));
        todoList = new TodoList();
        todoItems = todoList.getAllList();
        query = todoList.getQuery();

        if (selectedList != null) {
            TextView textView = findViewById(R.id.textView);
            textView.setText(selectedList);
        }
    }

    @Override
    public void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                filterTableLayout(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                filterTableLayout(newText);
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
        layout.removeAllViews();
        query.setSearch(newText);

        for (final Todo todo : todoList.getAllList()) {

            if (todo.getLabel().toLowerCase().contains(newText.toLowerCase())) {
                createTableRow(todo);
            }
        }
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
                        layout.removeAllViews();

                        for (final Todo todo : todoList.getAllList()) {
                            filter.setValues(Collections.singletonList("All"));
                            query.setFilter(filter);
                            createTableRow(todo);
                        }
                        break;
                    }
                    case 1: {
                        layout.removeAllViews();
                        for (final Todo todo : todoList.getAllList()) {
                            if (todo.isChecked()) {
                                filter.setValues(Collections.singletonList("Completed"));
                                query.setFilter(filter);
                                createTableRow(todo);
                            }
                        }
                        break;
                    }
                    case 2: {
                        layout.removeAllViews();
                        for (final Todo todo : todoList.getAllList()) {
                            if (!todo.isChecked()) {
                                filter.setValues(Collections.singletonList("Not Completed"));
                                query.setFilter(filter);
                                createTableRow(todo);
                            }
                        }
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

                updateTableLayout();
                updatePageNumber();
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {}
        });
        loadTodoList(selectedList);
    }

    private void navigateToNextPage() {
        if ((currentPage * pageSize) < todoItems.size()) {
            currentPage++;
            updateTableLayout();
            updatePageNumber();
        }
    }

    private void navigateToPreviousPage() {
        if (currentPage > 1) {
            currentPage--;
            updateTableLayout();
            updatePageNumber();
        }
    }

    /**
     * <p>
     * Create a table row for a todo item
     * </p>
     *
     * @param todo representing todo items
     */
    public void createTableRow(final Todo todo) {
        layout.removeAllViews();
        final TableRow tableRow = new TableRow(this);
        final CheckBox checkBox = new CheckBox(this);
        final TextView textView = new TextView(this);
        final ImageView closeIcon = new ImageView(this);

        tableRow.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT
        ));

        checkBox.setChecked(getCheckedBoxState(todo.getLabel()));
        checkBox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                textView.setTextColor(Color.RED);
                todo.setChecked();
            } else {
                textView.setTextColor(Color.BLACK);
                todo.setChecked();
            }
            saveCheckedBoxState(todo.getLabel(), isChecked);
        });

        tableRow.addView(checkBox);
        textView.setText(todo.getLabel());
        tableRow.addView(textView);

        closeIcon.setImageResource(R.drawable.close);
        closeIcon.setOnClickListener(view -> removeItem(tableRow, todo));
        tableRow.addView(closeIcon);

        layout.addView(tableRow);
    }

    /**
     * <p>
     * View the child project table
     * </p>
     */
    private void viewTable() {
        layout.removeAllViews();

        for (final Todo todo : todoList.getAllList()) {
            createTableRow(todo);
            saveTodoList();
            editText.getText().clear();
        }
    }

    /**
     * <p>
     * Remove an item from the table layout
     * </p>
     *
     * @param todo representing todo item
     */
    public void removeItem(final TableRow row, final Todo todo) {
        layout.removeView(row);
        todoList.remove(todo.getId());
        final int totalPageCount = (int) Math.ceil((double) todoItems.size()/ pageSize);

        if (currentPage > totalPageCount) {
            currentPage = totalPageCount;
        }
        updatePageNumber();
        saveTodoList();
    }

    /**
     * <p>
     * Save the check box state to shared preferences
     * </p>
     *
     * @param label representing list name
     * @param isChecked representing list is checked or not checked
     */
    private void saveCheckedBoxState(final String label, final boolean isChecked) {
        final SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference), MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(label, isChecked);
        editor.apply();
    }

    /**
     * <p>
     * Get the check box state from shared preferences
     * </p>
     *
     * @param label representing list name
     */
    private boolean getCheckedBoxState(final String label) {
        final SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference), MODE_PRIVATE);

        return sharedPreferences.getBoolean(label, false);
    }

    /**
     * <p>
     * Load saved items associated with a specific list name from shared preferences
     * </p>
     *
     * @param listName Refers name of the todo list from which to load items
     */
    private void loadTodoList(final String listName) {
        final SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference), MODE_PRIVATE);
        final String savedTodoItems = sharedPreferences.getString(listName, getString(R.string.space));
        final String[] todoItems = savedTodoItems.split(getString(R.string.kama));

        for (final String todoItem : todoItems) {
            if (!todoItem.isEmpty()) {
                final Todo todo = new Todo(todoItem);

                todoList.add(todo);
            }
        }
        viewTable();
    }

    /**
     * <p>
     * Saved the list of Items on shared preferences
     * </p>
     */
    private void saveTodoList() {
        final SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference), MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        final StringBuilder todoItems = new StringBuilder();

        for (final Todo todo : todoList.getAllList()) {
            todoItems.append(todo.getLabel()).append(getString(R.string.kama));
        }
        editor.putString(selectedList, todoItems.toString());
        editor.apply();
    }

    @SuppressLint("DefaultLocale")
    private void updatePageNumber() {
        final int totalPage = (int) Math.ceil((double) todoItems.size() / pageSize);

        pageNumber.setText(String.format("%d / %d", currentPage, totalPage));
    }

    private void updateTableLayout() {
        layout.removeAllViews();
        final int startIndex = (currentPage - 1) * pageSize;
        final int endIndex = Math.min(startIndex + pageSize, todoItems.size());

        for (int i = startIndex; i < endIndex; i++) {
            final Todo todo = todoItems.get(i);

            createTableRow(todo);
        }
    }
}