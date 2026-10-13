package com.eps.controller;

import com.eps.dao.UserDao;
import com.eps.dao.impl.UserDaoImpl;
import com.eps.exception.AppException;
import com.eps.model.Department;
import com.eps.model.Employee;
import com.eps.model.Role;
import com.eps.model.User;
import com.eps.service.DepartmentService;
import com.eps.service.EmployeeService;
import com.eps.service.impl.DepartmentServiceImpl;
import com.eps.service.impl.EmployeeServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller managing Employee CRUD operations and Manager assignments for Administrators.
 */
@WebServlet(name = "EmployeeServlet", urlPatterns = {"/admin/employees"})
public class EmployeeServlet extends HttpServlet {

    private EmployeeService employeeService;
    private DepartmentService departmentService;
    private UserDao userDao;

    @Override
    public void init() {
        this.employeeService = new EmployeeServiceImpl();
        this.departmentService = new DepartmentServiceImpl();
        this.userDao = new UserDaoImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Employee> employees = employeeService.getAllEmployees();
        List<Department> departments = departmentService.getAllDepartments();
        List<User> managers = userDao.findByRole(Role.MANAGER);

        request.setAttribute("employees", employees);
        request.setAttribute("departments", departments);
        request.setAttribute("managers", managers);

        request.getRequestDispatcher("/WEB-INF/views/admin/employees.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String action = request.getParameter("action");

        try {
            if ("add".equalsIgnoreCase(action)) {
                Employee emp = new Employee();
                emp.setUsername(request.getParameter("username"));
                emp.setFullName(request.getParameter("fullName"));
                emp.setEmail(request.getParameter("email"));
                emp.setPhone(request.getParameter("phone"));
                emp.setEmployeeCode(request.getParameter("employeeCode"));
                emp.setDesignation(request.getParameter("designation"));

                String deptIdStr = request.getParameter("departmentId");
                if (deptIdStr != null && !deptIdStr.isBlank()) {
                    emp.setDepartmentId(Integer.parseInt(deptIdStr));
                }

                String mgrIdStr = request.getParameter("managerId");
                if (mgrIdStr != null && !mgrIdStr.isBlank()) {
                    emp.setManagerId(Integer.parseInt(mgrIdStr));
                }

                String hireDateStr = request.getParameter("hireDate");
                if (hireDateStr != null && !hireDateStr.isBlank()) {
                    emp.setHireDate(LocalDate.parse(hireDateStr));
                } else {
                    emp.setHireDate(LocalDate.now());
                }

                String salaryStr = request.getParameter("salary");
                if (salaryStr != null && !salaryStr.isBlank()) {
                    emp.setSalary(new BigDecimal(salaryStr));
                }

                emp.setActive("true".equalsIgnoreCase(request.getParameter("active")));

                String password = request.getParameter("password");
                employeeService.createEmployee(emp, password);
                request.getSession().setAttribute("flashSuccess", "Employee '" + emp.getFullName() + "' created successfully.");

            } else if ("edit".equalsIgnoreCase(action)) {
                int empId = Integer.parseInt(request.getParameter("employeeId"));
                Employee emp = employeeService.getEmployeeById(empId);

                emp.setFullName(request.getParameter("fullName"));
                emp.setEmail(request.getParameter("email"));
                emp.setPhone(request.getParameter("phone"));
                emp.setEmployeeCode(request.getParameter("employeeCode"));
                emp.setDesignation(request.getParameter("designation"));

                String deptIdStr = request.getParameter("departmentId");
                if (deptIdStr != null && !deptIdStr.isBlank()) {
                    emp.setDepartmentId(Integer.parseInt(deptIdStr));
                } else {
                    emp.setDepartmentId(null);
                }

                String mgrIdStr = request.getParameter("managerId");
                if (mgrIdStr != null && !mgrIdStr.isBlank()) {
                    emp.setManagerId(Integer.parseInt(mgrIdStr));
                } else {
                    emp.setManagerId(null);
                }

                String hireDateStr = request.getParameter("hireDate");
                if (hireDateStr != null && !hireDateStr.isBlank()) {
                    emp.setHireDate(LocalDate.parse(hireDateStr));
                }

                String salaryStr = request.getParameter("salary");
                if (salaryStr != null && !salaryStr.isBlank()) {
                    emp.setSalary(new BigDecimal(salaryStr));
                }

                emp.setActive("true".equalsIgnoreCase(request.getParameter("active")));

                employeeService.updateEmployee(emp);
                request.getSession().setAttribute("flashSuccess", "Employee '" + emp.getFullName() + "' updated successfully.");

            } else if ("assign-manager".equalsIgnoreCase(action)) {
                int empId = Integer.parseInt(request.getParameter("employeeId"));
                String mgrIdStr = request.getParameter("managerId");
                Integer mgrId = (mgrIdStr != null && !mgrIdStr.isBlank()) ? Integer.parseInt(mgrIdStr) : null;

                employeeService.assignManager(empId, mgrId);
                request.getSession().setAttribute("flashSuccess", "Manager assigned successfully.");

            } else if ("delete".equalsIgnoreCase(action)) {
                int empId = Integer.parseInt(request.getParameter("employeeId"));
                employeeService.deleteEmployee(empId);
                request.getSession().setAttribute("flashSuccess", "Employee deleted successfully.");
            }
        } catch (AppException e) {
            request.getSession().setAttribute("flashError", e.getMessage());
        } catch (Exception e) {
            request.getSession().setAttribute("flashError", "Failed to process employee action: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/admin/employees");
    }
}
