package com.example.todo.dao.impl;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.todo.dao.ItemDao;
import com.example.todo.database.DBHelper;
import com.example.todo.database.table.ItemTable;
import com.example.todo.model.Todo;

import java.util.ArrayList;
import java.util.List;

public class ItemDaoImpl implements ItemDao {

    private SQLiteDatabase database;
    private final DBHelper dbHelper;

    public ItemDaoImpl(final Context context) {
        this.dbHelper = new DBHelper(context);
    }

    @Override
    public Long insert(final Todo todo) {
        final ContentValues values = new ContentValues();
        final ItemTable itemTable = new ItemTable();

        values.put(itemTable.COLUMN_NAME, todo.getLabel());
        values.put(itemTable.COLUMN_PROJECT_ID, todo.getParentId());
        values.put(itemTable.COLUMN_STATUS, todo.getStatus());

        return database.insert(itemTable.TABLE_NAME, null, values);
    }

    @Override
    public long onDelete(final Long id) {
        final ItemTable itemTable = new ItemTable();

        return database.delete(itemTable.TABLE_NAME, "ID = " + itemTable.COLUMN_ID, null);
    }

    public void onUpdate(final Todo todo) {
        final ContentValues values = new ContentValues();
        final ItemTable itemTable = new ItemTable();

        values.put(itemTable.COLUMN_STATUS, String.valueOf(todo.getStatus()));
        database.update(itemTable.TABLE_NAME, values, String.format("%s = ?", itemTable.COLUMN_ID), new String[]{String.valueOf(todo.getId())});
    }

    @SuppressLint("Range")
    public List<Todo> getTodoItems(final Long projectId) {
        final List<Todo> todoItemList = new ArrayList<>();
        final ItemTable itemTable = new ItemTable();

        try (final Cursor cursor = database.query(itemTable.TABLE_NAME, null,
                String.format("%s = ?", itemTable.COLUMN_PROJECT_ID), new String[]{String.valueOf(projectId)},
                null, null, null)) {

            if (null != cursor && cursor.moveToFirst()) {
                do {
                    final Long itemId = cursor.getLong(cursor.getColumnIndex(itemTable.COLUMN_ID));
                    final String itemName = cursor.getString(cursor.getColumnIndex(itemTable.COLUMN_NAME));
                    final String status = cursor.getString(cursor.getColumnIndex(itemTable.COLUMN_STATUS));
                    final Todo todo = new Todo(itemName);

                    todo.setId(itemId);
                    todo.setParentId(projectId);
                    todo.setStatus(status.toUpperCase());
                    todoItemList.add(todo);
                } while (cursor.moveToNext());
            }
        }

        return todoItemList;
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
}
