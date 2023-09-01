package com.example.todo.model;

import java.util.List;

public class Filter {

    private String attribute;
    private List<Object> values;

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(final String attribute) {
        this.attribute = attribute;
    }

    public List<Object> getValues() {
        return values;
    }

    public void setValues(final List<Object> values) {
        this.values = values;
    }
}

