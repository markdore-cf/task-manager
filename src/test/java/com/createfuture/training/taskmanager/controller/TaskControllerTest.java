package com.createfuture.training.taskmanager.controller;

import com.createfuture.training.taskmanager.model.Task;
import com.createfuture.training.taskmanager.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    private Task sampleTask;

    @BeforeEach
    void setUp() {
        sampleTask = new Task("Test Task");
        when(taskService.getAllTasks()).thenReturn(Arrays.asList(sampleTask));
    }

    @Test
    void getHomePage_ReturnsTasksView() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("tasks"))
                .andExpect(content().string(containsString("Test Task")));
    }

    @Test
    void postAddTask_ShouldCallService() throws Exception {
        mockMvc.perform(post("/add")
                .param("title", "New Task"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(taskService).addTask(Mockito.argThat(title -> title.equals("New Task")));
    }

    @Test
    void postMarkTaskDone_ShouldCallService() throws Exception {
        mockMvc.perform(post("/tasks/done")
                .param("id", "abc123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(taskService).markDone("abc123"); // Verifying the id is passed as a String
    }
}
