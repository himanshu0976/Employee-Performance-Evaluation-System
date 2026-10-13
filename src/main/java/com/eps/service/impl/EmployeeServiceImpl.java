package com.eps.service.impl;

import com.eps.dao.EmployeeDao;
import com.eps.dao.UserDao;
import com.eps.dao.impl.EmployeeDaoImpl;
import com.eps.dao.impl.UserDaoImpl;
import com.eps.exception.DuplicateResourceException;
import com.eps.exception.ResourceNotFoundException;
import com.eps.exception.ValidationException;
import com.eps.model.Employee;
import com.eps.model.Role;
import com.eps.model.User;
import com.eps.service.EmployeeService;
import com.eps.util.PasswordUtil;
import com.eps.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of EmployeeService orchestrating user accounts and employee records.
 */
public class EmployeeServiceImpl implements EmployeeService {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private final EmployeeDao employeeDao;
    private final UserDao userDao;

    public EmployeeServiceImpl() {
        this.employeeDao = new EmployeeDaoImpl();
        this.userDao = new UserDaoImpl();
    }

    public EmployeeServiceImpl(EmployeeDao employeeDao, UserDao userDao) {
        this.employeeDao = employeeDao;
        this.userDao = userDao;
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeDao.findAllWithDetails();
    }

    @Override
    public Employee getEmployeeById(Integer id) {
        return employeeDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));
    }

    @Override
    public Employee getEmployeeByUserId(Integer userId) {
        return employeeDao.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee profile not found for user ID: " + userId));
    }

    @Override
    public List<Employee> getEmployeesByManager(Integer managerUserId) {
        return employeeDao.findByManagerUserId(managerUserId);
    }

    @Override
    public List<Employee> getEmployeesByDepartment(Integer departmentId) {
        return employeeDao.findByDepartmentId(departmentId);
    }

    @Override
    public Employee createEmployee(Employee employee, String plainPassword) {
        ValidationUtil.requireNonEmpty(employee.getUsername(), "Username");
        ValidationUtil.requireNonEmpty(plainPassword, "Password");
        ValidationUtil.requireNonEmpty(employee.getFullName(), "Full Name");
        ValidationUtil.requireNonEmpty(employee.getEmployeeCode(), "Employee Code");
        ValidationUtil.requireNonEmpty(employee.getDesignation(), "Designation");
        ValidationUtil.validateEmail(employee.getEmail());

        if (userDao.findByUsername(employee.getUsername()).isPresent()) {
            throw new DuplicateResourceException("Username '" + employee.getUsername() + "' is already taken.");
        }
        if (userDao.findByEmail(employee.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email '" + employee.getEmail() + "' is already in use.");
        }
        if (employeeDao.findByEmployeeCode(employee.getEmployeeCode()).isPresent()) {
            throw new DuplicateResourceException("Employee code '" + employee.getEmployeeCode() + "' is already registered.");
        }

        // 1. Create User
        employee.setPasswordHash(PasswordUtil.hashPassword(plainPassword));
        employee.setRole(Role.EMPLOYEE);
        User savedUser = userDao.save(employee);
        employee.setId(savedUser.getId());

        // 2. Create Employee profile
        employeeDao.save(employee);
        logger.info("Successfully created employee '{}' ({})", employee.getFullName(), employee.getEmployeeCode());

        return employee;
    }

    @Override
    public void updateEmployee(Employee employee) {
        if (employee.getEmployeeId() == null) {
            throw new ValidationException("Employee ID is required for update.");
        }
        ValidationUtil.requireNonEmpty(employee.getFullName(), "Full Name");
        ValidationUtil.requireNonEmpty(employee.getEmployeeCode(), "Employee Code");
        ValidationUtil.requireNonEmpty(employee.getDesignation(), "Designation");
        ValidationUtil.validateEmail(employee.getEmail());

        Employee existing = getEmployeeById(employee.getEmployeeId());

        // Validate unique email
        Optional<User> emailUser = userDao.findByEmail(employee.getEmail());
        if (emailUser.isPresent() && !emailUser.get().getId().equals(existing.getId())) {
            throw new DuplicateResourceException("Email '" + employee.getEmail() + "' is already in use.");
        }

        // Validate unique employee code
        Optional<Employee> codeEmp = employeeDao.findByEmployeeCode(employee.getEmployeeCode());
        if (codeEmp.isPresent() && !codeEmp.get().getEmployeeId().equals(existing.getEmployeeId())) {
            throw new DuplicateResourceException("Employee code '" + employee.getEmployeeCode() + "' is already in use.");
        }

        // Update User account
        existing.setFullName(employee.getFullName());
        existing.setEmail(employee.getEmail());
        existing.setPhone(employee.getPhone());
        existing.setActive(employee.isActive());
        userDao.update(existing);

        // Update Employee profile
        existing.setEmployeeCode(employee.getEmployeeCode());
        existing.setDepartmentId(employee.getDepartmentId());
        existing.setManagerId(employee.getManagerId());
        existing.setDesignation(employee.getDesignation());
        existing.setHireDate(employee.getHireDate());
        existing.setSalary(employee.getSalary());
        employeeDao.update(existing);

        logger.info("Updated employee profile {}", existing.getEmployeeCode());
    }

    @Override
    public void assignManager(Integer employeeId, Integer managerUserId) {
        Employee emp = getEmployeeById(employeeId);
        if (managerUserId != null) {
            User manager = userDao.findById(managerUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("Manager user not found with ID: " + managerUserId));
            if (manager.getRole() != Role.MANAGER) {
                throw new ValidationException("Selected user is not assigned the MANAGER role.");
            }
        }
        employeeDao.assignManager(emp.getEmployeeId(), managerUserId);
        logger.info("Assigned manager {} to employee {}", managerUserId, employeeId);
    }

    @Override
    public void deleteEmployee(Integer employeeId) {
        Employee emp = getEmployeeById(employeeId);
        // Cascade deleting user deletes employee row automatically
        userDao.deleteById(emp.getId());
        logger.info("Deleted employee profile and user account for ID {}", employeeId);
    }
}
