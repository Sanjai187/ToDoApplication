package com.example.todo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.todo.database.table.ItemContract;
import com.example.todo.database.table.ProjectContract;
import com.example.todo.database.table.UserContract;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Todo application.db";
    private static final int DATABASE_VERSION = 1;
    public final UserContract userContract = new UserContract();
    public final ProjectContract projectContract =new ProjectContract();
    public final ItemContract itemContract = new ItemContract();

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase database) {
        database.execSQL(userContract.CREATE_TABLE);
        database.execSQL(projectContract.CREATE_TABLE);
        database.execSQL(itemContract.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase database, final int oldVersion, final int newVersion) {
        database.execSQL(String.format("DROP TABLE IF EXISTS %s", userContract.TABLE_NAME));
        database.execSQL(String.format("DROP TABLE IF EXISTS %s", projectContract.TABLE_NAME));
        database.execSQL(String.format("DROP TABLE IF EXISTS %s", itemContract.TABLE_NAME));
        onCreate(database);
    }
}
