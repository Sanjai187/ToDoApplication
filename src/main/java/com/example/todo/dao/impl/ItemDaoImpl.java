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
        values.put(itemTable.COLUMN_STATUS, String.valueOf(todo.getStatus()).toLowerCase());
        values.put(itemTable.COLUMN_ORDER, todo.getOrder());

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
        final SQLiteDatabase sqLiteDB = dbHelper.getReadableDatabase();
        final List<Todo> todoList = new ArrayList<>();
        final ItemTable itemTable = new ItemTable();

        try (final Cursor cursor = sqLiteDB.query(itemTable.TABLE_NAME, null,
                null, null, null, null,
                itemTable.COLUMN_ORDER))  {

            if (null != cursor && cursor.moveToFirst()) {
                do {
                    final Long itemId = cursor.getLong(cursor.getColumnIndex(itemTable.COLUMN_ID));
                    final String itemName = cursor.getString(cursor.getColumnIndex(itemTable.COLUMN_NAME));
                    final String status = cursor.getString(cursor.getColumnIndex(itemTable.COLUMN_STATUS));
                    final String order = cursor.getString(cursor.getColumnIndex(itemTable.COLUMN_ORDER));
                    final Todo todo = new Todo(itemName);

                    todo.setId(itemId);
                    todo.setParentId(projectId);
                    todo.setStatus(Todo.Status.valueOf(status.toUpperCase()));
                    todo.setOrder(Long.valueOf(order));
                    todoList.add(todo);
                } while (cursor.moveToNext());
            }
        }

        return todoList;
    }

    @Override
    public void updateTodoItemOrder(final Todo todo) {
        final ContentValues values = new ContentValues();
        final ItemTable itemTable = new ItemTable();

        values.put(itemTable.COLUMN_ORDER, todo.getOrder());
        database.update(itemTable.TABLE_NAME, values, String.format("%s = ?", itemTable.COLUMN_ID), new String[]{String.valueOf(todo.getId())});
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
