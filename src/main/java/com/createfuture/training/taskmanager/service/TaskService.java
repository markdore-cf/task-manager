package com.createfuture.training.taskmanager.service;

import com.createfuture.training.taskmanager.model.Task;
// New Firestore repository
import com.createfuture.training.taskmanager.repository.FirestoreTaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class TaskService {
    // Firestore repository
    private final FirestoreTaskRepository firestoreTaskRepository;

    public TaskService(FirestoreTaskRepository firestoreTaskRepository) {
        this.firestoreTaskRepository = firestoreTaskRepository;
    }

    public Task addTask(String title) {
        try {
            return firestoreTaskRepository.addTask(title);
        } catch (ExecutionException | InterruptedException e) {
            // Handle exception appropriately - log and rethrow as runtime exception
            Thread.currentThread().interrupt(); // Restore interruption status
            throw new RuntimeException("Error adding task to Firestore", e);
        }
    }

    public List<Task> getAllTasks() {
        try {
            return firestoreTaskRepository.getAllTasks();
        } catch (ExecutionException | InterruptedException e) {
            // Handle exception appropriately - log and rethrow as runtime exception
            Thread.currentThread().interrupt(); // Restore interruption status
            throw new RuntimeException("Error getting all tasks from Firestore", e);
        }
    }

    public List<Task> getTopNTasks(int n) {
        try {
            return firestoreTaskRepository.getTopNTasks(n);
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error getting top tasks from Firestore", e);
        }
    }

    /**
     * Marks a task as done by its ID.
     * 
     * @param id the ID of the task
     * @return true if the task was successfully marked as done, false otherwise
     * @deprecated Use {@link #markDone(String)} instead
     */
    @Deprecated
    public boolean markDone(Long id) {
        // Temporary bridge method - will be removed in future
        return markDone(String.valueOf(id));
    }

    /**
     * Marks a task as done by its ID.
     * 
     * @param id the String ID of the task (Firestore document ID)
     * @return true if the task was successfully marked as done, false otherwise
     */
    public boolean markDone(String id) {
        try {
            return firestoreTaskRepository.markDone(id);
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error marking task as done in Firestore", e);
        }
    }

    public void resetTasks() {
        try {
            firestoreTaskRepository.resetTasks();
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error resetting tasks in Firestore", e);
        }
    }

    public List<Task> searchTasksByTitle(String term) {
        try {
            return firestoreTaskRepository.searchTasksByTitle(term);
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error searching tasks by title in Firestore", e);
        }
    }

    /**
     * Gets a task by its ID.
     * 
     * @param id the String ID of the task
     * @return the found Task object, or null if no task exists with the given ID
     */
    public Task getTaskById(String id) {
        try {
            return firestoreTaskRepository.findById(id);
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error getting task by ID from Firestore", e);
        }
    }
}
