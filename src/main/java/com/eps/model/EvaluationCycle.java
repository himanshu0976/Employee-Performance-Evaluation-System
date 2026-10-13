package com.eps.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Evaluation Cycle model representing review periods.
 */
public class EvaluationCycle implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status {
        UPCOMING,
        ACTIVE,
        COMPLETED
    }

    private Integer id;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Status status = Status.UPCOMING;
    private LocalDateTime createdAt;

    // Derived statistics for dashboard/reporting
    private int totalEligibleEmployees;
    private int completedEvaluationsCount;
    private int pendingEvaluationsCount;

    public EvaluationCycle() {
    }

    public EvaluationCycle(Integer id, String title, String description, 
                           LocalDate startDate, LocalDate endDate, Status status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public boolean isActive() {
        return this.status == Status.ACTIVE;
    }

    public boolean isCompleted() {
        return this.status == Status.COMPLETED;
    }

    public boolean isUpcoming() {
        return this.status == Status.UPCOMING;
    }

    public String getStatusBadgeClass() {
        if (status == Status.ACTIVE) {
            return "bg-success";
        } else if (status == Status.COMPLETED) {
            return "bg-secondary";
        } else {
            return "bg-warning text-dark";
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title != null ? title.trim() : null;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description != null ? description.trim() : null;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getTotalEligibleEmployees() {
        return totalEligibleEmployees;
    }

    public void setTotalEligibleEmployees(int totalEligibleEmployees) {
        this.totalEligibleEmployees = totalEligibleEmployees;
    }

    public int getCompletedEvaluationsCount() {
        return completedEvaluationsCount;
    }

    public void setCompletedEvaluationsCount(int completedEvaluationsCount) {
        this.completedEvaluationsCount = completedEvaluationsCount;
    }

    public int getPendingEvaluationsCount() {
        return pendingEvaluationsCount;
    }

    public void setPendingEvaluationsCount(int pendingEvaluationsCount) {
        this.pendingEvaluationsCount = pendingEvaluationsCount;
    }
}
