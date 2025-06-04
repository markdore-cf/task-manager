package com.createfuture.training.taskmanager.service;

import com.createfuture.training.taskmanager.model.Task;
import com.createfuture.training.taskmanager.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task addTask(String title) {
        return taskRepository.addTask(title);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public List<Task> getTopNTasks(int n) {
        return taskRepository.findTopN(n);
    }

    public boolean markDone(Long id) {
        return taskRepository.deleteById(id) > 0;
    }

    public void resetTasks() {
        taskRepository.reset();
    }

    public List<Task> searchTasksByTitle(String term) {
        return taskRepository.searchByTitle(term);
    }
}
