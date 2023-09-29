package com.example.todo.service;

public interface TodoService {

    void onAddTodoItem();

    void setupFilterSpinner();

    void startSearchActivity();

    void toggleAddListVisibility();

    void navigateToNextPage();

    void navigateToPreviousPage();
}
