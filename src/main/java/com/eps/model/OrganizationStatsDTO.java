package com.eps.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * High-level system & organizational statistics for Admin dashboard & executive reporting.
 */
public class OrganizationStatsDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int totalEmployees;
    private int totalManagers;
    private int totalDepartments;
    private int totalCycles;
    private int activeCyclesCount;
    private int completedEvaluationsCount;
    private int pendingEvaluationsCount;
    private BigDecimal overallAverageScore = BigDecimal.ZERO;
    private double completionRatePercent = 0.0;

    // Ordered distribution of ratings: Outstanding, Exceeds, Meets, Needs Improvement, Unsatisfactory
    private Map<String, Integer> ratingDistribution = new LinkedHashMap<>();

    public OrganizationStatsDTO() {
        ratingDistribution.put(Evaluation.RATING_OUTSTANDING, 0);
        ratingDistribution.put(Evaluation.RATING_EXCEEDS, 0);
        ratingDistribution.put(Evaluation.RATING_MEETS, 0);
        ratingDistribution.put(Evaluation.RATING_NEEDS_IMPROVEMENT, 0);
        ratingDistribution.put(Evaluation.RATING_UNSATISFACTORY, 0);
    }

    public int getTotalEmployees() {
        return totalEmployees;
    }

    public void setTotalEmployees(int totalEmployees) {
        this.totalEmployees = totalEmployees;
    }

    public int getTotalManagers() {
        return totalManagers;
    }

    public void setTotalManagers(int totalManagers) {
        this.totalManagers = totalManagers;
    }

    public int getTotalDepartments() {
        return totalDepartments;
    }

    public void setTotalDepartments(int totalDepartments) {
        this.totalDepartments = totalDepartments;
    }

    public int getTotalCycles() {
        return totalCycles;
    }

    public void setTotalCycles(int totalCycles) {
        this.totalCycles = totalCycles;
    }

    public int getActiveCyclesCount() {
        return activeCyclesCount;
    }

    public void setActiveCyclesCount(int activeCyclesCount) {
        this.activeCyclesCount = activeCyclesCount;
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

    public BigDecimal getOverallAverageScore() {
        return overallAverageScore;
    }

    public void setOverallAverageScore(BigDecimal overallAverageScore) {
        this.overallAverageScore = overallAverageScore;
    }

    public double getCompletionRatePercent() {
        return completionRatePercent;
    }

    public void setCompletionRatePercent(double completionRatePercent) {
        this.completionRatePercent = completionRatePercent;
    }

    public Map<String, Integer> getRatingDistribution() {
        return ratingDistribution;
    }

    public void setRatingDistribution(Map<String, Integer> ratingDistribution) {
        this.ratingDistribution = ratingDistribution;
    }
}
