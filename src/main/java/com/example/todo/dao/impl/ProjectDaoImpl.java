package com.example.todo.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.todo.dao.ProjectDao;
import com.example.todo.database.DBHelper;
import com.example.todo.database.table.ItemTable;
import com.example.todo.database.table.ProjectTable;
import com.example.todo.model.Project;

public class ProjectDaoImpl implements ProjectDao {

    private SQLiteDatabase database;
    private final DBHelper dbHelper;

    public ProjectDaoImpl(final Context context) {
        this.dbHelper = new DBHelper(context);
    }

    @Override
    public Long insert(final Project project) {
        final ContentValues values = new ContentValues();
        final ProjectTable projectTable = new ProjectTable();

        values.put(projectTable.COLUMN_NAME, project.getLabel());
        values.put(projectTable.COLUMN_USER_ID, project.getUserId());
        values.put(projectTable.COLUMN_ORDER, project.getOrder());

        return database.insert(projectTable.TABLE_NAME, null, values);
    }

    @Override
    public long onDelete(final Long id) {
        final ItemTable itemTable = new ItemTable();

        return database.delete(itemTable.TABLE_NAME, "ID = " + itemTable.COLUMN_ID, null);
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
