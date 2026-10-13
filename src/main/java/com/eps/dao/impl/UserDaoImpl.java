package com.eps.dao.impl;

import com.eps.dao.UserDao;
import com.eps.exception.DatabaseException;
import com.eps.model.Admin;
import com.eps.model.Employee;
import com.eps.model.Manager;
import com.eps.model.Role;
import com.eps.model.User;
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
 * Implementation of UserDao using JDBC PreparedStatements and polymorphic user instantiation.
 */
public class UserDaoImpl implements UserDao {
    private static final Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);

    private static final String SELECT_BASE = 
            "SELECT u.id, u.username, u.password_hash, u.role, u.full_name, u.email, u.phone, u.is_active, u.created_at, u.updated_at " +
            "FROM users u ";

    @Override
    public Optional<User> findById(Integer id) {
        String sql = SELECT_BASE + "WHERE u.id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToUser(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding user by id {}: {}", id, e.getMessage());
            throw new DatabaseException("Failed to find user by id: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = SELECT_BASE + "WHERE u.username = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToUser(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding user by username {}: {}", username, e.getMessage());
            throw new DatabaseException("Failed to find user by username: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = SELECT_BASE + "WHERE LOWER(u.email) = LOWER(?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToUser(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding user by email {}: {}", email, e.getMessage());
            throw new DatabaseException("Failed to find user by email: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY u.full_name ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRowToUser(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding all users: {}", e.getMessage());
            throw new DatabaseException("Failed to list users: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<User> findByRole(Role role) {
        List<User> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE u.role = ? AND u.is_active = 1 ORDER BY u.full_name ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToUser(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding users by role {}: {}", role, e.getMessage());
            throw new DatabaseException("Failed to find users by role: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public User save(User user) {
        String sql = "INSERT INTO users (username, password_hash, role, full_name, email, phone, is_active) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getRole().name());
            ps.setString(4, user.getFullName());
            ps.setString(5, user.getEmail());
            ps.setString(6, user.getPhone());
            ps.setBoolean(7, user.isActive());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        user.setId(keys.getInt(1));
                    }
                }
            }
            return user;
        } catch (SQLException e) {
            logger.error("Error creating user {}: {}", user.getUsername(), e.getMessage());
            throw new DatabaseException("Failed to save user: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(User user) {
        String sql = "UPDATE users SET full_name = ?, email = ?, phone = ?, is_active = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setBoolean(4, user.isActive());
            ps.setInt(5, user.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating user {}: {}", user.getId(), e.getMessage());
            throw new DatabaseException("Failed to update user: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updatePassword(Integer userId, String newPasswordHash) {
        String sql = "UPDATE users SET password_hash = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPasswordHash);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating password for user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to update password: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean toggleActiveStatus(Integer userId, boolean active) {
        String sql = "UPDATE users SET is_active = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, active);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error toggling active status for user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to toggle user status: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting user {}: {}", id, e.getMessage());
            throw new DatabaseException("Failed to delete user: " + e.getMessage(), e);
        }
    }

    @Override
    public int countByRole(Role role) {
        String sql = "SELECT COUNT(*) FROM users WHERE role = ? AND is_active = 1";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role.name());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.error("Error counting users by role: {}", e.getMessage());
            throw new DatabaseException("Failed to count users: " + e.getMessage(), e);
        }
        return 0;
    }

    /**
     * Polymorphic User mapper creating Admin, Manager, or Employee instance based on role.
     */
    private User mapRowToUser(ResultSet rs) throws SQLException {
        String roleStr = rs.getString("role");
        Role role = Role.valueOf(roleStr);

        User user;
        switch (role) {
            case ADMIN -> user = new Admin();
            case MANAGER -> user = new Manager();
            case EMPLOYEE -> user = new Employee();
            default -> throw new IllegalArgumentException("Unknown role: " + roleStr);
        }

        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(role);
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setActive(rs.getBoolean("is_active"));

        java.sql.Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) {
            user.setCreatedAt(createdTs.toLocalDateTime());
        }
        java.sql.Timestamp updatedTs = rs.getTimestamp("updated_at");
        if (updatedTs != null) {
            user.setUpdatedAt(updatedTs.toLocalDateTime());
        }

        return user;
    }
}
