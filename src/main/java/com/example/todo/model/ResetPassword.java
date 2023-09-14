package com.example.todo.model;

public class ResetPassword {

    private String email;
    private String password;
    private String hint;
    private String newHint;

    public ResetPassword(final Credential credential, final String newHint) {
        this.email = credential.getEmail();
        this.password = credential.getPassword();
        this.hint = credential.getHint();
        this.newHint = newHint;
    }
}
