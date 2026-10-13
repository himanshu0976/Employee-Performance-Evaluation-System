package com.eps.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Aggregated department performance statistics DTO.
 */
public class DepartmentReportDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer departmentId;
    private String departmentName;
    private String departmentCode;
    private int totalEmployees;
    private int completedEvaluations;
    private BigDecimal averageScore = BigDecimal.ZERO;
    private String averageRating;
    private Map<String, Integer> ratingCounts = new HashMap<>();

    public DepartmentReportDTO() {
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public int getTotalEmployees() {
        return totalEmployees;
    }

    public void setTotalEmployees(int totalEmployees) {
        this.totalEmployees = totalEmployees;
    }

    public int getCompletedEvaluations() {
        return completedEvaluations;
    }

    public void setCompletedEvaluations(int completedEvaluations) {
        this.completedEvaluations = completedEvaluations;
    }

    public BigDecimal getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(BigDecimal averageScore) {
        this.averageScore = averageScore;
    }

    public String getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(String averageRating) {
        this.averageRating = averageRating;
    }

    public Map<String, Integer> getRatingCounts() {
        return ratingCounts;
    }

    public void setRatingCounts(Map<String, Integer> ratingCounts) {
        this.ratingCounts = ratingCounts;
    }
}
