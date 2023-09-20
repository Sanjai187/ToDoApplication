package com.example.todo.database.table;

public class CredentialTable {

    public final String TABLE_NAME = "CREDENTIAL";
    public final String COLUMN_ID = "ID";
    public final String COLUMN_EMAIL = "EMAIL";
    public final String COLUMN_PASSWORD = "PASSWORD";
    public final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_EMAIL + " TEXT NOT NULL, " +
                    COLUMN_PASSWORD + " TEXT NOT NULL)";
}