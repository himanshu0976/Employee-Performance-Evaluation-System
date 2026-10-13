package com.eps.dao;

import com.eps.model.EvaluationCycle;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Evaluation Cycle operations.
 */
public interface EvaluationCycleDao extends GenericDao<EvaluationCycle, Integer> {

    Optional<EvaluationCycle> findActiveCycle();

    List<EvaluationCycle> findByStatus(EvaluationCycle.Status status);

    boolean updateStatus(Integer cycleId, EvaluationCycle.Status status);

    int countTotal();

    int countByStatus(EvaluationCycle.Status status);
}
