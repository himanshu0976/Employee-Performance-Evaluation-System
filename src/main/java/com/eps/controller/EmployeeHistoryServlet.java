package com.eps.controller;

import com.eps.model.Employee;
import com.eps.model.Evaluation;
import com.eps.model.User;
import com.eps.service.EmployeeService;
import com.eps.service.EvaluationService;
import com.eps.service.impl.EmployeeServiceImpl;
import com.eps.service.impl.EvaluationServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Controller providing personal evaluation history for Employees.
 */
@WebServlet(name = "EmployeeHistoryServlet", urlPatterns = {"/employee/history"})
public class EmployeeHistoryServlet extends HttpServlet {

    private EmployeeService employeeService;
    private EvaluationService evaluationService;

    @Override
    public void init() {
        this.employeeService = new EmployeeServiceImpl();
        this.evaluationService = new EvaluationServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");

        Employee employee;
        if (currentUser instanceof Employee) {
            employee = (Employee) currentUser;
        } else {
            employee = employeeService.getEmployeeByUserId(currentUser.getId());
        }

        List<Evaluation> history = evaluationService.getEvaluationsForEmployee(employee.getEmployeeId());

        request.setAttribute("employee", employee);
        request.setAttribute("history", history);

        request.getRequestDispatcher("/WEB-INF/views/employee/history.jsp").forward(request, response);
    }
}
