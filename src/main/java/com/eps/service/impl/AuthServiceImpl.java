package com.eps.service.impl;

import com.eps.dao.EmployeeDao;
import com.eps.dao.UserDao;
import com.eps.dao.impl.EmployeeDaoImpl;
import com.eps.dao.impl.UserDaoImpl;
import com.eps.exception.AuthenticationException;
import com.eps.exception.ValidationException;
import com.eps.model.Employee;
import com.eps.model.Role;
import com.eps.model.User;
import com.eps.service.AuthService;
import com.eps.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Implementation of AuthService verifying BCrypt passwords and loading rich user context.
 */
public class AuthServiceImpl implements AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserDao userDao;
    private final EmployeeDao employeeDao;

    public AuthServiceImpl() {
        this.userDao = new UserDaoImpl();
        this.employeeDao = new EmployeeDaoImpl();
    }

    public AuthServiceImpl(UserDao userDao, EmployeeDao employeeDao) {
        this.userDao = userDao;
        this.employeeDao = employeeDao;
    }

    @Override
    public User authenticate(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new AuthenticationException("Username and password are required.");
        }

        String trimmedUser = username.trim();
        Optional<User> userOpt = userDao.findByUsername(trimmedUser);
        if (userOpt.isEmpty()) {
            // Check if entered email
            userOpt = userDao.findByEmail(trimmedUser);
        }

        if (userOpt.isEmpty()) {
            logger.warn("Authentication failed: User not found for '{}'", trimmedUser);
            throw new AuthenticationException("Invalid username or password.");
        }

        User user = userOpt.get();
        if (!user.isActive()) {
            logger.warn("Authentication blocked: Inactive account '{}'", trimmedUser);
            throw new AuthenticationException("Your account is deactivated. Please contact an administrator.");
        }

        if (!PasswordUtil.checkPassword(password, user.getPasswordHash())) {
            logger.warn("Authentication failed: Incorrect password for '{}'", trimmedUser);
            throw new AuthenticationException("Invalid username or password.");
        }

        // If employee, populate employee profile info
        if (user.getRole() == Role.EMPLOYEE) {
            Optional<Employee> empOpt = employeeDao.findByUserId(user.getId());
            if (empOpt.isPresent()) {
                user = empOpt.get();
            }
        }

        logger.info("User '{}' ({}) successfully logged in", user.getUsername(), user.getRole());
        return user;
    }

    @Override
    public void changePassword(Integer userId, String oldPassword, String newPassword) {
        if (newPassword == null || newPassword.length() < 6) {
            throw new ValidationException("New password must be at least 6 characters long.");
        }

        User user = userDao.findById(userId)
                .orElseThrow(() -> new AuthenticationException("User not found."));

        if (!PasswordUtil.checkPassword(oldPassword, user.getPasswordHash())) {
            throw new AuthenticationException("Incorrect current password.");
        }

        String newHash = PasswordUtil.hashPassword(newPassword);
        userDao.updatePassword(userId, newHash);
        logger.info("Password changed for user id {}", userId);
    }
}
