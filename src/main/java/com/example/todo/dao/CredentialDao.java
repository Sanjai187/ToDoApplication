package com.example.todo.dao;

import com.example.todo.model.Credential;

public interface CredentialDao {

    long insert(final Credential credential);

    boolean checkCredentials(final Credential credential);

    long updatePassword(final Credential credential);

    boolean checkEmailExists(final String email);

    void open();

    void close();
}
