package com.eps.service;

import com.eps.model.EvaluationCycle;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing performance review cycles.
 */
public interface EvaluationCycleService {

    List<EvaluationCycle> getAllCycles();

    EvaluationCycle getCycleById(Integer id);

    Optional<EvaluationCycle> getActiveCycle();

    EvaluationCycle createCycle(EvaluationCycle cycle);

    void updateCycle(EvaluationCycle cycle);

    void setCycleStatus(Integer cycleId, EvaluationCycle.Status status);

    void deleteCycle(Integer cycleId);
}
