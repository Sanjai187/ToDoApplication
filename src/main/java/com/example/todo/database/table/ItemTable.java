package com.example.todo.database.table;

public class ItemTable {

    public final String TABLE_NAME = "ITEMS";
    public final String COLUMN_ID = "ID";
    public final String COLUMN_NAME = "NAME";
    public final String COLUMN_PROJECT_ID = "PROJECT_ID";
    public final String COLUMN_STATUS = "STATUS";
    public final String COLUMN_ORDER = "TODO_ORDER";
    private final ProjectTable projectTable = new ProjectTable();
    public final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_PROJECT_ID + " INTEGER NOT NULL, " +
                    COLUMN_STATUS + " TEXT, " +
                    COLUMN_ORDER + " INTEGER, " +
                    "FOREIGN KEY(" + COLUMN_PROJECT_ID + ") REFERENCES " +
                    projectTable.TABLE_NAME + "(" + projectTable.COLUMN_ID + "));";
}
