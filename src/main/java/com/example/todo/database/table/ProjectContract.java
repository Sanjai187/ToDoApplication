    package com.example.todo.database.table;

    public class ProjectContract {

        public final String TABLE_NAME = "PROJECTS";
        public final String COLUMN_ID = "ID";
        public final String COLUMN_NAME = "NAME";
        public final String COLUMN_USER_ID = "USER_ID";
        public final String COLUMN_ORDER = "PROJECT_ORDER";
        private final UserContract userContract = new UserContract();

        public final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_NAME + " TEXT NOT NULL, " +
                        COLUMN_USER_ID + " INTEGER NOT NULL, " +
                        COLUMN_ORDER + " INTEGER, " +
                        "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " +
                        userContract.TABLE_NAME + "(" + userContract.COLUMN_ID + "));";
    }
