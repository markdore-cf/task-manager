package com.createfuture.training.taskmanager.repository;

import com.createfuture.training.taskmanager.model.Task;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TaskRepository {

    private final JdbcTemplate jdbcTemplate;

    public TaskRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Task> taskMapper = (rs, rowNum) -> new Task(rs.getLong("id"), rs.getString("title"));

    public Task addTask(String title) {
        String sql = "INSERT INTO tasks (title) VALUES ('" + title + "')";
        jdbcTemplate.execute(sql);
        return getLastInsertedTaskByTitle(title);
    }

    public Task getLastInsertedTaskByTitle(String title) {
        String sql = "SELECT * FROM tasks WHERE title = ? ORDER BY id DESC LIMIT 1";
        List<Task> tasks = jdbcTemplate.query(sql, taskMapper, title);
        if (tasks.isEmpty()) {
            return null;
        }
        return tasks.get(0);
    }

    public List<Task> findAll() {
        return jdbcTemplate.query("SELECT * FROM tasks", taskMapper);
    }

    public int deleteById(Long id) {
        return jdbcTemplate.update("DELETE FROM tasks WHERE id = ?", id);
    }

    public List<Task> findTopN(int n) {
        String sql = "SELECT * FROM tasks LIMIT ?";
        return jdbcTemplate.query(sql, taskMapper, n);
    }

    public void reset() {
        jdbcTemplate.execute("DELETE FROM tasks");
    }

    public List<Task> searchByTitle(String term) {
        String sql = "SELECT * FROM tasks WHERE LOWER(title) LIKE ?";
        String pattern = "%" + term.toLowerCase() + "%";
        return jdbcTemplate.query(sql, taskMapper, pattern);
    }
}
