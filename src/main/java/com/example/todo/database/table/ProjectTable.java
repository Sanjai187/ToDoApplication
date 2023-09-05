package com.example.todo.database.table;

public class ProjectTable {

    public final String TABLE_NAME = "PROJECTS";
    public final String COLUMN_ID = "ID";
    public final String COLUMN_NAME = "NAME";
    public final String COLUMN_USER_ID = "USER_ID";
    public final String COLUMN_ORDER = "PROJECT_ORDER";
    private final UserTable userTable = new UserTable();

    public final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_USER_ID + " INTEGER NOT NULL, " +
                    COLUMN_ORDER + " INTEGER, " +
                    "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " +
                    userTable.TABLE_NAME + "(" + userTable.COLUMN_ID + "));";
}
