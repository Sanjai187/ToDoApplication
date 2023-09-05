package com.example.todo.database.table;

public class UserTable {

    public final String TABLE_NAME = "USERS";
    public final String COLUMN_ID = "ID";
    public final String COLUMN_NAME = "NAME";
    public final String COLUMN_DESCRIPTION = "DESCRIPTION";

    public final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_DESCRIPTION + " TEXT);";
}
