package com.example.todo.model;

import androidx.annotation.NonNull;

public class Project {

    private Long id;
    private String label;
    private Long userId;

    public Project(final String label) {
        this.label = label;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(final Long userId) {
        this.userId = userId;
    }

    @NonNull
    public String toString() {
        return label;
    }
}

