package com.example.todo.service;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.example.todo.R;
import com.example.todo.model.Filter;
import com.example.todo.model.Todo;
import com.example.todo.model.TodoList;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 * This activity displays a list of todo items for a specific project
 * </p>
 *
 * @author sanjai
 * @version 1.0
 */
public class ChildProject extends AppCompatActivity {

    private TodoList todoList;
    private EditText editText;
    private TableLayout layout;
    private String selectedList;
    private SearchView searchView;
    private Spinner spinner;
    private Spinner fliter;
    private Long projectId;
    private Long id = 0L;
    private List<Todo> todoItems;
    private int currentPage = 1;
    private TextView pageNumber;
    private ImageView previous;
    private ImageView next;
    private int pageSize = 5;

    /**
     * <p>
     * Creation of the child project
     * </p>
     *
     * @param savedInstanceState Refers the saved instance of the state
     */
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_childproject);

        final ImageView backButton = findViewById(R.id.backButton1);
        final Button addButton = findViewById(R.id.button);
        final ImageView search = findViewById(R.id.search);
        final ImageView addList = findViewById(R.id.addButton);
        layout = findViewById(R.id.tableLayout);
        editText = findViewById(R.id.editText);
        searchView = findViewById(R.id.searchbar);
        spinner = findViewById(R.id.statusbutton);
        fliter = findViewById(R.id.filter);
        pageNumber = findViewById(R.id.pageCount);
        previous = findViewById(R.id.prev_page);
        next = findViewById(R.id.next_page);

        initializeData();
        backButton.setOnClickListener(view -> onBackPressed());
        addButton.setOnClickListener(view -> addItem());
        search.setOnClickListener(view -> toggleSearchView());
        addList.setOnClickListener(view -> {
            if (editText.getVisibility() == View.GONE) {
                editText.setVisibility(View.VISIBLE);
                addButton.setVisibility(View.VISIBLE);
            } else {
                editText.setVisibility(View.GONE);
                addButton.setVisibility(View.GONE);
            }
        });
        setupSpinner();
        setupSearchView();
        filterPage();
        updatePageNumber(pageNumber);
        loadTodoList(selectedList);
    }

    private void initializeData() {
        projectId = getIntent().getLongExtra(getString(R.string.project_id), 0L);
        selectedList = getIntent().getStringExtra(getString(R.string.project_name));
        todoList = new TodoList();
        todoItems = todoList.getAllList();

        if (selectedList != null) {
            final TextView textView = findViewById(R.id.textView);

            textView.setText(selectedList);
        }
    }

    /**
     * <p>
     * Toggle the visibility of the search view and spinner
     * </p>
     */
    private void toggleSearchView() {
        if (searchView.getVisibility() == View.GONE) {
            searchView.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.VISIBLE);
            fliter.setVisibility(View.VISIBLE);
        } else {
            searchView.setVisibility(View.GONE);
            spinner.setVisibility(View.GONE);
            fliter.setVisibility(View.GONE);
        }
    }

    /**
     * <p>
     * Add a new item to the todo list
     * </p>
     */
    private void addItem() {
        final String text = editText.getText().toString().trim();

        if (!text.isEmpty()) {
            final Todo todo = new Todo(text);

            todo.setParentId(projectId);
            todo.setId(++id);
            todo.setStatus("Not completed");
            todoList.add(todo);
            updateTableLayout();
            saveTodoList();
            editText.getText().clear();
        }
    }

    /**
     * <p>
     * Setup the search view with query listeners
     * </p>
     */
    private void setupSearchView() {
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

        for (final Todo todo : todoList.getAllList()) {

            if (todo.getLabel().toLowerCase().contains(newText.toLowerCase())) {
                createTable(todo);
            }
        }
    }

    /**
     * <p>
     * Setup the spinner with filter options
     * </p>
     */
    private void setupSpinner() {
        final ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.filter_options,  android.R.layout.simple_spinner_item);
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
                            createTable(todo);
                        }
                        break;
                    }
                    case 1: {
                        layout.removeAllViews();
                        for (final Todo todo : todoList.getAllList()) {
                            if (todo.isChecked()) {
                                filter.setValues(Collections.singletonList("Completed"));
                                createTable(todo);
                            }
                        }
                        break;
                    }
                    case 2: {
                        layout.removeAllViews();
                        for (final Todo todo : todoList.getAllList()) {
                            if (!todo.isChecked()) {
                                filter.setValues(Collections.singletonList("Not Completed"));
                                createTable(todo);
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

    private void filterPage() {
        final ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.page_filter,  android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fliter.setAdapter(spinnerAdapter);
        fliter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view,
                                       final int i, final long id) {
                pageSize = Integer.parseInt(parent.getItemAtPosition(i).toString());

                updateTableLayout();
                updatePageNumber(pageNumber);
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {}
        });

        next.setOnClickListener(view -> {
            if ((currentPage * pageSize) < todoItems.size()) {
                currentPage++;
                updateTableLayout();
                updatePageNumber(pageNumber);
            }
        });

        previous.setOnClickListener(view -> {
            if (currentPage > 1) {
                currentPage--;
                updateTableLayout();
                updatePageNumber(pageNumber);
            }
        });
        loadTodoList(selectedList);
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
        viewTable();
        updatePageNumber(pageNumber);
        saveTodoList();
    }

    /**
     * <p>
     * Create a table row for a todo item
     * </p>
     *
     * @param todo representing todo items
     */
    public void createTable(final Todo todo) {
        final TableRow table = new TableRow(this);
        final CheckBox checkBox = new CheckBox(this);
        final TextView textView = new TextView(this);
        final ImageView closeIcon = new ImageView(this);

        table.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
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
        table.addView(checkBox);
        textView.setText(todo.getLabel());
        table.addView(textView);
        closeIcon.setImageResource(R.drawable.close);
        closeIcon.setOnClickListener(view -> removeItem(table, todo));
        table.addView(closeIcon);
        layout.addView(table);
    }

    /**
     * <p>
     * View the child project table
     * </p>
     */
    private void viewTable() {
        layout.removeAllViews();

        for (final Todo todo : todoList.getAllList()) {
            createTable(todo);
            saveTodoList();
            editText.getText().clear();
        }
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
    private void updatePageNumber(final TextView pageNumber) {
        final int totalPage = (int) Math.ceil((double) todoItems.size() / pageSize);

        pageNumber.setText(String.format("%d / %d", currentPage, totalPage));
    }

    private void updateTableLayout() {
        layout.removeAllViews();
        final int startIndex = (currentPage - 1) * pageSize;
        final int endIndex = Math.min(startIndex + pageSize, todoItems.size());

        for (int i = startIndex; i < endIndex; i++) {
            final Todo todo = todoItems.get(i);

            createTable(todo);
        }
    }
}