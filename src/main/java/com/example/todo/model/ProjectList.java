package com.example.todo.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ProjectList {

    private List<Project> projectList;

    public ProjectList() {
        this.projectList = new ArrayList<>();
    }

    public void add(final Project project) {
        projectList.add(project);
    }

    public void remove(final Long id) {
        projectList = projectList.stream().filter(project -> ! Objects.equals(project.getId(), id))
                .collect(Collectors.toList());
    }

    public List<Project> getAllList() {
        return projectList;
    }

}

