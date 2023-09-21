package com.example.todo.controller;

import com.example.todo.service.TodoService;

public class TodoController {

    private final TodoService service;

    public TodoController(final TodoService service) {
        this.service = service;
    }

    public void onAddItem() {
        service.onAddTodoItem();
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
