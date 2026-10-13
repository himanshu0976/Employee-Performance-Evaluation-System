package com.eps.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility for hashing and verifying passwords using BCrypt.
 */
public final class PasswordUtil {

    private static final int BCRYPT_LOG_ROUNDS = 10;

    private PasswordUtil() {
        // Private constructor for utility class
    }

    /**
     * Hashes a plain text password using BCrypt with salt.
     *
     * @param plainPassword Plain text password
     * @return BCrypt hashed string
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_LOG_ROUNDS));
    }

    /**
     * Verifies a plain text password against a stored BCrypt hash.
     *
     * @param plainPassword Plain text password
     * @param hashedPassword Stored BCrypt hash
     * @return true if matches, false otherwise
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null || hashedPassword.isBlank()) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static void main(String[] args) {
        System.out.println("Admin@123: " + hashPassword("Admin@123"));
        System.out.println("Manager@123: " + hashPassword("Manager@123"));
        System.out.println("Employee@123: " + hashPassword("Employee@123"));
    }
}
