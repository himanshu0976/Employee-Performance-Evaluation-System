package com.eps.model;

/**
 * Manager model extending abstract User.
 */
public class Manager extends User {
    private static final long serialVersionUID = 1L;

    private Integer departmentId;
    private String departmentName;
    private int directReportsCount;

    public Manager() {
        super();
        this.role = Role.MANAGER;
    }

    public Manager(Integer id, String username, String passwordHash, 
                   String fullName, String email, String phone, boolean active) {
        super(id, username, passwordHash, Role.MANAGER, fullName, email, phone, active);
    }

    @Override
    public String getDashboardUrl() {
        return "/manager/dashboard";
    }

    @Override
    public String getRoleDisplayName() {
        return "Manager / Evaluator";
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public int getDirectReportsCount() {
        return directReportsCount;
    }

    public void setDirectReportsCount(int directReportsCount) {
        this.directReportsCount = directReportsCount;
    }
}
