package com.eps.service;

import com.eps.model.Evaluation;
import com.eps.model.EvaluationScore;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for creating, calculating, saving, and submitting employee evaluations.
 */
public interface EvaluationService {

    Evaluation getEvaluationById(Integer id);

    Optional<Evaluation> getEvaluationByCycleAndEmployee(Integer cycleId, Integer employeeId);

    List<Evaluation> getEvaluationsForEmployee(Integer employeeId);

    List<Evaluation> getEvaluationsByManager(Integer managerUserId);

    List<Evaluation> getEvaluationsByCycle(Integer cycleId);

    List<Evaluation> getFilteredEvaluations(Integer cycleId, Integer departmentId, String rating);

    Evaluation saveOrSubmitEvaluation(Integer cycleId, Integer employeeId, Integer evaluatorUserId,
                                      List<EvaluationScore> scores, String summaryFeedback,
                                      String strengths, String areasForImprovement, boolean submitFinal);

    void approveEvaluation(Integer evaluationId);

    void deleteEvaluation(Integer evaluationId);
}
