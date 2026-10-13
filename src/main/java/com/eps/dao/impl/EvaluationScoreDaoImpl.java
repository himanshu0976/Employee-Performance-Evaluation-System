package com.eps.dao.impl;

import com.eps.dao.EvaluationScoreDao;
import com.eps.exception.DatabaseException;
import com.eps.model.EvaluationScore;
import com.eps.util.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of EvaluationScoreDao using PreparedStatements and transactional batch execution.
 */
public class EvaluationScoreDaoImpl implements EvaluationScoreDao {
    private static final Logger logger = LoggerFactory.getLogger(EvaluationScoreDaoImpl.class);

    private static final String SELECT_BASE =
            "SELECT s.id, s.evaluation_id, s.criteria_id, s.score, s.feedback, " +
            "       c.name as criteria_name, c.weightage " +
            "FROM evaluation_scores s " +
            "JOIN evaluation_criteria c ON s.criteria_id = c.id ";

    @Override
    public List<EvaluationScore> findByEvaluationId(Integer evaluationId) {
        List<EvaluationScore> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE s.evaluation_id = ? ORDER BY c.id ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, evaluationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToScore(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding scores for evaluation {}: {}", evaluationId, e.getMessage());
            throw new DatabaseException("Failed to find evaluation scores: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public void saveScoresTransactional(Connection conn, Integer evaluationId, List<EvaluationScore> scores) {
        String sql = "INSERT INTO evaluation_scores (evaluation_id, criteria_id, score, feedback) " +
                     "VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE score = VALUES(score), feedback = VALUES(feedback)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (EvaluationScore s : scores) {
                ps.setInt(1, evaluationId);
                ps.setInt(2, s.getCriteriaId());
                ps.setBigDecimal(3, s.getScore());
                ps.setString(4, s.getFeedback());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            logger.error("Error saving batch scores for evaluation {}: {}", evaluationId, e.getMessage());
            throw new DatabaseException("Failed to save evaluation scores batch: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateScoresTransactional(Connection conn, Integer evaluationId, List<EvaluationScore> scores) {
        saveScoresTransactional(conn, evaluationId, scores);
    }

    @Override
    public boolean deleteByEvaluationId(Integer evaluationId) {
        String sql = "DELETE FROM evaluation_scores WHERE evaluation_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, evaluationId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting scores for evaluation {}: {}", evaluationId, e.getMessage());
            throw new DatabaseException("Failed to delete scores: " + e.getMessage(), e);
        }
    }

    private EvaluationScore mapRowToScore(ResultSet rs) throws SQLException {
        EvaluationScore s = new EvaluationScore();
        s.setId(rs.getInt("id"));
        s.setEvaluationId(rs.getInt("evaluation_id"));
        s.setCriteriaId(rs.getInt("criteria_id"));
        s.setCriteriaName(rs.getString("criteria_name"));
        s.setWeightage(rs.getBigDecimal("weightage"));
        s.setScore(rs.getBigDecimal("score"));
        s.setFeedback(rs.getString("feedback"));
        return s;
    }
}
