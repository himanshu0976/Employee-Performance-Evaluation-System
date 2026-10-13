package com.eps.dao.impl;

import com.eps.dao.EvaluationCycleDao;
import com.eps.exception.DatabaseException;
import com.eps.model.EvaluationCycle;
import com.eps.util.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of EvaluationCycleDao using PreparedStatements.
 */
public class EvaluationCycleDaoImpl implements EvaluationCycleDao {
    private static final Logger logger = LoggerFactory.getLogger(EvaluationCycleDaoImpl.class);

    private static final String SELECT_BASE =
            "SELECT c.id, c.title, c.description, c.start_date, c.end_date, c.status, c.created_at, " +
            "       (SELECT COUNT(*) FROM employees) as eligible_count, " +
            "       (SELECT COUNT(*) FROM evaluations ev WHERE ev.cycle_id = c.id AND ev.status IN ('SUBMITTED', 'APPROVED')) as completed_count, " +
            "       ((SELECT COUNT(*) FROM employees) - (SELECT COUNT(*) FROM evaluations ev WHERE ev.cycle_id = c.id AND ev.status IN ('SUBMITTED', 'APPROVED'))) as pending_count " +
            "FROM evaluation_cycles c ";

    @Override
    public Optional<EvaluationCycle> findById(Integer id) {
        String sql = SELECT_BASE + "WHERE c.id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToCycle(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding cycle by id {}: {}", id, e.getMessage());
            throw new DatabaseException("Failed to find cycle: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<EvaluationCycle> findActiveCycle() {
        String sql = SELECT_BASE + "WHERE c.status = 'ACTIVE' ORDER BY c.start_date DESC LIMIT 1";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return Optional.of(mapRowToCycle(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding active cycle: {}", e.getMessage());
            throw new DatabaseException("Failed to find active evaluation cycle: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<EvaluationCycle> findAll() {
        List<EvaluationCycle> list = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY c.start_date DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRowToCycle(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding all cycles: {}", e.getMessage());
            throw new DatabaseException("Failed to list evaluation cycles: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<EvaluationCycle> findByStatus(EvaluationCycle.Status status) {
        List<EvaluationCycle> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE c.status = ? ORDER BY c.start_date DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToCycle(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding cycles by status: {}", e.getMessage());
            throw new DatabaseException("Failed to find cycles by status: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public EvaluationCycle save(EvaluationCycle cycle) {
        String sql = "INSERT INTO evaluation_cycles (title, description, start_date, end_date, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cycle.getTitle());
            ps.setString(2, cycle.getDescription());
            ps.setDate(3, Date.valueOf(cycle.getStartDate()));
            ps.setDate(4, Date.valueOf(cycle.getEndDate()));
            ps.setString(5, cycle.getStatus().name());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        cycle.setId(keys.getInt(1));
                    }
                }
            }
            return cycle;
        } catch (SQLException e) {
            logger.error("Error saving evaluation cycle: {}", e.getMessage());
            throw new DatabaseException("Failed to save evaluation cycle: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(EvaluationCycle cycle) {
        String sql = "UPDATE evaluation_cycles SET title = ?, description = ?, start_date = ?, end_date = ?, status = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cycle.getTitle());
            ps.setString(2, cycle.getDescription());
            ps.setDate(3, Date.valueOf(cycle.getStartDate()));
            ps.setDate(4, Date.valueOf(cycle.getEndDate()));
            ps.setString(5, cycle.getStatus().name());
            ps.setInt(6, cycle.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating cycle {}: {}", cycle.getId(), e.getMessage());
            throw new DatabaseException("Failed to update evaluation cycle: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateStatus(Integer cycleId, EvaluationCycle.Status status) {
        String sql = "UPDATE evaluation_cycles SET status = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, cycleId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating cycle status: {}", e.getMessage());
            throw new DatabaseException("Failed to update cycle status: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM evaluation_cycles WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting evaluation cycle: {}", e.getMessage());
            throw new DatabaseException("Failed to delete evaluation cycle: " + e.getMessage(), e);
        }
    }

    @Override
    public int countTotal() {
        String sql = "SELECT COUNT(*) FROM evaluation_cycles";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error counting cycles: {}", e.getMessage());
            throw new DatabaseException("Failed to count evaluation cycles: " + e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public int countByStatus(EvaluationCycle.Status status) {
        String sql = "SELECT COUNT(*) FROM evaluation_cycles WHERE status = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.error("Error counting cycles by status: {}", e.getMessage());
            throw new DatabaseException("Failed to count cycles: " + e.getMessage(), e);
        }
        return 0;
    }

    private EvaluationCycle mapRowToCycle(ResultSet rs) throws SQLException {
        EvaluationCycle c = new EvaluationCycle();
        c.setId(rs.getInt("id"));
        c.setTitle(rs.getString("title"));
        c.setDescription(rs.getString("description"));
        Date sDate = rs.getDate("start_date");
        if (sDate != null) c.setStartDate(sDate.toLocalDate());
        Date eDate = rs.getDate("end_date");
        if (eDate != null) c.setEndDate(eDate.toLocalDate());
        String st = rs.getString("status");
        if (st != null) c.setStatus(EvaluationCycle.Status.valueOf(st));
        c.setTotalEligibleEmployees(rs.getInt("eligible_count"));
        c.setCompletedEvaluationsCount(rs.getInt("completed_count"));
        c.setPendingEvaluationsCount(Math.max(0, rs.getInt("pending_count")));

        java.sql.Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) {
            c.setCreatedAt(createdTs.toLocalDateTime());
        }
        return c;
    }
}
