package com.eps.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Abstract User class demonstrating OOP inheritance and encapsulation.
 * Serves as the base entity for Admin, Manager, and Employee models.
 */
public abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;

    protected Integer id;
    protected String username;
    protected String passwordHash;
    protected Role role;
    protected String fullName;
    protected String email;
    protected String phone;
    protected boolean active = true;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;

    public User() {
    }

    public User(Integer id, String username, String passwordHash, Role role, 
                String fullName, String email, String phone, boolean active) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.active = active;
    }

    // Abstract methods to demonstrate polymorphism
    public abstract String getDashboardUrl();
    public abstract String getRoleDisplayName();

    // Role helper methods
    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }

    public boolean isManager() {
        return this.role == Role.MANAGER;
    }

    public boolean isEmployee() {
        return this.role == Role.EMPLOYEE;
    }

    // Encapsulation: Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username != null ? username.trim() : null;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName != null ? fullName.trim() : null;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.trim().toLowerCase() : null;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone != null ? phone.trim() : null;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role=" + role +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", active=" + active +
                '}';
    }
}
