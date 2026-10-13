package com.eps.util;

import com.eps.model.Evaluation;

import java.io.PrintWriter;
import java.util.List;

/**
 * Utility for exporting evaluation reports to RFC 4180 compliant CSV format.
 */
public final class CsvExportUtil {

    private CsvExportUtil() {
    }

    public static void writeEvaluationsCsv(PrintWriter writer, List<Evaluation> evaluations) {
        // CSV Header
        writer.println("Evaluation ID,Cycle,Employee Code,Employee Name,Department,Designation,Evaluator,Total Score,Rating,Status,Date Evaluated,Summary Feedback,Strengths,Areas for Improvement");

        if (evaluations == null) {
            return;
        }

        for (Evaluation eval : evaluations) {
            StringBuilder sb = new StringBuilder();
            appendCsvCell(sb, String.valueOf(eval.getId()));
            appendCsvCell(sb, eval.getCycleTitle());
            appendCsvCell(sb, eval.getEmployeeCode());
            appendCsvCell(sb, eval.getEmployeeName());
            appendCsvCell(sb, eval.getDepartmentName());
            appendCsvCell(sb, eval.getDesignation());
            appendCsvCell(sb, eval.getEvaluatorName());
            appendCsvCell(sb, eval.getTotalScore() != null ? eval.getTotalScore().toString() : "N/A");
            appendCsvCell(sb, eval.getRating() != null ? eval.getRating() : "Pending");
            appendCsvCell(sb, eval.getStatus() != null ? eval.getStatus().name() : "");
            appendCsvCell(sb, eval.getEvaluatedAt() != null ? eval.getEvaluatedAt().toString() : "Pending");
            appendCsvCell(sb, eval.getSummaryFeedback());
            appendCsvCell(sb, eval.getStrengths());
            appendCsvCell(sb, eval.getAreasForImprovement(), true); // last cell, no trailing comma
            writer.println(sb.toString());
        }
    }

    private static void appendCsvCell(StringBuilder sb, String text) {
        appendCsvCell(sb, text, false);
    }

    private static void appendCsvCell(StringBuilder sb, String text, boolean isLast) {
        if (text == null) {
            sb.append("\"\"");
        } else {
            String escaped = text.replace("\"", "\"\"");
            sb.append("\"").append(escaped).append("\"");
        }
        if (!isLast) {
            sb.append(",");
        }
    }
}
