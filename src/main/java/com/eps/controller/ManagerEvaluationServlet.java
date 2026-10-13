package com.eps.controller;

import com.eps.exception.AppException;
import com.eps.exception.ValidationException;
import com.eps.model.Employee;
import com.eps.model.Evaluation;
import com.eps.model.EvaluationCriteria;
import com.eps.model.EvaluationCycle;
import com.eps.model.EvaluationScore;
import com.eps.model.User;
import com.eps.service.EmployeeService;
import com.eps.service.EvaluationCriteriaService;
import com.eps.service.EvaluationCycleService;
import com.eps.service.EvaluationService;
import com.eps.service.impl.EmployeeServiceImpl;
import com.eps.service.impl.EvaluationCriteriaServiceImpl;
import com.eps.service.impl.EvaluationCycleServiceImpl;
import com.eps.service.impl.EvaluationServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for Managers to evaluate direct reports during active review cycles.
 */
@WebServlet(name = "ManagerEvaluationServlet", urlPatterns = {"/manager/evaluations"})
public class ManagerEvaluationServlet extends HttpServlet {

    private EmployeeService employeeService;
    private EvaluationService evaluationService;
    private EvaluationCycleService cycleService;
    private EvaluationCriteriaService criteriaService;

    @Override
    public void init() {
        this.employeeService = new EmployeeServiceImpl();
        this.evaluationService = new EvaluationServiceImpl();
        this.cycleService = new EvaluationCycleServiceImpl();
        this.criteriaService = new EvaluationCriteriaServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");

        String action = request.getParameter("action");
        if ("evaluate".equalsIgnoreCase(action)) {
            showEvaluationForm(request, response, currentUser);
        } else {
            showTeamList(request, response, currentUser);
        }
    }

    private void showTeamList(HttpServletRequest request, HttpServletResponse response, User currentUser)
            throws ServletException, IOException {
        List<Employee> directReports = employeeService.getEmployeesByManager(currentUser.getId());
        Optional<EvaluationCycle> activeCycle = cycleService.getActiveCycle();

        Map<Integer, Evaluation> evaluationsMap = new HashMap<>();
        if (activeCycle.isPresent()) {
            Integer cycleId = activeCycle.get().getId();
            for (Employee emp : directReports) {
                evaluationService.getEvaluationByCycleAndEmployee(cycleId, emp.getEmployeeId())
                        .ifPresent(eval -> evaluationsMap.put(emp.getEmployeeId(), eval));
            }
        }

        request.setAttribute("directReports", directReports);
        request.setAttribute("activeCycle", activeCycle.orElse(null));
        request.setAttribute("evaluationsMap", evaluationsMap);

        request.getRequestDispatcher("/WEB-INF/views/manager/evaluations.jsp").forward(request, response);
    }

    private void showEvaluationForm(HttpServletRequest request, HttpServletResponse response, User currentUser)
            throws ServletException, IOException {
        String empIdStr = request.getParameter("employeeId");
        if (empIdStr == null || empIdStr.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/manager/evaluations");
            return;
        }

        int employeeId = Integer.parseInt(empIdStr);
        Employee employee = employeeService.getEmployeeById(employeeId);

        // Security check: ensure this employee is assigned to this manager (unless manager is admin)
        if (!currentUser.isAdmin() && (employee.getManagerId() == null || !employee.getManagerId().equals(currentUser.getId()))) {
            request.getSession().setAttribute("flashError", "Access denied: This employee is not assigned to your team.");
            response.sendRedirect(request.getContextPath() + "/manager/evaluations");
            return;
        }

        EvaluationCycle activeCycle = cycleService.getActiveCycle()
                .orElseThrow(() -> new ValidationException("There is no active evaluation cycle at this time."));

        List<EvaluationCriteria> criteriaList = criteriaService.getActiveCriteria();
        Optional<Evaluation> existingEval = evaluationService.getEvaluationByCycleAndEmployee(activeCycle.getId(), employeeId);

        Map<Integer, EvaluationScore> scoreMap = new HashMap<>();
        if (existingEval.isPresent()) {
            for (EvaluationScore s : existingEval.get().getScores()) {
                scoreMap.put(s.getCriteriaId(), s);
            }
        }

        request.setAttribute("employee", employee);
        request.setAttribute("activeCycle", activeCycle);
        request.setAttribute("criteriaList", criteriaList);
        request.setAttribute("evaluation", existingEval.orElse(null));
        request.setAttribute("scoreMap", scoreMap);

        request.getRequestDispatcher("/WEB-INF/views/manager/evaluate-form.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");

        String submitAction = request.getParameter("action"); // "save-draft" or "submit"
        boolean isSubmitFinal = "submit".equalsIgnoreCase(submitAction);

        try {
            int cycleId = Integer.parseInt(request.getParameter("cycleId"));
            int employeeId = Integer.parseInt(request.getParameter("employeeId"));
            String summaryFeedback = request.getParameter("summaryFeedback");
            String strengths = request.getParameter("strengths");
            String areasForImprovement = request.getParameter("areasForImprovement");

            // Extract criteria scores
            List<EvaluationCriteria> criteriaList = criteriaService.getActiveCriteria();
            List<EvaluationScore> scores = new ArrayList<>();

            for (EvaluationCriteria c : criteriaList) {
                String scoreParam = request.getParameter("score_" + c.getId());
                String feedbackParam = request.getParameter("feedback_" + c.getId());

                BigDecimal scoreVal = null;
                if (scoreParam != null && !scoreParam.isBlank()) {
                    scoreVal = new BigDecimal(scoreParam);
                }

                EvaluationScore score = new EvaluationScore();
                score.setCriteriaId(c.getId());
                score.setCriteriaName(c.getName());
                score.setWeightage(c.getWeightage());
                score.setScore(scoreVal);
                score.setFeedback(feedbackParam);
                scores.add(score);
            }

            Evaluation saved = evaluationService.saveOrSubmitEvaluation(
                    cycleId, employeeId, currentUser.getId(), scores,
                    summaryFeedback, strengths, areasForImprovement, isSubmitFinal);

            if (isSubmitFinal) {
                session.setAttribute("flashSuccess", "Evaluation submitted successfully! Total Score: " +
                        saved.getTotalScore() + " (" + saved.getRating() + ")");
            } else {
                session.setAttribute("flashSuccess", "Evaluation draft saved successfully.");
            }

            response.sendRedirect(request.getContextPath() + "/manager/evaluations");

        } catch (AppException e) {
            session.setAttribute("flashError", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/manager/evaluations?action=evaluate&employeeId=" + request.getParameter("employeeId"));
        } catch (Exception e) {
            session.setAttribute("flashError", "Failed to save evaluation: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/manager/evaluations");
        }
    }
}
