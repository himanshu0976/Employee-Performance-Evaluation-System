package com.eps.dao.impl;

import com.eps.dao.EvaluationCriteriaDao;
import com.eps.exception.DatabaseException;
import com.eps.model.EvaluationCriteria;
import com.eps.util.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of EvaluationCriteriaDao using PreparedStatements.
 */
public class EvaluationCriteriaDaoImpl implements EvaluationCriteriaDao {
    private static final Logger logger = LoggerFactory.getLogger(EvaluationCriteriaDaoImpl.class);

    private static final String SELECT_BASE =
            "SELECT c.id, c.name, c.description, c.weightage, c.is_active, c.created_at " +
            "FROM evaluation_criteria c ";

    @Override
    public Optional<EvaluationCriteria> findById(Integer id) {
        String sql = SELECT_BASE + "WHERE c.id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToCriteria(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding criteria by id {}: {}", id, e.getMessage());
            throw new DatabaseException("Failed to find evaluation criterion: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<EvaluationCriteria> findByName(String name) {
        String sql = SELECT_BASE + "WHERE LOWER(c.name) = LOWER(?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToCriteria(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding criteria by name {}: {}", name, e.getMessage());
            throw new DatabaseException("Failed to find criterion: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<EvaluationCriteria> findAll() {
        List<EvaluationCriteria> list = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY c.id ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRowToCriteria(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding all criteria: {}", e.getMessage());
            throw new DatabaseException("Failed to list evaluation criteria: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<EvaluationCriteria> findAllActive() {
        List<EvaluationCriteria> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE c.is_active = 1 ORDER BY c.id ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRowToCriteria(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding active criteria: {}", e.getMessage());
            throw new DatabaseException("Failed to list active criteria: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public EvaluationCriteria save(EvaluationCriteria criteria) {
        String sql = "INSERT INTO evaluation_criteria (name, description, weightage, is_active) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, criteria.getName());
            ps.setString(2, criteria.getDescription());
            ps.setBigDecimal(3, criteria.getWeightage());
            ps.setBoolean(4, criteria.isActive());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        criteria.setId(keys.getInt(1));
                    }
                }
            }
            return criteria;
        } catch (SQLException e) {
            logger.error("Error saving criteria {}: {}", criteria.getName(), e.getMessage());
            throw new DatabaseException("Failed to save evaluation criterion: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(EvaluationCriteria criteria) {
        String sql = "UPDATE evaluation_criteria SET name = ?, description = ?, weightage = ?, is_active = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, criteria.getName());
            ps.setString(2, criteria.getDescription());
            ps.setBigDecimal(3, criteria.getWeightage());
            ps.setBoolean(4, criteria.isActive());
            ps.setInt(5, criteria.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating criteria {}: {}", criteria.getId(), e.getMessage());
            throw new DatabaseException("Failed to update evaluation criterion: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean toggleActiveStatus(Integer id, boolean active) {
        String sql = "UPDATE evaluation_criteria SET is_active = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, active);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error toggling criteria active status: {}", e.getMessage());
            throw new DatabaseException("Failed to toggle criterion status: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM evaluation_criteria WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting criteria: {}", e.getMessage());
            throw new DatabaseException("Failed to delete criterion: " + e.getMessage(), e);
        }
    }

    @Override
    public BigDecimal getTotalActiveWeightage() {
        String sql = "SELECT COALESCE(SUM(weightage), 0) FROM evaluation_criteria WHERE is_active = 1";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
        } catch (SQLException e) {
            logger.error("Error calculating total active weightage: {}", e.getMessage());
            throw new DatabaseException("Failed to sum criteria weightage: " + e.getMessage(), e);
        }
        return BigDecimal.ZERO;
    }

    private EvaluationCriteria mapRowToCriteria(ResultSet rs) throws SQLException {
        EvaluationCriteria c = new EvaluationCriteria();
        c.setId(rs.getInt("id"));
        c.setName(rs.getString("name"));
        c.setDescription(rs.getString("description"));
        c.setWeightage(rs.getBigDecimal("weightage"));
        c.setActive(rs.getBoolean("is_active"));
        java.sql.Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) {
            c.setCreatedAt(createdTs.toLocalDateTime());
        }
        return c;
    }
}
