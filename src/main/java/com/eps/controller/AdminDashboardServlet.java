package com.eps.controller;

import com.eps.model.DepartmentReportDTO;
import com.eps.model.Evaluation;
import com.eps.model.EvaluationCycle;
import com.eps.model.OrganizationStatsDTO;
import com.eps.service.EvaluationCycleService;
import com.eps.service.EvaluationService;
import com.eps.service.ReportService;
import com.eps.service.impl.EvaluationCycleServiceImpl;
import com.eps.service.impl.EvaluationServiceImpl;
import com.eps.service.impl.ReportServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Controller serving Admin dashboard summary statistics and operational status.
 */
@WebServlet(name = "AdminDashboardServlet", urlPatterns = {"/admin/dashboard"})
public class AdminDashboardServlet extends HttpServlet {

    private ReportService reportService;
    private EvaluationCycleService cycleService;
    private EvaluationService evaluationService;

    @Override
    public void init() {
        this.reportService = new ReportServiceImpl();
        this.cycleService = new EvaluationCycleServiceImpl();
        this.evaluationService = new EvaluationServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Optional<EvaluationCycle> activeCycle = cycleService.getActiveCycle();
        Integer activeCycleId = activeCycle.map(EvaluationCycle::getId).orElse(null);

        OrganizationStatsDTO stats = reportService.getOrganizationStats(activeCycleId);
        List<DepartmentReportDTO> deptReports = reportService.getDepartmentReports(activeCycleId);
        List<EvaluationCycle> allCycles = cycleService.getAllCycles();
        List<Evaluation> recentEvaluations = evaluationService.getFilteredEvaluations(activeCycleId, null, null);

        request.setAttribute("stats", stats);
        request.setAttribute("activeCycle", activeCycle.orElse(null));
        request.setAttribute("deptReports", deptReports);
        request.setAttribute("allCycles", allCycles);
        request.setAttribute("recentEvaluations", recentEvaluations);

        request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(request, response);
    }
}
