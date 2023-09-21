package com.example.todo.model;

import androidx.annotation.NonNull;

public class Todo {

    private String id;
    private String name;
    private boolean isChecked;
    private String parentId;
    private Status status;
    private Long order;
    private String description;

    public Todo(final String name) {
        this.name = name;
    }

    public enum Status {
        COMPLETED,
        NOT_COMPLETED
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String label) {
        this.name = label;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked() {
        this.isChecked =! this.isChecked;
    }

    public String  getParentId() {
        return parentId;
    }

    public void setParentId(final String  parentId) {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    @NonNull
    public String toString() {
        return name;
    }
}