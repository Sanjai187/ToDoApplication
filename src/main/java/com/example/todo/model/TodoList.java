package com.example.todo.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TodoList {

    private List<Todo> todoList;
   private final Query query;

    public TodoList() {
        this.todoList = new ArrayList<>();
        this.query = new Query();
    }

    public void add(final Todo todo) {
        todoList.add(todo);
    }

    public void remove(final Long id) {
        todoList = todoList.stream().filter(todo -> !Objects.equals(todo.getId(), id)).collect(Collectors.toList());
    }

    public List<Todo> getAllList() {
        return todoList;
    }

    public Query getQuery() {
        return query;
    }
}