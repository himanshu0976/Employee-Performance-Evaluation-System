package com.eps.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Employee model extending abstract User.
 * Encapsulates both user account properties and employment details.
 */
public class Employee extends User {
    private static final long serialVersionUID = 1L;

    private Integer employeeId; // Primary key from employees table
    private String employeeCode;
    private Integer departmentId;
    private String departmentName;
    private Integer managerId; // user_id of the assigned manager
    private String managerName;
    private String designation;
    private LocalDate hireDate;
    private BigDecimal salary;

    public Employee() {
        super();
        this.role = Role.EMPLOYEE;
    }

    public Employee(Integer id, String username, String passwordHash, 
                    String fullName, String email, String phone, boolean active) {
        super(id, username, passwordHash, Role.EMPLOYEE, fullName, email, phone, active);
    }

    @Override
    public String getDashboardUrl() {
        return "/employee/dashboard";
    }

    @Override
    public String getRoleDisplayName() {
        return "Employee";
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode != null ? employeeCode.trim() : null;
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

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation != null ? designation.trim() : null;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }
}
