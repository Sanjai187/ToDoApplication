package com.example.todo.database.table;

public class ItemContract {

    public final String TABLE_NAME = "ITEMS";
    public final String COLUMN_ID = "ID";
    public final String COLUMN_NAME = "NAME";
    public final String COLUMN_PROJECT_ID = "PROJECT_ID";
    public final String COLUMN_STATUS = "STATUS";
    private final ProjectContract projectContract = new ProjectContract();
    public final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_PROJECT_ID + " INTEGER NOT NULL, " +
                    COLUMN_STATUS + " TEXT, " +
                    "FOREIGN KEY(" + COLUMN_PROJECT_ID + ") REFERENCES " +
                    projectContract.TABLE_NAME + "(" + projectContract.COLUMN_ID + "));";
}
