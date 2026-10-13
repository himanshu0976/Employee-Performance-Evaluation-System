package com.eps.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Criterion performance average across an evaluation cycle or department.
 */
public class CriteriaAverageDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer criteriaId;
    private String criteriaName;
    private BigDecimal weightage;
    private BigDecimal averageScore = BigDecimal.ZERO;

    public CriteriaAverageDTO() {
    }

    public CriteriaAverageDTO(Integer criteriaId, String criteriaName, BigDecimal weightage, BigDecimal averageScore) {
        this.criteriaId = criteriaId;
        this.criteriaName = criteriaName;
        this.weightage = weightage;
        this.averageScore = averageScore;
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

    public BigDecimal getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(BigDecimal averageScore) {
        this.averageScore = averageScore;
    }
}
