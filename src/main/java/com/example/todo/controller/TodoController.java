package com.example.todo.controller;

import com.example.todo.TodoActivity;
import com.example.todo.TodoListActivity;
import com.example.todo.model.Project;
import com.example.todo.service.TodoService;

public class TodoController {

    private final TodoListActivity activity;
    private final TodoService service;

    public TodoController(final TodoListActivity activity, final TodoService service) {
        this.activity = activity;
        this.service = service;
    }

    public void onAddItem() {
        service.onAddItem();
    }

    public void setupFilterSpinner() {
        service.setupFilterSpinner();
    }

    public void goToSearchActivity() {
        service.navigateToSearchActivity();
    }

    public void onClickAddVisibility() {
        service.toggleAddListVisibility();
    }

    public void onClickNextPage() {
        service.navigateToNextPage();
    }

    public void onclickPreviousPage() {
        service.navigateToPreviousPage();
    }
}
