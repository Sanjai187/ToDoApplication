package com.example.todo.model;

import androidx.annotation.NonNull;

public class Project {

    private Long id;
    private String label;
    private Long userId;
    private Long order;

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

    public Long getOrder() {
        return order;
    }

    public void setOrder(Long order) {
        this.order = order;
    }

    @NonNull
    public String toString() {
        return label;
    }
}

