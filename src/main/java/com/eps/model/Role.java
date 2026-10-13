package com.eps.model;

/**
 * User roles in the Employee Performance Evaluation System.
 */
public enum Role {
    ADMIN("Administrator"),
    MANAGER("Manager"),
    EMPLOYEE("Employee");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
