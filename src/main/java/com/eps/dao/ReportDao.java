package com.eps.dao;

import com.eps.model.CriteriaAverageDTO;
import com.eps.model.DepartmentReportDTO;
import com.eps.model.OrganizationStatsDTO;

import java.util.List;
import java.util.Map;

/**
 * Data Access Object for organization reports, analytics, and chart data.
 */
public interface ReportDao {

    List<DepartmentReportDTO> getDepartmentReports(Integer cycleId);

    List<CriteriaAverageDTO> getCriteriaAverages(Integer cycleId, Integer departmentId);

    OrganizationStatsDTO getOrganizationStats(Integer cycleId);

    Map<String, Integer> getRatingDistribution(Integer cycleId, Integer departmentId);
}
