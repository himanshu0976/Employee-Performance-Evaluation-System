package com.eps.dao.impl;

import com.eps.dao.ReportDao;
import com.eps.exception.DatabaseException;
import com.eps.model.CriteriaAverageDTO;
import com.eps.model.DepartmentReportDTO;
import com.eps.model.Evaluation;
import com.eps.model.OrganizationStatsDTO;
import com.eps.util.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of ReportDao generating organization analytics, Chart.js feeds, and metrics.
 */
public class ReportDaoImpl implements ReportDao {
    private static final Logger logger = LoggerFactory.getLogger(ReportDaoImpl.class);

    @Override
    public List<DepartmentReportDTO> getDepartmentReports(Integer cycleId) {
        List<DepartmentReportDTO> reports = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT d.id as dept_id, d.name as dept_name, d.code as dept_code, " +
                "       COUNT(DISTINCT e.id) as total_employees, " +
                "       COUNT(DISTINCT CASE WHEN ev.status IN ('SUBMITTED', 'APPROVED') THEN ev.id END) as completed_evaluations, " +
                "       AVG(CASE WHEN ev.status IN ('SUBMITTED', 'APPROVED') THEN ev.total_score END) as avg_score " +
                "FROM departments d " +
                "LEFT JOIN employees e ON d.id = e.department_id " +
                "LEFT JOIN evaluations ev ON e.id = ev.employee_id "
        );

        if (cycleId != null && cycleId > 0) {
            sql.append("AND ev.cycle_id = ? ");
        }
        sql.append("GROUP BY d.id, d.name, d.code ORDER BY d.name ASC");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            if (cycleId != null && cycleId > 0) {
                ps.setInt(1, cycleId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DepartmentReportDTO dto = new DepartmentReportDTO();
                    dto.setDepartmentId(rs.getInt("dept_id"));
                    dto.setDepartmentName(rs.getString("dept_name"));
                    dto.setDepartmentCode(rs.getString("dept_code"));
                    dto.setTotalEmployees(rs.getInt("total_employees"));
                    dto.setCompletedEvaluations(rs.getInt("completed_evaluations"));

                    BigDecimal avg = rs.getBigDecimal("avg_score");
                    if (avg != null) {
                        dto.setAverageScore(avg.setScale(2, RoundingMode.HALF_UP));
                        dto.setAverageRating(Evaluation.determineRating(dto.getAverageScore()));
                    } else {
                        dto.setAverageScore(BigDecimal.ZERO);
                        dto.setAverageRating("N/A");
                    }
                    reports.add(dto);
                }
            }
        } catch (SQLException e) {
            logger.error("Error generating department reports: {}", e.getMessage());
            throw new DatabaseException("Failed to generate department reports: " + e.getMessage(), e);
        }
        return reports;
    }

    @Override
    public List<CriteriaAverageDTO> getCriteriaAverages(Integer cycleId, Integer departmentId) {
        List<CriteriaAverageDTO> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT c.id, c.name, c.weightage, AVG(s.score) as avg_score " +
                "FROM evaluation_criteria c " +
                "LEFT JOIN evaluation_scores s ON c.id = s.criteria_id " +
                "LEFT JOIN evaluations ev ON s.evaluation_id = ev.id " +
                "LEFT JOIN employees emp ON ev.employee_id = emp.id " +
                "WHERE c.is_active = 1 "
        );

        List<Object> params = new ArrayList<>();
        if (cycleId != null && cycleId > 0) {
            sql.append("AND ev.cycle_id = ? ");
            params.add(cycleId);
        }
        if (departmentId != null && departmentId > 0) {
            sql.append("AND emp.department_id = ? ");
            params.add(departmentId);
        }
        sql.append("GROUP BY c.id, c.name, c.weightage ORDER BY c.id ASC");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CriteriaAverageDTO dto = new CriteriaAverageDTO();
                    dto.setCriteriaId(rs.getInt("id"));
                    dto.setCriteriaName(rs.getString("name"));
                    dto.setWeightage(rs.getBigDecimal("weightage"));
                    BigDecimal avg = rs.getBigDecimal("avg_score");
                    dto.setAverageScore(avg != null ? avg.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            logger.error("Error calculating criteria averages: {}", e.getMessage());
            throw new DatabaseException("Failed to calculate criteria averages: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public OrganizationStatsDTO getOrganizationStats(Integer cycleId) {
        OrganizationStatsDTO stats = new OrganizationStatsDTO();

        try (Connection conn = DBUtil.getConnection()) {
            // Count total employees, departments, cycles, managers
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT (SELECT COUNT(*) FROM employees) as emp_count, " +
                    "       (SELECT COUNT(*) FROM users WHERE role = 'MANAGER' AND is_active = 1) as mgr_count, " +
                    "       (SELECT COUNT(*) FROM departments) as dept_count, " +
                    "       (SELECT COUNT(*) FROM evaluation_cycles) as cycle_count, " +
                    "       (SELECT COUNT(*) FROM evaluation_cycles WHERE status = 'ACTIVE') as active_cycle_count");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.setTotalEmployees(rs.getInt("emp_count"));
                    stats.setTotalManagers(rs.getInt("mgr_count"));
                    stats.setTotalDepartments(rs.getInt("dept_count"));
                    stats.setTotalCycles(rs.getInt("cycle_count"));
                    stats.setActiveCyclesCount(rs.getInt("active_cycle_count"));
                }
            }

            // In cycle scope (or active cycle if not specified)
            StringBuilder evalSql = new StringBuilder(
                    "SELECT COUNT(*) as total_evals, " +
                    "       AVG(total_score) as avg_score " +
                    "FROM evaluations WHERE status IN ('SUBMITTED', 'APPROVED') "
            );
            if (cycleId != null && cycleId > 0) {
                evalSql.append("AND cycle_id = ?");
            }

            try (PreparedStatement ps = conn.prepareStatement(evalSql.toString())) {
                if (cycleId != null && cycleId > 0) {
                    ps.setInt(1, cycleId);
                }
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int completed = rs.getInt("total_evals");
                        stats.setCompletedEvaluationsCount(completed);
                        BigDecimal avg = rs.getBigDecimal("avg_score");
                        stats.setOverallAverageScore(avg != null ? avg.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);

                        int totalEmp = stats.getTotalEmployees();
                        stats.setPendingEvaluationsCount(Math.max(0, totalEmp - completed));
                        if (totalEmp > 0) {
                            stats.setCompletionRatePercent(Math.round(((double) completed / totalEmp) * 1000.0) / 10.0);
                        }
                    }
                }
            }

            // Rating distribution
            stats.setRatingDistribution(getRatingDistribution(cycleId, null));

        } catch (SQLException e) {
            logger.error("Error gathering organization stats: {}", e.getMessage());
            throw new DatabaseException("Failed to get organization stats: " + e.getMessage(), e);
        }

        return stats;
    }

    @Override
    public Map<String, Integer> getRatingDistribution(Integer cycleId, Integer departmentId) {
        Map<String, Integer> dist = new LinkedHashMap<>();
        dist.put(Evaluation.RATING_OUTSTANDING, 0);
        dist.put(Evaluation.RATING_EXCEEDS, 0);
        dist.put(Evaluation.RATING_MEETS, 0);
        dist.put(Evaluation.RATING_NEEDS_IMPROVEMENT, 0);
        dist.put(Evaluation.RATING_UNSATISFACTORY, 0);

        StringBuilder sql = new StringBuilder(
                "SELECT ev.rating, COUNT(*) as cnt " +
                "FROM evaluations ev " +
                "JOIN employees emp ON ev.employee_id = emp.id " +
                "WHERE ev.status IN ('SUBMITTED', 'APPROVED') AND ev.rating IS NOT NULL "
        );

        List<Object> params = new ArrayList<>();
        if (cycleId != null && cycleId > 0) {
            sql.append("AND ev.cycle_id = ? ");
            params.add(cycleId);
        }
        if (departmentId != null && departmentId > 0) {
            sql.append("AND emp.department_id = ? ");
            params.add(departmentId);
        }
        sql.append("GROUP BY ev.rating");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String rating = rs.getString("rating");
                    int count = rs.getInt("cnt");
                    if (dist.containsKey(rating)) {
                        dist.put(rating, count);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting rating distribution: {}", e.getMessage());
            throw new DatabaseException("Failed to get rating distribution: " + e.getMessage(), e);
        }
        return dist;
    }
}
