package com.eps.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordUtilTest {

    @Test
    public void testHashAndVerifyPassword() {
        String raw = "Admin@123";
        String hash = PasswordUtil.hashPassword(raw);

        assertNotNull(hash);
        assertTrue(hash.startsWith("$2a$10$") || hash.startsWith("$2a$") || hash.startsWith("$2b$"));
        assertTrue(PasswordUtil.checkPassword(raw, hash));
        assertFalse(PasswordUtil.checkPassword("WrongPassword", hash));
    }

    @Test
    public void testEmptyPasswordThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> PasswordUtil.hashPassword(""));
        assertThrows(IllegalArgumentException.class, () -> PasswordUtil.hashPassword(null));
    }
}
