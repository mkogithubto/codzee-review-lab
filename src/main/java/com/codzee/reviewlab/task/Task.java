
package com.codzee.reviewlab.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class Task {

    private Long id;

    @NotBlank(message = "Task title is required")
    @Size(max = 100, message = "Task title must not exceed 100 characters")
    private String title;

    @Size(max = 500, message = "Task description must not exceed 500 characters")
    private String description;

    private boolean completed;

    public Task() {
    }

    public Task(Long id, String title, String description, boolean completed) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.completed = completed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
