package com.createfuture.training.taskmanager.controller;

import com.createfuture.training.taskmanager.model.Task;
import com.createfuture.training.taskmanager.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing tasks.
 * <p>
 * Provides endpoints to get all tasks, get top N tasks, create a new task, and
 * delete a task.
 * </p>
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskRestController {

    /**
     * Service for task operations.
     */
    private final TaskService taskService;

    /**
     * Constructs a new TaskRestController with the given TaskService.
     * 
     * @param taskService the service to manage tasks
     */
    @Autowired
    public TaskRestController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Retrieves all tasks.
     *
     * @return {@link ResponseEntity} containing the list of all tasks and HTTP 200
     *         OK status
     */
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    /**
     * Retrieves the top N tasks.
     *
     * @param n the maximum number of top tasks to return (default is 5 if not
     *          specified)
     * @return {@link ResponseEntity} containing the list of top N tasks and HTTP
     *         200 OK status
     */
    @GetMapping("/top")
    public ResponseEntity<List<Task>> getTopNTasks(@RequestParam(defaultValue = "5") int n) {
        List<Task> topTasks = taskService.getTopNTasks(n);
        return ResponseEntity.ok(topTasks);
    }

    /**
     * Creates a new task with the given title.
     *
     * @param task the {@link Task} object containing the title of the new task
     *             (other fields are ignored)
     * @return {@link ResponseEntity} containing the created task and HTTP 201
     *         Created status
     */
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        Task created = taskService.addTask(task.getTitle());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Marks the task with the specified ID as done (soft delete).
     *
     * @param id the ID of the task to mark as done
     * @return {@link ResponseEntity} with HTTP 204 No Content if the task was found
     *         and marked as done,
     *         or HTTP 404 Not Found if no such task exists
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        boolean removed = taskService.markDone(id);
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    /**
     * Searches for tasks whose titles contain the given term (case-insensitive,
     * partial match).
     * <p>
     * Example: <code>GET /api/tasks/search?term=foo</code> returns all tasks whose
     * titles contain "foo" (case-insensitive).
     * </p>
     *
     * @param term the search term to match in task titles
     * @return {@link ResponseEntity} containing the list of matching tasks and HTTP
     *         200 OK status
     */
    @GetMapping("/search")
    public ResponseEntity<List<Task>> searchTasks(@RequestParam("term") String term) {
        List<Task> results = taskService.searchTasksByTitle(term);
        return ResponseEntity.ok(results);
    }
}
