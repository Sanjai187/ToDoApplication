package com.example.todo.model;

public class SignUp {

    private String name;
    private String title;
    private String email;
    private String password;
    private String hint;

    public SignUp(final UserProfile userProfile, final Credential credential) {
        this.name = userProfile.getUserName();
        this.email = userProfile.getEmail();
        this.title = userProfile.getTitle();
        this.hint = credential.getHint();
        this.password = credential.getPassword();
    }
}
