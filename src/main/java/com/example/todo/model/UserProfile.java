package com.example.todo.model;

public class UserProfile {

    private String userName;
    private String title;

    public String getUserName() {
        return userName;
    }

    public void setUserName(final String userName) {
        this.userName = userName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public StringBuilder getProfileIcon() {
        final String[] words = userName.split(" ");
        final StringBuilder text = new StringBuilder();

        for (final String word : words) {

            if (! word.isEmpty()) {
                text.append(Character.toUpperCase(word.charAt(0)));
            }
        }

        return text;
    }
}
