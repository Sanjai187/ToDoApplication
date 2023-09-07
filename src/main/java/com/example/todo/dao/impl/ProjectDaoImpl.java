package com.example.todo.dao.impl;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.todo.dao.ProjectDao;
import com.example.todo.database.DBHelper;
import com.example.todo.database.table.ProjectTable;
import com.example.todo.model.Project;

import java.util.ArrayList;
import java.util.List;

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
    public long onDelete(final Project project) {
        final ProjectTable projectTable = new ProjectTable();

        return database.delete(projectTable.TABLE_NAME,  projectTable.COLUMN_ID + " = ?", new String[]{String.valueOf(project.getId())});
    }

    @Override
    public void updateProjectsOrder(final Project project) {
        final ContentValues values = new ContentValues();
        final ProjectTable projectTable = new ProjectTable();

        values.put(projectTable.COLUMN_ORDER, project.getOrder());
        database.update(projectTable.TABLE_NAME, values, String.format("%s = ?", projectTable.COLUMN_ID), new String[]{String.valueOf(project.getId())});
    }

    @SuppressLint("Range")
    @Override
    public List<Project> getAllProjects() {
        final SQLiteDatabase sqLiteDB = dbHelper.getReadableDatabase();
        final List<Project> projects = new ArrayList<>();
        final ProjectTable projectTable = new ProjectTable();

        try (final Cursor cursor = sqLiteDB.query(projectTable.TABLE_NAME, null,
                null, null, null, null,
                projectTable.COLUMN_ORDER)) {

            if (null != cursor && cursor.moveToFirst()) {
                do {
                    final Long projectId = cursor.getLong(cursor.getColumnIndex(projectTable.COLUMN_ID));
                    final String projectName = cursor.getString(cursor.getColumnIndex(projectTable.COLUMN_NAME));
                    final Long userId = cursor.getLong(cursor.getColumnIndex(projectTable.COLUMN_USER_ID));
                    final Project project = new Project();

                    project.setId(projectId);
                    project.setLabel(projectName);
                    project.setUserId(userId);
                    projects.add(project);
                } while (cursor.moveToNext());
            }
        }

        return projects;
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
