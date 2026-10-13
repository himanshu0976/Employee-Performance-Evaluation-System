package com.eps.dao;

import com.eps.model.Department;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Department operations.
 */
public interface DepartmentDao extends GenericDao<Department, Integer> {

    Optional<Department> findByName(String name);

    Optional<Department> findByCode(String code);

    List<Department> findAllWithEmployeeCount();

    int countTotal();
}
