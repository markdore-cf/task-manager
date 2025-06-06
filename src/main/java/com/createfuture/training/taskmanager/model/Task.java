package com.createfuture.training.taskmanager.model;

/**
 * Represents a task entity with an id and title.
 * Adapted for Firestore storage.
 */
public class Task {
    /**
     * The unique identifier for the task.
     * Using String for Firestore document IDs.
     */
    private String id;

    /**
     * The title or description of the task.
     */
    private String title;

    /**
     * Default constructor is needed for Spring's data binding.
     */
    public Task() {
        // Default constructor is needed for Spring's data binding
    }

    /**
     * Constructs a Task with the specified title.
     *
     * @param title the title of the task
     */
    public Task(String title) {
        this.title = title;
    }

    /**
     * Constructs a Task with the specified id and title.
     *
     * @param id    the unique identifier of the task (Firestore document ID)
     * @param title the title of the task
     */
    public Task(String id, String title) {
        this.id = id;
        this.title = title;
    }

    /**
     * Sets the id of the task.
     *
     * @param id the unique identifier to set (Firestore document ID)
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the id of the task.
     *
     * @return the id of the task (Firestore document ID)
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the title of the task.
     *
     * @return the title of the task
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the task.
     *
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns a string representation of the Task.
     *
     * @return a string representation of the Task
     */
    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
