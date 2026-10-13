package com.eps.dao;

import com.eps.model.Employee;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Employee operations.
 */
public interface EmployeeDao extends GenericDao<Employee, Integer> {

    Optional<Employee> findByUserId(Integer userId);

    Optional<Employee> findByEmployeeCode(String employeeCode);

    List<Employee> findByManagerUserId(Integer managerUserId);

    List<Employee> findByDepartmentId(Integer departmentId);

    List<Employee> findAllWithDetails();

    boolean assignManager(Integer employeeId, Integer managerUserId);

    int countTotal();
}
