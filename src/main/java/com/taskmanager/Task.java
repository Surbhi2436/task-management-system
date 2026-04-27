package com.taskmanager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Task {
    private int id;
    private String title;
    private LocalDate dueDate;
    private LocalTime dueTime;
    private String status;
    private LocalDateTime createdAt;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalTime getDueTime() { return dueTime; }
    public void setDueTime(LocalTime dueTime) { this.dueTime = dueTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isOverdue() {
        LocalDateTime taskTime = LocalDateTime.of(dueDate, dueTime);
        return taskTime.isBefore(LocalDateTime.now()) && status.equals("pending");
    }
}
