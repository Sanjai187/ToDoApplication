package com.example.todo.controller;

import com.example.todo.SearchActivity;
import com.example.todo.service.SearchService;

public class SearchController {

    private final SearchActivity activity;
    private final SearchService service;

    public SearchController(final SearchActivity activity, final SearchService service) {
        this.activity = activity;
        this.service = service;
    }

    public void onClickSearchView() {
        service.setupSearchView();
    }

    public void onClickSpinner() {
        service.setupSpinner();
    }

    public void onClickFilterSpinner() {
        service.setupFilterSpinner();
    }
}
