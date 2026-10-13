package com.eps.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Score and qualitative feedback for an individual evaluation criterion.
 */
public class EvaluationScore implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer evaluationId;
    private Integer criteriaId;
    private String criteriaName;
    private BigDecimal weightage;
    private BigDecimal score; // 1.0 to 5.0
    private String feedback;

    public EvaluationScore() {
    }

    public EvaluationScore(Integer evaluationId, Integer criteriaId, BigDecimal score, String feedback) {
        this.evaluationId = evaluationId;
        this.criteriaId = criteriaId;
        this.score = score;
        this.feedback = feedback;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEvaluationId() {
        return evaluationId;
    }

    public void setEvaluationId(Integer evaluationId) {
        this.evaluationId = evaluationId;
    }

    public Integer getCriteriaId() {
        return criteriaId;
    }

    public void setCriteriaId(Integer criteriaId) {
        this.criteriaId = criteriaId;
    }

    public String getCriteriaName() {
        return criteriaName;
    }

    public void setCriteriaName(String criteriaName) {
        this.criteriaName = criteriaName;
    }

    public BigDecimal getWeightage() {
        return weightage;
    }

    public void setWeightage(BigDecimal weightage) {
        this.weightage = weightage;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback != null ? feedback.trim() : null;
    }

    @Override
    public String toString() {
        return "EvaluationScore{" +
                "criteriaId=" + criteriaId +
                ", criteriaName='" + criteriaName + '\'' +
                ", score=" + score +
                '}';
    }
}
