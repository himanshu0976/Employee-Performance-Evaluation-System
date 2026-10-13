package com.eps.dao.impl;

import com.eps.dao.EmployeeDao;
import com.eps.exception.DatabaseException;
import com.eps.model.Employee;
import com.eps.model.Role;
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
 * Implementation of EmployeeDao using PreparedStatements and joins across users, departments, and managers.
 */
public class EmployeeDaoImpl implements EmployeeDao {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeDaoImpl.class);

    private static final String SELECT_BASE = 
            "SELECT e.id as emp_id, e.user_id, e.employee_code, e.department_id, e.manager_id, " +
            "       e.designation, e.hire_date, e.salary, e.created_at as emp_created_at, " +
            "       u.username, u.password_hash, u.full_name, u.email, u.phone, u.is_active, u.created_at as user_created_at, " +
            "       d.name as department_name, " +
            "       m.full_name as manager_name " +
            "FROM employees e " +
            "JOIN users u ON e.user_id = u.id " +
            "LEFT JOIN departments d ON e.department_id = d.id " +
            "LEFT JOIN users m ON e.manager_id = m.id ";

    @Override
    public Optional<Employee> findById(Integer id) {
        String sql = SELECT_BASE + "WHERE e.id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToEmployee(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding employee by id {}: {}", id, e.getMessage());
            throw new DatabaseException("Failed to find employee: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Employee> findByUserId(Integer userId) {
        String sql = SELECT_BASE + "WHERE e.user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToEmployee(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding employee by userId {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to find employee by user id: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Employee> findByEmployeeCode(String employeeCode) {
        String sql = SELECT_BASE + "WHERE UPPER(e.employee_code) = UPPER(?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employeeCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToEmployee(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding employee by code {}: {}", employeeCode, e.getMessage());
            throw new DatabaseException("Failed to find employee by code: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Employee> findAll() {
        return findAllWithDetails();
    }

    @Override
    public List<Employee> findAllWithDetails() {
        List<Employee> list = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY u.full_name ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRowToEmployee(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding all employees: {}", e.getMessage());
            throw new DatabaseException("Failed to list employees: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Employee> findByManagerUserId(Integer managerUserId) {
        List<Employee> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE e.manager_id = ? AND u.is_active = 1 ORDER BY u.full_name ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, managerUserId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToEmployee(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding employees by manager {}: {}", managerUserId, e.getMessage());
            throw new DatabaseException("Failed to list manager's employees: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Employee> findByDepartmentId(Integer departmentId) {
        List<Employee> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE e.department_id = ? ORDER BY u.full_name ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, departmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToEmployee(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding employees by department {}: {}", departmentId, e.getMessage());
            throw new DatabaseException("Failed to list department employees: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public Employee save(Employee emp) {
        String sql = "INSERT INTO employees (user_id, employee_code, department_id, manager_id, designation, hire_date, salary) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, emp.getId()); // user_id is set in emp.getId()
            ps.setString(2, emp.getEmployeeCode());
            if (emp.getDepartmentId() != null) {
                ps.setInt(3, emp.getDepartmentId());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }
            if (emp.getManagerId() != null) {
                ps.setInt(4, emp.getManagerId());
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }
            ps.setString(5, emp.getDesignation());
            ps.setDate(6, emp.getHireDate() != null ? Date.valueOf(emp.getHireDate()) : Date.valueOf(java.time.LocalDate.now()));
            ps.setBigDecimal(7, emp.getSalary() != null ? emp.getSalary() : java.math.BigDecimal.ZERO);

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        emp.setEmployeeId(keys.getInt(1));
                    }
                }
            }
            return emp;
        } catch (SQLException e) {
            logger.error("Error creating employee profile for user {}: {}", emp.getId(), e.getMessage());
            throw new DatabaseException("Failed to save employee profile: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(Employee emp) {
        String sql = "UPDATE employees SET employee_code = ?, department_id = ?, manager_id = ?, designation = ?, hire_date = ?, salary = ? " +
                     "WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, emp.getEmployeeCode());
            if (emp.getDepartmentId() != null) {
                ps.setInt(2, emp.getDepartmentId());
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }
            if (emp.getManagerId() != null) {
                ps.setInt(3, emp.getManagerId());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }
            ps.setString(4, emp.getDesignation());
            ps.setDate(5, emp.getHireDate() != null ? Date.valueOf(emp.getHireDate()) : Date.valueOf(java.time.LocalDate.now()));
            ps.setBigDecimal(6, emp.getSalary() != null ? emp.getSalary() : java.math.BigDecimal.ZERO);
            ps.setInt(7, emp.getEmployeeId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating employee profile {}: {}", emp.getEmployeeId(), e.getMessage());
            throw new DatabaseException("Failed to update employee profile: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean assignManager(Integer employeeId, Integer managerUserId) {
        String sql = "UPDATE employees SET manager_id = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (managerUserId != null) {
                ps.setInt(1, managerUserId);
            } else {
                ps.setNull(1, java.sql.Types.INTEGER);
            }
            ps.setInt(2, employeeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error assigning manager to employee {}: {}", employeeId, e.getMessage());
            throw new DatabaseException("Failed to assign manager: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM employees WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting employee {}: {}", id, e.getMessage());
            throw new DatabaseException("Failed to delete employee: " + e.getMessage(), e);
        }
    }

    @Override
    public int countTotal() {
        String sql = "SELECT COUNT(*) FROM employees";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error counting employees: {}", e.getMessage());
            throw new DatabaseException("Failed to count employees: " + e.getMessage(), e);
        }
        return 0;
    }

    private Employee mapRowToEmployee(ResultSet rs) throws SQLException {
        Employee emp = new Employee();
        emp.setId(rs.getInt("user_id"));
        emp.setUsername(rs.getString("username"));
        emp.setPasswordHash(rs.getString("password_hash"));
        emp.setRole(Role.EMPLOYEE);
        emp.setFullName(rs.getString("full_name"));
        emp.setEmail(rs.getString("email"));
        emp.setPhone(rs.getString("phone"));
        emp.setActive(rs.getBoolean("is_active"));

        emp.setEmployeeId(rs.getInt("emp_id"));
        emp.setEmployeeCode(rs.getString("employee_code"));
        int deptId = rs.getInt("department_id");
        if (!rs.wasNull()) {
            emp.setDepartmentId(deptId);
        }
        emp.setDepartmentName(rs.getString("department_name"));

        int mgrId = rs.getInt("manager_id");
        if (!rs.wasNull()) {
            emp.setManagerId(mgrId);
        }
        emp.setManagerName(rs.getString("manager_name"));

        emp.setDesignation(rs.getString("designation"));
        Date hireDate = rs.getDate("hire_date");
        if (hireDate != null) {
            emp.setHireDate(hireDate.toLocalDate());
        }
        emp.setSalary(rs.getBigDecimal("salary"));

        java.sql.Timestamp createdTs = rs.getTimestamp("emp_created_at");
        if (createdTs != null) {
            emp.setCreatedAt(createdTs.toLocalDateTime());
        }
        return emp;
    }
}
