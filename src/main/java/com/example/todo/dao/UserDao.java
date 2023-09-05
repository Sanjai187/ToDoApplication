package com.example.todo.dao;

import com.example.todo.model.UserProfile;

public interface UserDao {

    Long insert(final UserProfile userProfile);

    void open();

    void close();
}
