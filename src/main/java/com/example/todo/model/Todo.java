package com.example.todo.model;

import androidx.annotation.NonNull;

public class Todo {

    private Long id;
    private String label;
    private boolean isChecked;
    private Long parentId;
    private Status status;
    private Long order;

    public Todo(final String label) {
        this.label = label;
    }

    public enum Status {
        COMPLETED,
        NOT_COMPLETED
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

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked() {
        this.isChecked =! this.isChecked;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(final Long parentId) {
        this.parentId = parentId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(final Status status) {
        this.status = status;
    }

    public Long getOrder() {
        return order;
    }

    public void setOrder(final Long order) {
        this.order = order;
    }

    @NonNull
    public String toString() {
        return label;
    }
}