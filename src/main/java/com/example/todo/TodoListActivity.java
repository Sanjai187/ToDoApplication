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

import com.example.todo.controller.TodoController;
import com.example.todo.dao.ItemDao;
import com.example.todo.dao.impl.ItemDaoImpl;
import com.example.todo.model.Todo;
import com.example.todo.model.TodoList;
import com.example.todo.service.TodoService;
import com.example.todo.todoadapter.ItemTouchHelperCallBack;
import com.example.todo.todoadapter.OnItemClickListener;
import com.example.todo.todoadapter.TodoAdapter;

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
    private Long projectId;
    private Long id = 0L;
    private List<com.example.todo.model.Todo> todoItems;
    private int currentPage = 1;
    private TextView pageNumber;
    private int pageSize = 5;
    private ItemDao itemDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list);

        initializeData();
        initializeViews();
        initializeListeners();
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

    private void initializeViews() {
        todoController = new TodoController(this, this);
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
        todoAdapter = new TodoAdapter(todoItems, itemDao);

        recyclerView.setAdapter(todoAdapter);
        final ItemTouchHelper.Callback callback = new ItemTouchHelperCallBack(todoAdapter);
        final ItemTouchHelper touchHelper = new ItemTouchHelper(callback);

        touchHelper.attachToRecyclerView(recyclerView);
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
                final int position = todoItems.indexOf(todo);

                if (-1 != position) {
                    final Todo updatedItem = todoItems.get(position);

                    updatedItem.setStatus(updatedItem.getStatus() == Todo.Status.COMPLETED ? Todo.Status.NOT_COMPLETED : Todo.Status.COMPLETED);
                }
                itemDao.onUpdateStatus(todo);
                todoAdapter.notifyItemChanged(position);
            }

            @Override
            public void onCloseIconClick(final Todo todo) {
                final int position = todoItems.indexOf(todo);

                if (-1 != position) {
                    final Todo removedItem = todoItems.remove(position);

                    todoList.remove(todo.getId());
                    itemDao.onDelete(removedItem.getId());
                    todoAdapter.notifyItemRemoved(position);
                    updatePageNumber();
                }
            }
        });
        loadTodoItemsFromDatabase(projectId);
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
    public void onAddItem() {
        final String text = editText.getText().toString().trim();

        if (!text.isEmpty()) {
            final Todo todo = new Todo(text);

            todo.setParentId(projectId);
            todo.setId(++id);
            todo.setStatus(Todo.Status.NOT_COMPLETED);
            todo.setOrder((long) todoAdapter.getItemCount() + 1);
            todoList.add(todo);
            itemDao.insert(todo);
            todoItems = todoList.getAllList();

            todoAdapter.notifyDataSetChanged();
            updateRecyclerView();
            updatePageNumber();
            editText.getText().clear();
        }
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
            updateRecyclerView();
            updatePageNumber();
        }
    }

    @Override
    public void navigateToPreviousPage() {
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

    private void updateRecyclerView() {
        final int startIndex = (currentPage - 1) * pageSize;
        final int endIndex = Math.min(startIndex + pageSize, todoItems.size());
        final List<Todo> pageItems = todoItems.subList(startIndex, endIndex);

        todoAdapter.updateTodoItems(pageItems);
    }

    private void loadTodoItemsFromDatabase(final Long selectedProjectId) {
        todoItems = itemDao.getTodoItems(selectedProjectId);

        if (null != todoItems) {
            todoAdapter.clearProjects();
            todoAdapter.addProjects(todoItems);
        }
        todoAdapter.updateTodoItems(todoItems);
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