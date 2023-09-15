package com.example.todo.model;

public class ResetPassword {

    private String email;
    private String password;
    private String oldHint;
    private String newHint;

    public ResetPassword(final Credential credential, final String newHint) {
        this.email = credential.getEmail();
        this.password = credential.getPassword();
        this.oldHint = credential.getHint();
        this.newHint = newHint;
    }
}
