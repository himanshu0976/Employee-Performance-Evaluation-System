package com.eps.dao;

import com.eps.model.EvaluationScore;

import java.sql.Connection;
import java.util.List;

/**
 * Data Access Object interface for individual criterion scores.
 */
public interface EvaluationScoreDao {

    List<EvaluationScore> findByEvaluationId(Integer evaluationId);

    void saveScoresTransactional(Connection conn, Integer evaluationId, List<EvaluationScore> scores);

    void updateScoresTransactional(Connection conn, Integer evaluationId, List<EvaluationScore> scores);

    boolean deleteByEvaluationId(Integer evaluationId);
}
