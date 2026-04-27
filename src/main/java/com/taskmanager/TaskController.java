package com.taskmanager;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.time.LocalDate;
import java.time.LocalTime;

@RestController
@RequestMapping("/")
public class TaskController {

    private final List<Task> tasks = new CopyOnWriteArrayList<>();
    private int nextId = 1;

    // Health check
    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "running");
        response.put("system", "Task Management System");
        response.put("version", "1.0");
        return response;
    }

    // GET /tasks - Get all tasks (with optional status filter)
    @GetMapping("/tasks")
    public List<Task> getTasks(@RequestParam(required = false) String status) {
        if (status == null) return tasks;
        
        if (status.equals("overdue")) {
            return tasks.stream()
                .filter(t -> t.getStatus().equals("pending") && t.isOverdue())
                .toList();
        } else if (status.equals("completed")) {
            return tasks.stream()
                .filter(t -> t.getStatus().equals("completed"))
                .toList();
        } else if (status.equals("pending")) {
            return tasks.stream()
                .filter(t -> t.getStatus().equals("pending"))
                .toList();
        }
        return tasks;
    }

    // GET /tasks/reminders - Tasks due in next 2 hours
    @GetMapping("/tasks/reminders")
    public Map<String, Object> getReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reminderWindow = now.plusHours(2);
        
        List<Task> reminders = tasks.stream()
            .filter(t -> t.getStatus().equals("pending"))
            .filter(t -> {
                try {
                    LocalDateTime taskTime = LocalDateTime.of(t.getDueDate(), t.getDueTime());
                    return !taskTime.isBefore(now) && !taskTime.isAfter(reminderWindow);
                } catch (Exception e) {
                    return false;
                }
            })
            .toList();
        
        Map<String, Object> response = new HashMap<>();
        response.put("reminders", reminders);
        response.put("count", reminders.size());
        response.put("message", "You have " + reminders.size() + " task(s) due soon!");
        return response;
    }

    // POST /tasks - Add a new task
    @PostMapping("/tasks")
    public ResponseEntity<Task> addTask(@RequestBody Task task) {
        task.setId(nextId++);
        task.setStatus("pending");
        task.setCreatedAt(LocalDateTime.now());
        tasks.add(task);
        return ResponseEntity.status(201).body(task);
    }

    // GET /tasks/{id} - Get a single task
    @GetMapping("/tasks/{id}")
    public ResponseEntity<Task> getTask(@PathVariable int id) {
        return tasks.stream()
            .filter(t -> t.getId() == id)
            .findFirst()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // PUT /tasks/{id}/complete - Mark task as complete
    @PutMapping("/tasks/{id}/complete")
    public ResponseEntity<Task> completeTask(@PathVariable int id) {
        for (Task task : tasks) {
            if (task.getId() == id) {
                task.setStatus("completed");
                return ResponseEntity.ok(task);
            }
        }
        return ResponseEntity.notFound().build();
    }

    // PUT /tasks/{id} - Edit a task
    @PutMapping("/tasks/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable int id, @RequestBody Task updatedTask) {
        for (Task task : tasks) {
            if (task.getId() == id) {
                if (updatedTask.getTitle() != null) task.setTitle(updatedTask.getTitle());
                if (updatedTask.getDueDate() != null) task.setDueDate(updatedTask.getDueDate());
                if (updatedTask.getDueTime() != null) task.setDueTime(updatedTask.getDueTime());
                return ResponseEntity.ok(task);
            }
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE /tasks/{id} - Delete a task
    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Map<String, String>> deleteTask(@PathVariable int id) {
        boolean removed = tasks.removeIf(t -> t.getId() == id);
        if (removed) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Task deleted successfully");
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE /tasks/clear-completed - Clear all completed tasks
    @DeleteMapping("/tasks/clear-completed")
    public Map<String, String> clearCompleted() {
        tasks.removeIf(t -> t.getStatus().equals("completed"));
        Map<String, String> response = new HashMap<>();
        response.put("message", "All completed tasks cleared");
        return response;
    }
}