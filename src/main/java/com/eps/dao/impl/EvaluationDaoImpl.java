package com.eps.dao.impl;

import com.eps.dao.EvaluationDao;
import com.eps.dao.EvaluationScoreDao;
import com.eps.exception.DatabaseException;
import com.eps.model.Evaluation;
import com.eps.util.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of EvaluationDao using PreparedStatements and JDBC transactions.
 */
public class EvaluationDaoImpl implements EvaluationDao {
    private static final Logger logger = LoggerFactory.getLogger(EvaluationDaoImpl.class);
    private final EvaluationScoreDao evaluationScoreDao = new EvaluationScoreDaoImpl();

    private static final String SELECT_BASE =
            "SELECT ev.id, ev.cycle_id, ev.employee_id, ev.evaluator_id, ev.total_score, ev.rating, ev.status, " +
            "       ev.summary_feedback, ev.strengths, ev.areas_for_improvement, ev.evaluated_at, ev.created_at, ev.updated_at, " +
            "       c.title as cycle_title, c.status as cycle_status, " +
            "       u.full_name as employee_name, emp.employee_code, emp.designation, d.name as department_name, " +
            "       m.full_name as evaluator_name " +
            "FROM evaluations ev " +
            "JOIN evaluation_cycles c ON ev.cycle_id = c.id " +
            "JOIN employees emp ON ev.employee_id = emp.id " +
            "JOIN users u ON emp.user_id = u.id " +
            "LEFT JOIN departments d ON emp.department_id = d.id " +
            "JOIN users m ON ev.evaluator_id = m.id ";

    @Override
    public Optional<Evaluation> findById(Integer id) {
        String sql = SELECT_BASE + "WHERE ev.id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Evaluation eval = mapRowToEvaluation(rs);
                    eval.setScores(evaluationScoreDao.findByEvaluationId(eval.getId()));
                    return Optional.of(eval);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding evaluation by id {}: {}", id, e.getMessage());
            throw new DatabaseException("Failed to find evaluation: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Evaluation> findByCycleAndEmployee(Integer cycleId, Integer employeeId) {
        String sql = SELECT_BASE + "WHERE ev.cycle_id = ? AND ev.employee_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cycleId);
            ps.setInt(2, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Evaluation eval = mapRowToEvaluation(rs);
                    eval.setScores(evaluationScoreDao.findByEvaluationId(eval.getId()));
                    return Optional.of(eval);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding evaluation by cycle {} and employee {}: {}", cycleId, employeeId, e.getMessage());
            throw new DatabaseException("Failed to find evaluation for cycle and employee: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Evaluation> findByEmployeeId(Integer employeeId) {
        List<Evaluation> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE ev.employee_id = ? ORDER BY c.start_date DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Evaluation eval = mapRowToEvaluation(rs);
                    eval.setScores(evaluationScoreDao.findByEvaluationId(eval.getId()));
                    list.add(eval);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding evaluations for employee {}: {}", employeeId, e.getMessage());
            throw new DatabaseException("Failed to list employee evaluations: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Evaluation> findByEvaluatorId(Integer evaluatorId) {
        List<Evaluation> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE ev.evaluator_id = ? ORDER BY c.start_date DESC, u.full_name ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, evaluatorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Evaluation eval = mapRowToEvaluation(rs);
                    eval.setScores(evaluationScoreDao.findByEvaluationId(eval.getId()));
                    list.add(eval);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding evaluations by evaluator {}: {}", evaluatorId, e.getMessage());
            throw new DatabaseException("Failed to list manager's evaluations: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Evaluation> findByCycleId(Integer cycleId) {
        List<Evaluation> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE ev.cycle_id = ? ORDER BY u.full_name ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cycleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Evaluation eval = mapRowToEvaluation(rs);
                    eval.setScores(evaluationScoreDao.findByEvaluationId(eval.getId()));
                    list.add(eval);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding evaluations for cycle {}: {}", cycleId, e.getMessage());
            throw new DatabaseException("Failed to list cycle evaluations: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Evaluation> findFiltered(Integer cycleId, Integer departmentId, String rating) {
        List<Evaluation> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(SELECT_BASE).append("WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (cycleId != null && cycleId > 0) {
            sql.append("AND ev.cycle_id = ? ");
            params.add(cycleId);
        }
        if (departmentId != null && departmentId > 0) {
            sql.append("AND emp.department_id = ? ");
            params.add(departmentId);
        }
        if (rating != null && !rating.isBlank()) {
            sql.append("AND ev.rating = ? ");
            params.add(rating);
        }
        sql.append("ORDER BY ev.total_score DESC, u.full_name ASC");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Evaluation eval = mapRowToEvaluation(rs);
                    eval.setScores(evaluationScoreDao.findByEvaluationId(eval.getId()));
                    list.add(eval);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding filtered evaluations: {}", e.getMessage());
            throw new DatabaseException("Failed to filter evaluations: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Evaluation> findAll() {
        return findFiltered(null, null, null);
    }

    @Override
    public Evaluation save(Evaluation entity) {
        saveWithScores(entity);
        return entity;
    }

    @Override
    public boolean saveWithScores(Evaluation eval) {
        String sql = "INSERT INTO evaluations (cycle_id, employee_id, evaluator_id, total_score, rating, status, " +
                     "summary_feedback, strengths, areas_for_improvement, evaluated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, eval.getCycleId());
                ps.setInt(2, eval.getEmployeeId());
                ps.setInt(3, eval.getEvaluatorId());
                ps.setBigDecimal(4, eval.getTotalScore());
                ps.setString(5, eval.getRating());
                ps.setString(6, eval.getStatus().name());
                ps.setString(7, eval.getSummaryFeedback());
                ps.setString(8, eval.getStrengths());
                ps.setString(9, eval.getAreasForImprovement());
                if (eval.getEvaluatedAt() != null) {
                    ps.setTimestamp(10, Timestamp.valueOf(eval.getEvaluatedAt()));
                } else {
                    ps.setNull(10, java.sql.Types.TIMESTAMP);
                }

                int affected = ps.executeUpdate();
                if (affected > 0) {
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) {
                            eval.setId(keys.getInt(1));
                        }
                    }
                }
            }

            if (eval.getScores() != null && !eval.getScores().isEmpty()) {
                evaluationScoreDao.saveScoresTransactional(conn, eval.getId(), eval.getScores());
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            logger.error("Transaction failed saving evaluation: {}", e.getMessage());
            throw new DatabaseException("Failed to save evaluation and scores: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    @Override
    public boolean update(Evaluation entity) {
        return updateWithScores(entity);
    }

    @Override
    public boolean updateWithScores(Evaluation eval) {
        String sql = "UPDATE evaluations SET total_score = ?, rating = ?, status = ?, " +
                     "summary_feedback = ?, strengths = ?, areas_for_improvement = ?, evaluated_at = ? " +
                     "WHERE id = ?";
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setBigDecimal(1, eval.getTotalScore());
                ps.setString(2, eval.getRating());
                ps.setString(3, eval.getStatus().name());
                ps.setString(4, eval.getSummaryFeedback());
                ps.setString(5, eval.getStrengths());
                ps.setString(6, eval.getAreasForImprovement());
                if (eval.getEvaluatedAt() != null) {
                    ps.setTimestamp(7, Timestamp.valueOf(eval.getEvaluatedAt()));
                } else {
                    ps.setNull(7, java.sql.Types.TIMESTAMP);
                }
                ps.setInt(8, eval.getId());
                ps.executeUpdate();
            }

            if (eval.getScores() != null && !eval.getScores().isEmpty()) {
                evaluationScoreDao.updateScoresTransactional(conn, eval.getId(), eval.getScores());
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            logger.error("Transaction failed updating evaluation {}: {}", eval.getId(), e.getMessage());
            throw new DatabaseException("Failed to update evaluation: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM evaluations WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting evaluation {}: {}", id, e.getMessage());
            throw new DatabaseException("Failed to delete evaluation: " + e.getMessage(), e);
        }
    }

    @Override
    public int countCompletedInCycle(Integer cycleId) {
        String sql = "SELECT COUNT(*) FROM evaluations WHERE cycle_id = ? AND status IN ('SUBMITTED', 'APPROVED')";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cycleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error counting completed evaluations: {}", e.getMessage());
            throw new DatabaseException("Failed to count evaluations: " + e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public int countPendingInCycle(Integer cycleId) {
        String sql = "SELECT (SELECT COUNT(*) FROM employees) - (SELECT COUNT(*) FROM evaluations WHERE cycle_id = ? AND status IN ('SUBMITTED', 'APPROVED'))";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cycleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Math.max(0, rs.getInt(1));
            }
        } catch (SQLException e) {
            logger.error("Error counting pending evaluations: {}", e.getMessage());
            throw new DatabaseException("Failed to count pending evaluations: " + e.getMessage(), e);
        }
        return 0;
    }

    private Evaluation mapRowToEvaluation(ResultSet rs) throws SQLException {
        Evaluation eval = new Evaluation();
        eval.setId(rs.getInt("id"));
        eval.setCycleId(rs.getInt("cycle_id"));
        eval.setEmployeeId(rs.getInt("employee_id"));
        eval.setEvaluatorId(rs.getInt("evaluator_id"));
        eval.setTotalScore(rs.getBigDecimal("total_score"));
        eval.setRating(rs.getString("rating"));
        eval.setStatus(Evaluation.Status.valueOf(rs.getString("status")));
        eval.setSummaryFeedback(rs.getString("summary_feedback"));
        eval.setStrengths(rs.getString("strengths"));
        eval.setAreasForImprovement(rs.getString("areas_for_improvement"));

        Timestamp evalTs = rs.getTimestamp("evaluated_at");
        if (evalTs != null) {
            eval.setEvaluatedAt(evalTs.toLocalDateTime());
        }
        Timestamp cTs = rs.getTimestamp("created_at");
        if (cTs != null) {
            eval.setCreatedAt(cTs.toLocalDateTime());
        }
        Timestamp uTs = rs.getTimestamp("updated_at");
        if (uTs != null) {
            eval.setUpdatedAt(uTs.toLocalDateTime());
        }

        eval.setCycleTitle(rs.getString("cycle_title"));
        eval.setCycleStatus(rs.getString("cycle_status"));
        eval.setEmployeeName(rs.getString("employee_name"));
        eval.setEmployeeCode(rs.getString("employee_code"));
        eval.setDesignation(rs.getString("designation"));
        eval.setDepartmentName(rs.getString("department_name"));
        eval.setEvaluatorName(rs.getString("evaluator_name"));

        return eval;
    }
}
