package com.eps.service.impl;

import com.eps.dao.EvaluationCriteriaDao;
import com.eps.dao.EvaluationCycleDao;
import com.eps.dao.EvaluationDao;
import com.eps.dao.impl.EvaluationCriteriaDaoImpl;
import com.eps.dao.impl.EvaluationCycleDaoImpl;
import com.eps.dao.impl.EvaluationDaoImpl;
import com.eps.exception.ResourceNotFoundException;
import com.eps.exception.ValidationException;
import com.eps.model.Evaluation;
import com.eps.model.EvaluationCriteria;
import com.eps.model.EvaluationCycle;
import com.eps.model.EvaluationScore;
import com.eps.service.EvaluationService;
import com.eps.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of EvaluationService implementing score calculation and rating categorization.
 */
public class EvaluationServiceImpl implements EvaluationService {
    private static final Logger logger = LoggerFactory.getLogger(EvaluationServiceImpl.class);

    private final EvaluationDao evaluationDao;
    private final EvaluationCycleDao cycleDao;
    private final EvaluationCriteriaDao criteriaDao;

    public EvaluationServiceImpl() {
        this.evaluationDao = new EvaluationDaoImpl();
        this.cycleDao = new EvaluationCycleDaoImpl();
        this.criteriaDao = new EvaluationCriteriaDaoImpl();
    }

    public EvaluationServiceImpl(EvaluationDao evaluationDao, EvaluationCycleDao cycleDao, EvaluationCriteriaDao criteriaDao) {
        this.evaluationDao = evaluationDao;
        this.cycleDao = cycleDao;
        this.criteriaDao = criteriaDao;
    }

    @Override
    public Evaluation getEvaluationById(Integer id) {
        return evaluationDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluation record not found with ID: " + id));
    }

    @Override
    public Optional<Evaluation> getEvaluationByCycleAndEmployee(Integer cycleId, Integer employeeId) {
        return evaluationDao.findByCycleAndEmployee(cycleId, employeeId);
    }

    @Override
    public List<Evaluation> getEvaluationsForEmployee(Integer employeeId) {
        return evaluationDao.findByEmployeeId(employeeId);
    }

    @Override
    public List<Evaluation> getEvaluationsByManager(Integer managerUserId) {
        return evaluationDao.findByEvaluatorId(managerUserId);
    }

    @Override
    public List<Evaluation> getEvaluationsByCycle(Integer cycleId) {
        return evaluationDao.findByCycleId(cycleId);
    }

    @Override
    public List<Evaluation> getFilteredEvaluations(Integer cycleId, Integer departmentId, String rating) {
        return evaluationDao.findFiltered(cycleId, departmentId, rating);
    }

    @Override
    public Evaluation saveOrSubmitEvaluation(Integer cycleId, Integer employeeId, Integer evaluatorUserId,
                                             List<EvaluationScore> scores, String summaryFeedback,
                                             String strengths, String areasForImprovement, boolean submitFinal) {
        // 1. Verify cycle
        EvaluationCycle cycle = cycleDao.findById(cycleId)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluation cycle not found with ID: " + cycleId));

        if (!cycle.isActive() && submitFinal) {
            throw new ValidationException("Cannot submit evaluation: Cycle '" + cycle.getTitle() + "' is not currently ACTIVE.");
        }

        // 2. Load criteria map to link weights
        List<EvaluationCriteria> activeCriteria = criteriaDao.findAllActive();
        Map<Integer, EvaluationCriteria> criteriaMap = activeCriteria.stream()
                .collect(Collectors.toMap(EvaluationCriteria::getId, c -> c));

        if (submitFinal && (scores == null || scores.isEmpty())) {
            throw new ValidationException("Scores for all active criteria must be submitted.");
        }

        // 3. Validate scores and attach criteria metadata
        if (scores != null) {
            for (EvaluationScore s : scores) {
                EvaluationCriteria c = criteriaMap.get(s.getCriteriaId());
                if (c != null) {
                    s.setCriteriaName(c.getName());
                    s.setWeightage(c.getWeightage());
                } else {
                    s.setWeightage(new BigDecimal("16.67"));
                }

                if (submitFinal) {
                    ValidationUtil.validateScore(s.getScore(), s.getCriteriaName() != null ? s.getCriteriaName() : "Criterion " + s.getCriteriaId());
                } else if (s.getScore() != null) {
                    ValidationUtil.validateScore(s.getScore(), s.getCriteriaName() != null ? s.getCriteriaName() : "Criterion");
                }
            }
        }

        // 4. Find existing or create new Evaluation instance
        Optional<Evaluation> existingOpt = evaluationDao.findByCycleAndEmployee(cycleId, employeeId);
        Evaluation evaluation = existingOpt.orElseGet(Evaluation::new);

        evaluation.setCycleId(cycleId);
        evaluation.setEmployeeId(employeeId);
        evaluation.setEvaluatorId(evaluatorUserId);
        evaluation.setSummaryFeedback(summaryFeedback);
        evaluation.setStrengths(strengths);
        evaluation.setAreasForImprovement(areasForImprovement);
        evaluation.setScores(scores);

        // 5. Calculate total weighted score and rating
        evaluation.calculateTotalScoreAndRating();

        if (submitFinal) {
            evaluation.setStatus(Evaluation.Status.SUBMITTED);
            evaluation.setEvaluatedAt(LocalDateTime.now());
        } else {
            evaluation.setStatus(Evaluation.Status.DRAFT);
        }

        // 6. Save or update
        if (evaluation.getId() == null) {
            evaluationDao.saveWithScores(evaluation);
            logger.info("Saved new evaluation for employee {} in cycle {} (status={})", employeeId, cycleId, evaluation.getStatus());
        } else {
            evaluationDao.updateWithScores(evaluation);
            logger.info("Updated evaluation {} for employee {} (status={})", evaluation.getId(), employeeId, evaluation.getStatus());
        }

        return evaluation;
    }

    @Override
    public void approveEvaluation(Integer evaluationId) {
        Evaluation eval = getEvaluationById(evaluationId);
        eval.setStatus(Evaluation.Status.APPROVED);
        evaluationDao.update(eval);
    }

    @Override
    public void deleteEvaluation(Integer evaluationId) {
        getEvaluationById(evaluationId);
        evaluationDao.deleteById(evaluationId);
    }
}
