package com.example.todo.controller;

import com.example.todo.service.SearchService;

public class SearchController {

    private final SearchService service;

    public SearchController(final SearchService service) {
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
