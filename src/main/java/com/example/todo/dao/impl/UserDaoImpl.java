package com.example.todo.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.todo.dao.UserDao;
import com.example.todo.database.DBHelper;
import com.example.todo.database.table.UserTable;
import com.example.todo.model.UserProfile;

public class UserDaoImpl implements UserDao {

    private SQLiteDatabase database;
    private final DBHelper dbHelper;
    private UserTable userTable;

    public UserDaoImpl(final Context context) {
        this.dbHelper = new DBHelper(context);
    }

    @Override
    public void open() {
        database = dbHelper.getWritableDatabase();
        database = dbHelper.getReadableDatabase();
    }

    @Override
    public void close() {
        dbHelper.close();
    }

    @Override
    public Long insert(final UserProfile userProfile) {
        final ContentValues values = new ContentValues();
        userTable = new UserTable();

        values.put(userTable.COLUMN_NAME, userProfile.getUserName());
        values.put(userTable.COLUMN_DESCRIPTION, userProfile.getTitle());

        return database.insert(userTable.TABLE_NAME, null, values);
    }
}
