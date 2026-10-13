package com.eps.dao;

import com.eps.model.Evaluation;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Evaluation records.
 */
public interface EvaluationDao extends GenericDao<Evaluation, Integer> {

    Optional<Evaluation> findByCycleAndEmployee(Integer cycleId, Integer employeeId);

    List<Evaluation> findByEmployeeId(Integer employeeId);

    List<Evaluation> findByEvaluatorId(Integer evaluatorId);

    List<Evaluation> findByCycleId(Integer cycleId);

    List<Evaluation> findFiltered(Integer cycleId, Integer departmentId, String rating);

    int countCompletedInCycle(Integer cycleId);

    int countPendingInCycle(Integer cycleId);

    boolean saveWithScores(Evaluation evaluation);

    boolean updateWithScores(Evaluation evaluation);
}
