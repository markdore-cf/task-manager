package com.createfuture.training.taskmanager.controller;

import com.createfuture.training.taskmanager.model.Task;
import com.createfuture.training.taskmanager.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("task", new Task());
        model.addAttribute("tasks", taskService.getAllTasks());
        return "tasks";
    }

    @PostMapping("/add")
    public String addTask(@RequestParam String title) {
        taskService.addTask(title);
        return "redirect:/";
    }

    @PostMapping("/tasks/done")
    public String markTaskDone(@RequestParam String id) {
        taskService.markDone(id);
        return "redirect:/";
    }

}
