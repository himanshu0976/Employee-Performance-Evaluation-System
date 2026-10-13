package com.eps.service.impl;

import com.eps.dao.EvaluationDao;
import com.eps.dao.ReportDao;
import com.eps.dao.impl.EvaluationDaoImpl;
import com.eps.dao.impl.ReportDaoImpl;
import com.eps.model.CriteriaAverageDTO;
import com.eps.model.DepartmentReportDTO;
import com.eps.model.Evaluation;
import com.eps.model.OrganizationStatsDTO;
import com.eps.service.ReportService;
import com.eps.util.CsvExportUtil;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 * Implementation of ReportService for organization analytics and reporting.
 */
public class ReportServiceImpl implements ReportService {

    private final ReportDao reportDao;
    private final EvaluationDao evaluationDao;

    public ReportServiceImpl() {
        this.reportDao = new ReportDaoImpl();
        this.evaluationDao = new EvaluationDaoImpl();
    }

    public ReportServiceImpl(ReportDao reportDao, EvaluationDao evaluationDao) {
        this.reportDao = reportDao;
        this.evaluationDao = evaluationDao;
    }

    @Override
    public OrganizationStatsDTO getOrganizationStats(Integer cycleId) {
        return reportDao.getOrganizationStats(cycleId);
    }

    @Override
    public List<DepartmentReportDTO> getDepartmentReports(Integer cycleId) {
        return reportDao.getDepartmentReports(cycleId);
    }

    @Override
    public List<CriteriaAverageDTO> getCriteriaAverages(Integer cycleId, Integer departmentId) {
        return reportDao.getCriteriaAverages(cycleId, departmentId);
    }

    @Override
    public Map<String, Integer> getRatingDistribution(Integer cycleId, Integer departmentId) {
        return reportDao.getRatingDistribution(cycleId, departmentId);
    }

    @Override
    public void exportEvaluationsCsv(PrintWriter writer, Integer cycleId, Integer departmentId, String rating) {
        List<Evaluation> evals = evaluationDao.findFiltered(cycleId, departmentId, rating);
        CsvExportUtil.writeEvaluationsCsv(writer, evals);
    }
}
