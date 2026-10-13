package com.eps.service.impl;

import com.eps.dao.DepartmentDao;
import com.eps.dao.impl.DepartmentDaoImpl;
import com.eps.exception.DuplicateResourceException;
import com.eps.exception.ResourceNotFoundException;
import com.eps.exception.ValidationException;
import com.eps.model.Department;
import com.eps.service.DepartmentService;
import com.eps.util.ValidationUtil;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of DepartmentService with validation and business logic.
 */
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentDao departmentDao;

    public DepartmentServiceImpl() {
        this.departmentDao = new DepartmentDaoImpl();
    }

    public DepartmentServiceImpl(DepartmentDao departmentDao) {
        this.departmentDao = departmentDao;
    }

    @Override
    public List<Department> getAllDepartments() {
        return departmentDao.findAllWithEmployeeCount();
    }

    @Override
    public Department getDepartmentById(Integer id) {
        return departmentDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + id));
    }

    @Override
    public Department createDepartment(Department department) {
        ValidationUtil.requireNonEmpty(department.getName(), "Department Name");
        ValidationUtil.requireNonEmpty(department.getCode(), "Department Code");

        if (departmentDao.findByName(department.getName()).isPresent()) {
            throw new DuplicateResourceException("Department with name '" + department.getName() + "' already exists.");
        }
        if (departmentDao.findByCode(department.getCode()).isPresent()) {
            throw new DuplicateResourceException("Department with code '" + department.getCode() + "' already exists.");
        }

        return departmentDao.save(department);
    }

    @Override
    public void updateDepartment(Department department) {
        if (department.getId() == null) {
            throw new ValidationException("Department ID is required for update.");
        }
        ValidationUtil.requireNonEmpty(department.getName(), "Department Name");
        ValidationUtil.requireNonEmpty(department.getCode(), "Department Code");

        Department existing = getDepartmentById(department.getId());

        Optional<Department> withName = departmentDao.findByName(department.getName());
        if (withName.isPresent() && !withName.get().getId().equals(department.getId())) {
            throw new DuplicateResourceException("Department name '" + department.getName() + "' is already in use.");
        }

        Optional<Department> withCode = departmentDao.findByCode(department.getCode());
        if (withCode.isPresent() && !withCode.get().getId().equals(department.getId())) {
            throw new DuplicateResourceException("Department code '" + department.getCode() + "' is already in use.");
        }

        existing.setName(department.getName());
        existing.setCode(department.getCode());
        existing.setDescription(department.getDescription());

        departmentDao.update(existing);
    }

    @Override
    public void deleteDepartment(Integer id) {
        Department dept = getDepartmentById(id);
        if (dept.getEmployeeCount() > 0) {
            throw new ValidationException("Cannot delete department '" + dept.getName() + "' because it has " + dept.getEmployeeCount() + " assigned employees.");
        }
        departmentDao.deleteById(id);
    }
}
