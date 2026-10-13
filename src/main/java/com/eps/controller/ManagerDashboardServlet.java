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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller serving the Manager Dashboard with team evaluation metrics.
 */
@WebServlet(name = "ManagerDashboardServlet", urlPatterns = {"/manager/dashboard"})
public class ManagerDashboardServlet extends HttpServlet {

    private EmployeeService employeeService;
    private EvaluationCycleService cycleService;
    private EvaluationService evaluationService;

    @Override
    public void init() {
        this.employeeService = new EmployeeServiceImpl();
        this.cycleService = new EvaluationCycleServiceImpl();
        this.evaluationService = new EvaluationServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");

        List<Employee> directReports = employeeService.getEmployeesByManager(currentUser.getId());
        Optional<EvaluationCycle> activeCycle = cycleService.getActiveCycle();

        Map<Integer, Evaluation> teamEvaluations = new HashMap<>();
        int submittedCount = 0;
        int draftCount = 0;

        if (activeCycle.isPresent()) {
            Integer cycleId = activeCycle.get().getId();
            for (Employee emp : directReports) {
                Optional<Evaluation> evalOpt = evaluationService.getEvaluationByCycleAndEmployee(cycleId, emp.getEmployeeId());
                if (evalOpt.isPresent()) {
                    Evaluation eval = evalOpt.get();
                    teamEvaluations.put(emp.getEmployeeId(), eval);
                    if (eval.getStatus() == Evaluation.Status.SUBMITTED || eval.getStatus() == Evaluation.Status.APPROVED) {
                        submittedCount++;
                    } else if (eval.getStatus() == Evaluation.Status.DRAFT) {
                        draftCount++;
                    }
                }
            }
        }

        int totalTeam = directReports.size();
        int pendingCount = Math.max(0, totalTeam - submittedCount);
        double progressPercent = totalTeam > 0 ? Math.round(((double) submittedCount / totalTeam) * 1000.0) / 10.0 : 0.0;

        request.setAttribute("directReports", directReports);
        request.setAttribute("activeCycle", activeCycle.orElse(null));
        request.setAttribute("teamEvaluations", teamEvaluations);
        request.setAttribute("submittedCount", submittedCount);
        request.setAttribute("draftCount", draftCount);
        request.setAttribute("pendingCount", pendingCount);
        request.setAttribute("progressPercent", progressPercent);

        request.getRequestDispatcher("/WEB-INF/views/manager/dashboard.jsp").forward(request, response);
    }
}
