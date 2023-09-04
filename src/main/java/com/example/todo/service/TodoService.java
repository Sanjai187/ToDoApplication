package com.example.todo.service;

public interface TodoService {

    void onAddItem();

    void setupFilterSpinner();

    void navigateToSearchActivity();

    void toggleAddListVisibility();

    void navigateToNextPage();

    void navigateToPreviousPage();
}
