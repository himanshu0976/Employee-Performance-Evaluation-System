package com.eps.controller;

import com.eps.model.CriteriaAverageDTO;
import com.eps.model.Department;
import com.eps.model.DepartmentReportDTO;
import com.eps.model.Evaluation;
import com.eps.model.EvaluationCycle;
import com.eps.model.OrganizationStatsDTO;
import com.eps.service.DepartmentService;
import com.eps.service.EvaluationCycleService;
import com.eps.service.EvaluationService;
import com.eps.service.ReportService;
import com.eps.service.impl.DepartmentServiceImpl;
import com.eps.service.impl.EvaluationCycleServiceImpl;
import com.eps.service.impl.EvaluationServiceImpl;
import com.eps.service.impl.ReportServiceImpl;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller serving Organization Reports with multi-parameter filtering,
 * Chart.js dynamic data feeds, and RFC-4180 CSV export.
 */
@WebServlet(name = "AdminReportServlet", urlPatterns = {"/admin/reports", "/admin/reports/export", "/admin/reports/chart-data"})
public class AdminReportServlet extends HttpServlet {

    private ReportService reportService;
    private EvaluationService evaluationService;
    private EvaluationCycleService cycleService;
    private DepartmentService departmentService;
    private final Gson gson = new Gson();

    @Override
    public void init() {
        this.reportService = new ReportServiceImpl();
        this.evaluationService = new EvaluationServiceImpl();
        this.cycleService = new EvaluationCycleServiceImpl();
        this.departmentService = new DepartmentServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/admin/reports/export".equals(path) || "export".equalsIgnoreCase(request.getParameter("action"))) {
            handleCsvExport(request, response);
            return;
        }

        if ("/admin/reports/chart-data".equals(path)) {
            handleChartDataJson(request, response);
            return;
        }

        handleReportsPage(request, response);
    }

    private void handleReportsPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Parse filters
        String cycleIdStr = request.getParameter("cycleId");
        String deptIdStr = request.getParameter("departmentId");
        String rating = request.getParameter("rating");

        Integer cycleId = (cycleIdStr != null && !cycleIdStr.isBlank()) ? Integer.parseInt(cycleIdStr) : null;
        Integer deptId = (deptIdStr != null && !deptIdStr.isBlank()) ? Integer.parseInt(deptIdStr) : null;

        // If cycle not explicitly selected, default to active cycle (or latest cycle)
        if (cycleId == null) {
            cycleId = cycleService.getActiveCycle().map(EvaluationCycle::getId).orElse(null);
            if (cycleId == null) {
                List<EvaluationCycle> all = cycleService.getAllCycles();
                if (!all.isEmpty()) {
                    cycleId = all.get(0).getId();
                }
            }
        }

        List<EvaluationCycle> allCycles = cycleService.getAllCycles();
        List<Department> allDepts = departmentService.getAllDepartments();

        OrganizationStatsDTO stats = reportService.getOrganizationStats(cycleId);
        List<DepartmentReportDTO> deptReports = reportService.getDepartmentReports(cycleId);
        List<CriteriaAverageDTO> criteriaAverages = reportService.getCriteriaAverages(cycleId, deptId);
        List<Evaluation> filteredEvaluations = evaluationService.getFilteredEvaluations(cycleId, deptId, rating);

        request.setAttribute("selectedCycleId", cycleId);
        request.setAttribute("selectedDeptId", deptId);
        request.setAttribute("selectedRating", rating);
        request.setAttribute("allCycles", allCycles);
        request.setAttribute("allDepts", allDepts);
        request.setAttribute("stats", stats);
        request.setAttribute("deptReports", deptReports);
        request.setAttribute("criteriaAverages", criteriaAverages);
        request.setAttribute("filteredEvaluations", filteredEvaluations);

        request.getRequestDispatcher("/WEB-INF/views/admin/reports.jsp").forward(request, response);
    }

    private void handleChartDataJson(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String cycleIdStr = request.getParameter("cycleId");
        String deptIdStr = request.getParameter("departmentId");

        Integer cycleId = (cycleIdStr != null && !cycleIdStr.isBlank()) ? Integer.parseInt(cycleIdStr) : null;
        Integer deptId = (deptIdStr != null && !deptIdStr.isBlank()) ? Integer.parseInt(deptIdStr) : null;

        List<DepartmentReportDTO> deptReports = reportService.getDepartmentReports(cycleId);
        List<CriteriaAverageDTO> criteriaAverages = reportService.getCriteriaAverages(cycleId, deptId);
        Map<String, Integer> ratingDistribution = reportService.getRatingDistribution(cycleId, deptId);

        Map<String, Object> chartPayload = new HashMap<>();
        chartPayload.put("departmentReports", deptReports);
        chartPayload.put("criteriaAverages", criteriaAverages);
        chartPayload.put("ratingDistribution", ratingDistribution);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(chartPayload));
        out.flush();
    }

    private void handleCsvExport(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String cycleIdStr = request.getParameter("cycleId");
        String deptIdStr = request.getParameter("departmentId");
        String rating = request.getParameter("rating");

        Integer cycleId = (cycleIdStr != null && !cycleIdStr.isBlank()) ? Integer.parseInt(cycleIdStr) : null;
        Integer deptId = (deptIdStr != null && !deptIdStr.isBlank()) ? Integer.parseInt(deptIdStr) : null;

        response.setContentType("text/csv");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"EPS-Evaluation-Report.csv\"");

        PrintWriter writer = response.getWriter();
        reportService.exportEvaluationsCsv(writer, cycleId, deptId, rating);
        writer.flush();
    }
}
