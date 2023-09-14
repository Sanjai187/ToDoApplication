package com.example.todo.dao.impl;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.todo.dao.CredentialDao;
import com.example.todo.database.DBHelper;
import com.example.todo.database.table.CredentialTable;
import com.example.todo.model.Credential;

public class CredentialDaoImpl implements CredentialDao {

    private SQLiteDatabase database;
    private final DBHelper dbHelper;
    private final CredentialTable credentialTable = new CredentialTable();

    public CredentialDaoImpl(final Context context) {
        dbHelper = new DBHelper(context);
    }

    @Override
    public long insert(final Credential credential) {
        final ContentValues values = new ContentValues();

        values.put(credentialTable.COLUMN_EMAIL, credential.getEmail());
        values.put(credentialTable.COLUMN_PASSWORD, credential.getPassword());

        return database.insert(credentialTable.TABLE_NAME, null, values);
    }

    @SuppressLint({"Recycle", "Range"})
    @Override
    public boolean checkCredentials(final Credential credential) {
        final SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        final Cursor cursor = sqLiteDatabase.query(credentialTable.TABLE_NAME,
                new String[]{credentialTable.COLUMN_PASSWORD},
                String.format("%s = ?", credentialTable.COLUMN_EMAIL),
                new String[]{credential.getEmail()}, null, null, null);
        boolean result = false;

        if (null != cursor && cursor.moveToFirst()) {
            final String password = cursor.getString(cursor.getColumnIndex(
                    credentialTable.COLUMN_PASSWORD));

            if (password.equals(credential.getPassword())) {
                result = true;
            }
            cursor.close();
        }

        return result;
    }

    @Override
    public long updatePassword(final Credential credential) {
        final ContentValues values = new ContentValues();

        values.put(credentialTable.COLUMN_PASSWORD, credential.getPassword());

        return database.update(credentialTable.TABLE_NAME, values, String.format("%s = ?",
                credentialTable.COLUMN_EMAIL), new String[]{credential.getEmail()});
    }

    @Override
    public boolean checkEmailExists(final String email) {
        final SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        final Cursor cursor = sqLiteDatabase.query(credentialTable.TABLE_NAME,
                new String[]{credentialTable.COLUMN_EMAIL},
                String.format("%s = ?", credentialTable.COLUMN_EMAIL), new String[]{email},
                null, null, null);
        final boolean emailExists = null != cursor && 0 < cursor.getCount();

        if (null != cursor) {
            cursor.close();
        }
        return emailExists;
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
