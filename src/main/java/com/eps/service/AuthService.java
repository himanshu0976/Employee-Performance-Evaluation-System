package com.eps.service;

import com.eps.model.User;

/**
 * Service interface for authentication and credential operations.
 */
public interface AuthService {

    /**
     * Authenticates a user by username and password.
     *
     * @param username Username or email
     * @param password Plain text password
     * @return Authenticated User object
     * @throws com.eps.exception.AuthenticationException if invalid credentials
     */
    User authenticate(String username, String password);

    /**
     * Changes user password verifying the old password.
     */
    void changePassword(Integer userId, String oldPassword, String newPassword);
}
