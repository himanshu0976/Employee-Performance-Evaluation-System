package com.eps.service;

import com.eps.model.EvaluationCriteria;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface for managing evaluation criteria and their weightages.
 */
public interface EvaluationCriteriaService {

    List<EvaluationCriteria> getAllCriteria();

    List<EvaluationCriteria> getActiveCriteria();

    EvaluationCriteria getCriteriaById(Integer id);

    EvaluationCriteria createCriteria(EvaluationCriteria criteria);

    void updateCriteria(EvaluationCriteria criteria);

    void toggleStatus(Integer id, boolean active);

    void deleteCriteria(Integer id);

    BigDecimal getTotalActiveWeightage();
}
