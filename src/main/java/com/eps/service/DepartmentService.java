package com.eps.service;

import com.eps.model.Department;

import java.util.List;

/**
 * Service interface for Department CRUD operations.
 */
public interface DepartmentService {

    List<Department> getAllDepartments();

    Department getDepartmentById(Integer id);

    Department createDepartment(Department department);

    void updateDepartment(Department department);

    void deleteDepartment(Integer id);
}
