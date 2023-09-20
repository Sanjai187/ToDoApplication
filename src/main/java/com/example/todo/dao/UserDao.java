package com.example.todo.dao;

import com.example.todo.model.UserProfile;

public interface UserDao {

    Long insert(final UserProfile userProfile);
    UserProfile getUserProfile();
    Long onUpdate(final UserProfile userProfile);
    UserProfile getUserDetails(final String email);
    void open();
    void close();
}
