package com.example.todo.model;

import java.util.List;

public class Query {

    private String search;
    private List<String> searchAttribute;
    private Filter filter;

    public String getSearch() {
        return search;
    }

    public void setSearch(final String search) {
        this.search = search;
    }

    public List<String> getSearchAttribute() {
        return searchAttribute;
    }

    public void setSearchAttribute(final List<String> searchAttribute) {
        this.searchAttribute = searchAttribute;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(final Filter filter) {
        this.filter = filter;
    }
}
