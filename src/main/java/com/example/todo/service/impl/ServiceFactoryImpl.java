package com.example.todo.service.impl;

import com.example.todo.api.impl.AuthenticationService;
import com.example.todo.api.impl.ProjectListService;
import com.example.todo.api.impl.TodoItemService;

public class ServiceFactoryImpl {

    private static ServiceFactoryImpl serviceFactory;

    public ServiceFactoryImpl() {
    }

    public static ServiceFactoryImpl getInstance() {
        return null == serviceFactory ? serviceFactory = new ServiceFactoryImpl() : serviceFactory;
    }

    public AuthenticationService createAuthentication(final String baseUrl) {
        return new AuthenticationService(baseUrl);
    }

    public AuthenticationService createAuthentication(final String baseUrl, final String token) {
        return new AuthenticationService(baseUrl, token);
    }

    public ProjectListService createProject(final String baseUrl, final String token) {
        return new ProjectListService(baseUrl, token);
    }

    public TodoItemService createTodoItem(final String baseUrl, final String token) {
        return new TodoItemService(baseUrl, token);
    }
}