package com.eps.service;

import com.eps.model.CriteriaAverageDTO;
import com.eps.model.DepartmentReportDTO;
import com.eps.model.OrganizationStatsDTO;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 * Service interface for organization reports, chart feeds, and CSV export.
 */
public interface ReportService {

    OrganizationStatsDTO getOrganizationStats(Integer cycleId);

    List<DepartmentReportDTO> getDepartmentReports(Integer cycleId);

    List<CriteriaAverageDTO> getCriteriaAverages(Integer cycleId, Integer departmentId);

    Map<String, Integer> getRatingDistribution(Integer cycleId, Integer departmentId);

    void exportEvaluationsCsv(PrintWriter writer, Integer cycleId, Integer departmentId, String rating);
}
