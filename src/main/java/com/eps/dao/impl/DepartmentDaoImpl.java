package com.eps.dao.impl;

import com.eps.dao.DepartmentDao;
import com.eps.exception.DatabaseException;
import com.eps.model.Department;
import com.eps.util.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of DepartmentDao using PreparedStatements.
 */
public class DepartmentDaoImpl implements DepartmentDao {
    private static final Logger logger = LoggerFactory.getLogger(DepartmentDaoImpl.class);

    @Override
    public Optional<Department> findById(Integer id) {
        String sql = "SELECT d.id, d.name, d.code, d.description, d.created_at, COUNT(e.id) as emp_count " +
                     "FROM departments d " +
                     "LEFT JOIN employees e ON d.id = e.department_id " +
                     "WHERE d.id = ? " +
                     "GROUP BY d.id, d.name, d.code, d.description, d.created_at";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToDepartment(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding department by id {}: {}", id, e.getMessage());
            throw new DatabaseException("Failed to find department: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Department> findByName(String name) {
        String sql = "SELECT d.id, d.name, d.code, d.description, d.created_at, 0 as emp_count " +
                     "FROM departments d WHERE LOWER(d.name) = LOWER(?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToDepartment(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding department by name: {}", e.getMessage());
            throw new DatabaseException("Failed to find department by name: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Department> findByCode(String code) {
        String sql = "SELECT d.id, d.name, d.code, d.description, d.created_at, 0 as emp_count " +
                     "FROM departments d WHERE UPPER(d.code) = UPPER(?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToDepartment(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding department by code: {}", e.getMessage());
            throw new DatabaseException("Failed to find department by code: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Department> findAll() {
        return findAllWithEmployeeCount();
    }

    @Override
    public List<Department> findAllWithEmployeeCount() {
        List<Department> list = new ArrayList<>();
        String sql = "SELECT d.id, d.name, d.code, d.description, d.created_at, COUNT(e.id) as emp_count " +
                     "FROM departments d " +
                     "LEFT JOIN employees e ON d.id = e.department_id " +
                     "GROUP BY d.id, d.name, d.code, d.description, d.created_at " +
                     "ORDER BY d.name ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRowToDepartment(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding all departments: {}", e.getMessage());
            throw new DatabaseException("Failed to list departments: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public Department save(Department dept) {
        String sql = "INSERT INTO departments (name, code, description) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, dept.getName());
            ps.setString(2, dept.getCode());
            ps.setString(3, dept.getDescription());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        dept.setId(keys.getInt(1));
                    }
                }
            }
            return dept;
        } catch (SQLException e) {
            logger.error("Error saving department {}: {}", dept.getName(), e.getMessage());
            throw new DatabaseException("Failed to save department: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(Department dept) {
        String sql = "UPDATE departments SET name = ?, code = ?, description = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dept.getName());
            ps.setString(2, dept.getCode());
            ps.setString(3, dept.getDescription());
            ps.setInt(4, dept.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating department {}: {}", dept.getId(), e.getMessage());
            throw new DatabaseException("Failed to update department: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM departments WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting department {}: {}", id, e.getMessage());
            throw new DatabaseException("Failed to delete department: " + e.getMessage(), e);
        }
    }

    @Override
    public int countTotal() {
        String sql = "SELECT COUNT(*) FROM departments";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error counting departments: {}", e.getMessage());
            throw new DatabaseException("Failed to count departments: " + e.getMessage(), e);
        }
        return 0;
    }

    private Department mapRowToDepartment(ResultSet rs) throws SQLException {
        Department d = new Department();
        d.setId(rs.getInt("id"));
        d.setName(rs.getString("name"));
        d.setCode(rs.getString("code"));
        d.setDescription(rs.getString("description"));
        d.setEmployeeCount(rs.getInt("emp_count"));
        java.sql.Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            d.setCreatedAt(ts.toLocalDateTime());
        }
        return d;
    }
}
