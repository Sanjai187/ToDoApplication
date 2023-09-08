package com.example.todo.dao.impl;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.todo.dao.UserDao;
import com.example.todo.database.DBHelper;
import com.example.todo.database.table.UserTable;
import com.example.todo.model.UserProfile;

public class UserDaoImpl implements UserDao {

    private SQLiteDatabase database;
    private final DBHelper dbHelper;
    private final UserTable userTable = new UserTable();

    public UserDaoImpl(final Context context) {
        this.dbHelper = new DBHelper(context);
    }

    @Override
    public Long insert(final UserProfile userProfile) {
        final ContentValues values = new ContentValues();

        values.put(userTable.COLUMN_NAME, userProfile.getUserName());
        values.put(userTable.COLUMN_DESCRIPTION, userProfile.getTitle());

        return database.insert(userTable.TABLE_NAME, null, values);
    }

    @Override
    public Long onUpdate(UserProfile userProfile) {
        final ContentValues values = new ContentValues();

        values.put(userTable.COLUMN_NAME, userProfile.getUserName());
        values.put(userTable.COLUMN_DESCRIPTION, userProfile.getTitle());

        return (long) database.update(userTable.TABLE_NAME, values, String.format("%s = ?",
                userTable.COLUMN_ID), new String[] {String.valueOf(userProfile.getId())});
    }

    @SuppressLint("Range")
    @Override
    public UserProfile getUserProfile() {
        final SQLiteDatabase dataBase = dbHelper.getReadableDatabase();

        final Cursor cursor = dataBase.query(
                userTable.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            final UserProfile userProfile =  new UserProfile();

            userProfile.setId(cursor.getLong(cursor.getColumnIndex(userTable.COLUMN_ID)));
            userProfile.setUserName(cursor.getString(cursor.getColumnIndex(userTable.COLUMN_NAME)));
            userProfile.setTitle(cursor.getString(cursor.getColumnIndex(userTable.COLUMN_DESCRIPTION)));
            cursor.close();

            return userProfile;
        }

        return null;
    }

    @Override
    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    @Override
    public void close() {
        dbHelper.close();
    }
}
