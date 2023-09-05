package com.example.todo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.todo.database.table.ItemTable;
import com.example.todo.database.table.ProjectTable;
import com.example.todo.database.table.UserTable;

public class DBHelper extends SQLiteOpenHelper {

    public final UserTable userTable = new UserTable();
    public final ProjectTable projectTable = new ProjectTable();
    public final ItemTable itemTable = new ItemTable();

    public DBHelper(final Context context) {
        super(context, "Todo application.db", null, 1);
    }

    @Override
    public void onCreate(final SQLiteDatabase database) {
        database.execSQL(userTable.CREATE_TABLE);
        database.execSQL(projectTable.CREATE_TABLE);
        database.execSQL(itemTable.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase database, final int oldVersion, final int newVersion) {
        database.execSQL(String.format("DROP TABLE IF EXISTS %s", userTable.TABLE_NAME));
        database.execSQL(String.format("DROP TABLE IF EXISTS %s", projectTable.TABLE_NAME));
        database.execSQL(String.format("DROP TABLE IF EXISTS %s", itemTable.TABLE_NAME));
        onCreate(database);
    }
}
