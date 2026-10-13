package com.eps.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Evaluation entity encapsulating evaluation lifecycle, criteria scores, 
 * total score calculation, and performance rating tiers.
 */
public class Evaluation implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status {
        DRAFT,
        SUBMITTED,
        APPROVED
    }

    public static final String RATING_OUTSTANDING = "Outstanding";
    public static final String RATING_EXCEEDS = "Exceeds Expectations";
    public static final String RATING_MEETS = "Meets Expectations";
    public static final String RATING_NEEDS_IMPROVEMENT = "Needs Improvement";
    public static final String RATING_UNSATISFACTORY = "Unsatisfactory";

    private Integer id;
    private Integer cycleId;
    private String cycleTitle;
    private String cycleStatus;
    
    private Integer employeeId;
    private String employeeName;
    private String employeeCode;
    private String departmentName;
    private String designation;
    
    private Integer evaluatorId; // user_id of evaluator
    private String evaluatorName;

    private BigDecimal totalScore;
    private String rating;
    private Status status = Status.DRAFT;
    private String summaryFeedback;
    private String strengths;
    private String areasForImprovement;
    private LocalDateTime evaluatedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Collections demonstrating OOP
    private List<EvaluationScore> scores = new ArrayList<>();

    public Evaluation() {
    }

    /**
     * Calculates the total weighted average score and assigns the rating tier.
     * Formula: sum(score * weight) / sum(weight)
     */
    public void calculateTotalScoreAndRating() {
        if (scores == null || scores.isEmpty()) {
            this.totalScore = BigDecimal.ZERO;
            this.rating = RATING_UNSATISFACTORY;
            return;
        }

        BigDecimal totalWeightedScore = BigDecimal.ZERO;
        BigDecimal totalWeight = BigDecimal.ZERO;

        for (EvaluationScore s : scores) {
            BigDecimal scoreVal = s.getScore() != null ? s.getScore() : BigDecimal.ZERO;
            BigDecimal weightVal = s.getWeightage() != null ? s.getWeightage() : BigDecimal.ONE;

            totalWeightedScore = totalWeightedScore.add(scoreVal.multiply(weightVal));
            totalWeight = totalWeight.add(weightVal);
        }

        if (totalWeight.compareTo(BigDecimal.ZERO) > 0) {
            this.totalScore = totalWeightedScore.divide(totalWeight, 2, RoundingMode.HALF_UP);
        } else {
            // Unweighted fallback
            BigDecimal sum = BigDecimal.ZERO;
            for (EvaluationScore s : scores) {
                sum = sum.add(s.getScore() != null ? s.getScore() : BigDecimal.ZERO);
            }
            this.totalScore = sum.divide(new BigDecimal(scores.size()), 2, RoundingMode.HALF_UP);
        }

        this.rating = determineRating(this.totalScore);
    }

    /**
     * Determines rating category based on system rating scale:
     * 4.50 - 5.00: Outstanding
     * 3.50 - 4.49: Exceeds Expectations
     * 2.50 - 3.49: Meets Expectations
     * 1.50 - 2.49: Needs Improvement
     * Below 1.50: Unsatisfactory
     */
    public static String determineRating(BigDecimal score) {
        if (score == null) {
            return RATING_UNSATISFACTORY;
        }
        double val = score.doubleValue();
        if (val >= 4.5) {
            return RATING_OUTSTANDING;
        } else if (val >= 3.5) {
            return RATING_EXCEEDS;
        } else if (val >= 2.5) {
            return RATING_MEETS;
        } else if (val >= 1.5) {
            return RATING_NEEDS_IMPROVEMENT;
        } else {
            return RATING_UNSATISFACTORY;
        }
    }

    /**
     * Returns the appropriate Bootstrap badge CSS class for this rating.
     */
    public String getRatingBadgeClass() {
        if (rating == null) {
            return "bg-secondary";
        }
        return switch (rating) {
            case RATING_OUTSTANDING -> "bg-success";
            case RATING_EXCEEDS -> "bg-primary";
            case RATING_MEETS -> "bg-info text-dark";
            case RATING_NEEDS_IMPROVEMENT -> "bg-warning text-dark";
            case RATING_UNSATISFACTORY -> "bg-danger";
            default -> "bg-secondary";
        };
    }

    public String getStatusBadgeClass() {
        if (status == Status.APPROVED) {
            return "bg-success";
        } else if (status == Status.SUBMITTED) {
            return "bg-info text-dark";
        } else {
            return "bg-secondary";
        }
    }

    public boolean isDraft() {
        return this.status == Status.DRAFT;
    }

    public boolean isSubmitted() {
        return this.status == Status.SUBMITTED;
    }

    public boolean isApproved() {
        return this.status == Status.APPROVED;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCycleId() {
        return cycleId;
    }

    public void setCycleId(Integer cycleId) {
        this.cycleId = cycleId;
    }

    public String getCycleTitle() {
        return cycleTitle;
    }

    public void setCycleTitle(String cycleTitle) {
        this.cycleTitle = cycleTitle;
    }

    public String getCycleStatus() {
        return cycleStatus;
    }

    public void setCycleStatus(String cycleStatus) {
        this.cycleStatus = cycleStatus;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public Integer getEvaluatorId() {
        return evaluatorId;
    }

    public void setEvaluatorId(Integer evaluatorId) {
        this.evaluatorId = evaluatorId;
    }

    public String getEvaluatorName() {
        return evaluatorName;
    }

    public void setEvaluatorName(String evaluatorName) {
        this.evaluatorName = evaluatorName;
    }

    public BigDecimal getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(BigDecimal totalScore) {
        this.totalScore = totalScore;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getSummaryFeedback() {
        return summaryFeedback;
    }

    public void setSummaryFeedback(String summaryFeedback) {
        this.summaryFeedback = summaryFeedback != null ? summaryFeedback.trim() : null;
    }

    public String getStrengths() {
        return strengths;
    }

    public void setStrengths(String strengths) {
        this.strengths = strengths != null ? strengths.trim() : null;
    }

    public String getAreasForImprovement() {
        return areasForImprovement;
    }

    public void setAreasForImprovement(String areasForImprovement) {
        this.areasForImprovement = areasForImprovement != null ? areasForImprovement.trim() : null;
    }

    public LocalDateTime getEvaluatedAt() {
        return evaluatedAt;
    }

    public void setEvaluatedAt(LocalDateTime evaluatedAt) {
        this.evaluatedAt = evaluatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<EvaluationScore> getScores() {
        return scores;
    }

    public void setScores(List<EvaluationScore> scores) {
        this.scores = scores != null ? scores : new ArrayList<>();
    }
}
