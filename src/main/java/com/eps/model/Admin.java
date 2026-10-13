package com.eps.model;

/**
 * Admin model extending abstract User.
 */
public class Admin extends User {
    private static final long serialVersionUID = 1L;

    public Admin() {
        super();
        this.role = Role.ADMIN;
    }

    public Admin(Integer id, String username, String passwordHash, 
                 String fullName, String email, String phone, boolean active) {
        super(id, username, passwordHash, Role.ADMIN, fullName, email, phone, active);
    }

    @Override
    public String getDashboardUrl() {
        return "/admin/dashboard";
    }

    @Override
    public String getRoleDisplayName() {
        return "System Administrator";
    }
}
