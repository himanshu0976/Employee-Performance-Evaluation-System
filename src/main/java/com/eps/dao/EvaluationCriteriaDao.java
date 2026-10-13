package com.eps.dao;

import com.eps.model.EvaluationCriteria;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Evaluation Criteria.
 */
public interface EvaluationCriteriaDao extends GenericDao<EvaluationCriteria, Integer> {

    List<EvaluationCriteria> findAllActive();

    Optional<EvaluationCriteria> findByName(String name);

    boolean toggleActiveStatus(Integer id, boolean active);

    BigDecimal getTotalActiveWeightage();
}
