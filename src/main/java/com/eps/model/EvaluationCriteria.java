package com.eps.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Evaluation Criteria model.
 * Each criterion has a name, description, weightage (percentage), and active status.
 */
public class EvaluationCriteria implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String name;
    private String description;
    private BigDecimal weightage = new BigDecimal("16.67");
    private boolean active = true;
    private LocalDateTime createdAt;

    public EvaluationCriteria() {
    }

    public EvaluationCriteria(Integer id, String name, String description, BigDecimal weightage, boolean active) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.weightage = weightage;
        this.active = active;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name != null ? name.trim() : null;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description != null ? description.trim() : null;
    }

    public BigDecimal getWeightage() {
        return weightage;
    }

    public void setWeightage(BigDecimal weightage) {
        this.weightage = weightage;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "EvaluationCriteria{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", weightage=" + weightage +
                ", active=" + active +
                '}';
    }
}
