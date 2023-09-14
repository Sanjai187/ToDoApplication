package com.example.todo.model;

public class SignIn {

    private String email;
    private String password;

    public SignIn(final Credential credential) {
        this.email = credential.getEmail();
        this.password = credential.getPassword();
    }
}
