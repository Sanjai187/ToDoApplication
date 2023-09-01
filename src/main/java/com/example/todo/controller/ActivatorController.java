package com.example.todo.controller;

import com.example.todo.model.Project;
import com.example.todo.model.ProjectList;
import com.example.todo.service.Activitor;

public class ActivatorController {

    private final ProjectList projectList;
    private final Activitor activitor;

    public ActivatorController(final Activitor activitor, final ProjectList projectList) {
        this.activitor = activitor;
        this.projectList = projectList;
    }

    public void onListItemClicked(final Project project) {
        activitor.goToListPage(project);
    }

    public void onAddNameClicked() {
//        activitor.addNameDialog();
    }

    public void onNameAdded(final String name, final Long id, final Long userId) {
        if (!name.isEmpty()) {
            final Project project = new Project(name);

            project.setId(id);
            project.setLabel(name);
            project.setUserId(userId);
            projectList.add(project);
        }
    }
    public void onListItemLongClicked(final Project project) {
        projectList.remove(project.getId());
        activitor.removeList(project);
    }
}
