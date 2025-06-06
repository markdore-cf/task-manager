package com.createfuture.training.taskmanager.service;

import com.createfuture.training.taskmanager.model.Task;
import com.createfuture.training.taskmanager.repository.FirestoreTaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private FirestoreTaskRepository firestoreTaskRepository;

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        // No need for additional setup with @ExtendWith(MockitoExtension.class)
    }

    @Test
    void addTask_ShouldCallRepository() throws ExecutionException, InterruptedException {
        // Arrange
        String title = "Task 1";
        Task mockTask = new Task("task-id-123", title);
        when(firestoreTaskRepository.addTask(title)).thenReturn(mockTask);

        // Act
        Task result = taskService.addTask(title);

        // Assert
        assertNotNull(result);
        assertEquals(title, result.getTitle());
        assertEquals("task-id-123", result.getId());
        verify(firestoreTaskRepository).addTask(title);
    }

    @Test
    void getAllTasks_ShouldReturnAllTasks() throws ExecutionException, InterruptedException {
        // Arrange
        List<Task> mockTasks = Arrays.asList(
                new Task("id-1", "Task 1"),
                new Task("id-2", "Task 2"));
        when(firestoreTaskRepository.getAllTasks()).thenReturn(mockTasks);

        // Act
        List<Task> result = taskService.getAllTasks();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Task 1", result.get(0).getTitle());
        assertEquals("Task 2", result.get(1).getTitle());
        verify(firestoreTaskRepository).getAllTasks();
    }

    @Test
    void getTopNTasks_ShouldReturnTopNTasks() throws ExecutionException, InterruptedException {
        // Arrange
        List<Task> mockTasks = Arrays.asList(
                new Task("id-1", "Task 1"),
                new Task("id-2", "Task 2"));
        when(firestoreTaskRepository.getTopNTasks(2)).thenReturn(mockTasks);

        // Act
        List<Task> result = taskService.getTopNTasks(2);

        // Assert
        assertEquals(2, result.size());
        assertEquals("Task 1", result.get(0).getTitle());
        assertEquals("Task 2", result.get(1).getTitle());
        verify(firestoreTaskRepository).getTopNTasks(2);
    }

    @Test
    void getTopNTasks_ShouldReturnEmptyListWhenNIsZero() throws ExecutionException, InterruptedException {
        // Arrange
        when(firestoreTaskRepository.getTopNTasks(0)).thenReturn(Collections.emptyList());

        // Act
        List<Task> result = taskService.getTopNTasks(0);

        // Assert
        assertTrue(result.isEmpty());
        verify(firestoreTaskRepository).getTopNTasks(0);
    }

    @Test
    void markDone_ShouldCallRepositoryWithStringId() throws ExecutionException, InterruptedException {
        // Arrange
        String taskId = "task-id-123";
        when(firestoreTaskRepository.markDone(taskId)).thenReturn(true);

        // Act
        boolean result = taskService.markDone(taskId);

        // Assert
        assertTrue(result);
        verify(firestoreTaskRepository).markDone(taskId);
    }

    @Test
    void markDone_ShouldReturnFalseIfTaskNotFound() throws ExecutionException, InterruptedException {
        // Arrange
        String taskId = "nonexistent-id";
        when(firestoreTaskRepository.markDone(taskId)).thenReturn(false);

        // Act
        boolean result = taskService.markDone(taskId);

        // Assert
        assertFalse(result);
        verify(firestoreTaskRepository).markDone(taskId);
    }

    @Test
    void searchTasksByTitle_ShouldReturnMatchingTasks() throws ExecutionException, InterruptedException {
        // Arrange
        String searchTerm = "test";
        List<Task> mockTasks = Arrays.asList(
                new Task("id-1", "Test Task"),
                new Task("id-2", "Another test"));
        when(firestoreTaskRepository.searchTasksByTitle(searchTerm)).thenReturn(mockTasks);

        // Act
        List<Task> result = taskService.searchTasksByTitle(searchTerm);

        // Assert
        assertEquals(2, result.size());
        verify(firestoreTaskRepository).searchTasksByTitle(searchTerm);
    }

    @Test
    void getTaskById_ShouldReturnTaskIfExists() throws ExecutionException, InterruptedException {
        // Arrange
        String taskId = "task-id-123";
        Task mockTask = new Task(taskId, "Task 1");
        when(firestoreTaskRepository.findById(taskId)).thenReturn(mockTask);

        // Act
        Task result = taskService.getTaskById(taskId);

        // Assert
        assertNotNull(result);
        assertEquals(taskId, result.getId());
        assertEquals("Task 1", result.getTitle());
        verify(firestoreTaskRepository).findById(taskId);
    }

    @Test
    void getTaskById_ShouldReturnNullIfTaskNotFound() throws ExecutionException, InterruptedException {
        // Arrange
        String taskId = "nonexistent-id";
        when(firestoreTaskRepository.findById(taskId)).thenReturn(null);

        // Act
        Task result = taskService.getTaskById(taskId);

        // Assert
        assertNull(result);
        verify(firestoreTaskRepository).findById(taskId);
    }

    @Test
    void resetTasks_ShouldCallRepository() throws ExecutionException, InterruptedException {
        // Act
        taskService.resetTasks();

        // Assert
        verify(firestoreTaskRepository).resetTasks();
    }
}
