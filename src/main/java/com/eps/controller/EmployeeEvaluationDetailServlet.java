package com.eps.controller;

import com.eps.model.Employee;
import com.eps.model.Evaluation;
import com.eps.model.EvaluationScore;
import com.eps.model.User;
import com.eps.service.EmployeeService;
import com.eps.service.EvaluationService;
import com.eps.service.impl.EmployeeServiceImpl;
import com.eps.service.impl.EvaluationServiceImpl;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller rendering the full breakdown of an employee's evaluation including criteria charts.
 */
@WebServlet(name = "EmployeeEvaluationDetailServlet", urlPatterns = {"/employee/evaluation-view", "/employee/evaluation"})
public class EmployeeEvaluationDetailServlet extends HttpServlet {

    private EvaluationService evaluationService;
    private EmployeeService employeeService;
    private final Gson gson = new Gson();

    @Override
    public void init() {
        this.evaluationService = new EvaluationServiceImpl();
        this.employeeService = new EmployeeServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");

        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/employee/dashboard");
            return;
        }

        int evaluationId = Integer.parseInt(idStr);
        Evaluation evaluation = evaluationService.getEvaluationById(evaluationId);

        // Security check: if employee, ensure they can only view their own evaluation
        if (currentUser.isEmployee()) {
            Employee emp = (currentUser instanceof Employee) ? (Employee) currentUser :
                    employeeService.getEmployeeByUserId(currentUser.getId());
            if (!evaluation.getEmployeeId().equals(emp.getEmployeeId())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: You cannot view evaluations belonging to other employees.");
                return;
            }
        }

        // Prepare chart radar labels & data
        List<String> criteriaLabels = new ArrayList<>();
        List<Double> scoreValues = new ArrayList<>();

        for (EvaluationScore s : evaluation.getScores()) {
            criteriaLabels.add(s.getCriteriaName());
            scoreValues.add(s.getScore() != null ? s.getScore().doubleValue() : 0.0);
        }

        Map<String, Object> chartMap = new HashMap<>();
        chartMap.put("labels", criteriaLabels);
        chartMap.put("scores", scoreValues);

        request.setAttribute("evaluation", evaluation);
        request.setAttribute("chartJson", gson.toJson(chartMap));

        request.getRequestDispatcher("/WEB-INF/views/employee/evaluation-detail.jsp").forward(request, response);
    }
}
