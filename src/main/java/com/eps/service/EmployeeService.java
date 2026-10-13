package com.eps.service;

import com.eps.model.Employee;

import java.util.List;

/**
 * Service interface for Employee management and manager assignments.
 */
public interface EmployeeService {

    List<Employee> getAllEmployees();

    Employee getEmployeeById(Integer id);

    Employee getEmployeeByUserId(Integer userId);

    List<Employee> getEmployeesByManager(Integer managerUserId);

    List<Employee> getEmployeesByDepartment(Integer departmentId);

    Employee createEmployee(Employee employee, String plainPassword);

    void updateEmployee(Employee employee);

    void assignManager(Integer employeeId, Integer managerUserId);

    void deleteEmployee(Integer employeeId);
}
