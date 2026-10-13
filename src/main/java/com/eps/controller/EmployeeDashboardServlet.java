package com.eps.controller;

import com.eps.model.Employee;
import com.eps.model.Evaluation;
import com.eps.model.EvaluationCycle;
import com.eps.model.User;
import com.eps.service.EmployeeService;
import com.eps.service.EvaluationCycleService;
import com.eps.service.EvaluationService;
import com.eps.service.impl.EmployeeServiceImpl;
import com.eps.service.impl.EvaluationCycleServiceImpl;
import com.eps.service.impl.EvaluationServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Controller serving the Employee Dashboard with latest ratings, feedback, and performance chart.
 */
@WebServlet(name = "EmployeeDashboardServlet", urlPatterns = {"/employee/dashboard"})
public class EmployeeDashboardServlet extends HttpServlet {

    private EmployeeService employeeService;
    private EvaluationService evaluationService;
    private EvaluationCycleService cycleService;

    @Override
    public void init() {
        this.employeeService = new EmployeeServiceImpl();
        this.evaluationService = new EvaluationServiceImpl();
        this.cycleService = new EvaluationCycleServiceImpl();
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
        Optional<EvaluationCycle> activeCycle = cycleService.getActiveCycle();

        Evaluation latestEvaluation = null;
        if (!history.isEmpty()) {
            latestEvaluation = history.get(0); // First item is latest
        }

        request.setAttribute("employee", employee);
        request.setAttribute("latestEvaluation", latestEvaluation);
        request.setAttribute("history", history);
        request.setAttribute("activeCycle", activeCycle.orElse(null));

        request.getRequestDispatcher("/WEB-INF/views/employee/dashboard.jsp").forward(request, response);
    }
}
