package com.eps.controller;

import com.eps.exception.AppException;
import com.eps.model.Department;
import com.eps.service.DepartmentService;
import com.eps.service.impl.DepartmentServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Controller managing Department CRUD operations for Administrators.
 */
@WebServlet(name = "DepartmentServlet", urlPatterns = {"/admin/departments"})
public class DepartmentServlet extends HttpServlet {

    private DepartmentService departmentService;

    @Override
    public void init() {
        this.departmentService = new DepartmentServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Department> departments = departmentService.getAllDepartments();
        request.setAttribute("departments", departments);
        request.getRequestDispatcher("/WEB-INF/views/admin/departments.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String action = request.getParameter("action");

        try {
            if ("add".equalsIgnoreCase(action)) {
                Department dept = new Department();
                dept.setName(request.getParameter("name"));
                dept.setCode(request.getParameter("code"));
                dept.setDescription(request.getParameter("description"));
                departmentService.createDepartment(dept);
                request.getSession().setAttribute("flashSuccess", "Department '" + dept.getName() + "' created successfully.");

            } else if ("edit".equalsIgnoreCase(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                Department dept = departmentService.getDepartmentById(id);
                dept.setName(request.getParameter("name"));
                dept.setCode(request.getParameter("code"));
                dept.setDescription(request.getParameter("description"));
                departmentService.updateDepartment(dept);
                request.getSession().setAttribute("flashSuccess", "Department '" + dept.getName() + "' updated successfully.");

            } else if ("delete".equalsIgnoreCase(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                departmentService.deleteDepartment(id);
                request.getSession().setAttribute("flashSuccess", "Department deleted successfully.");
            }
        } catch (AppException e) {
            request.getSession().setAttribute("flashError", e.getMessage());
        } catch (Exception e) {
            request.getSession().setAttribute("flashError", "Failed to process department action: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/admin/departments");
    }
}
