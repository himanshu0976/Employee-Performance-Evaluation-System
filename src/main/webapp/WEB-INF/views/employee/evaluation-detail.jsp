<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Evaluation Report"/>
</jsp:include>

<div class="d-flex justify-content-between align-items-center mb-4">
    <div>
        <a href="javascript:history.back()" class="btn btn-outline-secondary btn-sm mb-2">
            <i class="bi bi-arrow-left me-1"></i> Back
        </a>
        <h2 class="fw-bold mb-1">${evaluation.cycleTitle} - Performance Evaluation Report</h2>
        <p class="text-muted mb-0">Employee: <strong>${evaluation.employeeName}</strong> (${evaluation.employeeCode}) &bull; Evaluator: <strong>${evaluation.evaluatorName}</strong></p>
    </div>
    <button type="button" class="btn btn-outline-dark btn-sm d-print-none" onclick="window.print()">
        <i class="bi bi-printer me-1"></i> Print / Save PDF
    </button>
</div>

<!-- Score Banner -->
<div class="card border-0 shadow-sm rounded-3 mb-4 bg-primary text-white p-4">
    <div class="row align-items-center text-center text-md-start">
        <div class="col-md-3 border-end-md">
            <div class="text-white-50 small text-uppercase fw-bold">Overall Rating</div>
            <div class="display-5 fw-bold mb-0">${evaluation.totalScore}</div>
            <span class="badge ${evaluation.ratingBadgeClass} fs-6 px-3 py-1 mt-1">${evaluation.rating}</span>
        </div>
        <div class="col-md-9 ps-md-4 mt-3 mt-md-0">
            <h5 class="fw-bold mb-1">Performance Assessment Summary</h5>
            <p class="mb-0 text-white-50 small">
                Status: <strong>${evaluation.status}</strong> &bull; 
                Department: <strong>${evaluation.departmentName}</strong> &bull; 
                Designation: <strong>${evaluation.designation}</strong> &bull; 
                Completed: <strong>${evaluation.evaluatedAt != null ? evaluation.evaluatedAt.toLocalDate() : 'Pending'}</strong>
            </p>
        </div>
    </div>
</div>

<!-- Criteria Scores Breakdown & Radar Visual -->
<div class="row g-4 mb-4">
    <div class="col-lg-7">
        <div class="card border-0 shadow-sm rounded-3 h-100">
            <div class="card-header bg-white border-0 py-3">
                <h6 class="fw-bold mb-0 text-dark">
                    <i class="bi bi-card-checklist text-primary me-2"></i>Criteria Evaluation Breakdown
                </h6>
            </div>
            <div class="table-responsive">
                <table class="table table-hover align-middle table-eps mb-0">
                    <thead>
                        <tr>
                            <th>Criterion</th>
                            <th>Weight</th>
                            <th>Score</th>
                            <th>Feedback / Observations</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${evaluation.scores}" var="s">
                            <tr>
                                <td class="fw-semibold text-dark">${s.criteriaName}</td>
                                <td><span class="badge bg-light text-muted border">${s.weightage}%</span></td>
                                <td>
                                    <span class="fw-bold text-primary">${s.score}</span>
                                    <span class="text-muted small">/ 5.0</span>
                                </td>
                                <td class="small text-muted" style="max-width: 260px;">
                                    ${not empty s.feedback ? s.feedback : '&mdash;'}
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <div class="col-lg-5">
        <div class="card border-0 shadow-sm rounded-3 h-100">
            <div class="card-header bg-white border-0 py-3">
                <h6 class="fw-bold mb-0 text-dark">
                    <i class="bi bi-radar text-info me-2"></i>Performance Competency Radar
                </h6>
            </div>
            <div class="card-body">
                <div class="chart-container" style="height: 280px;">
                    <canvas id="detailRadarChart"></canvas>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Manager Qualitative Feedback -->
<div class="card border-0 shadow-sm rounded-3 mb-4">
    <div class="card-header bg-white border-0 py-3">
        <h6 class="fw-bold mb-0 text-dark">
            <i class="bi bi-chat-text-fill text-success me-2"></i>Qualitative Feedback & Development Goals
        </h6>
    </div>
    <div class="card-body p-4">
        <div class="mb-4">
            <h6 class="fw-bold text-secondary small text-uppercase">Summary Commentary</h6>
            <div class="bg-light p-3 rounded-2 border text-dark">
                ${evaluation.summaryFeedback != null ? evaluation.summaryFeedback : 'No overall summary provided.'}
            </div>
        </div>

        <div class="row g-3">
            <div class="col-md-6">
                <div class="p-3 rounded-2 border border-success-subtle bg-success bg-opacity-10 h-100">
                    <h6 class="fw-bold text-success mb-2"><i class="bi bi-star-fill me-1"></i>Key Strengths</h6>
                    <div class="small text-dark">
                        ${evaluation.strengths != null ? evaluation.strengths : 'No strengths specified.'}
                    </div>
                </div>
            </div>
            <div class="col-md-6">
                <div class="p-3 rounded-2 border border-warning-subtle bg-warning bg-opacity-10 h-100">
                    <h6 class="fw-bold text-warning-emphasis mb-2"><i class="bi bi-bullseye me-1"></i>Areas for Improvement</h6>
                    <div class="small text-dark">
                        ${evaluation.areasForImprovement != null ? evaluation.areasForImprovement : 'No development areas specified.'}
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
document.addEventListener('DOMContentLoaded', () => {
    const rawData = ${chartJson};
    const ctx = document.getElementById('detailRadarChart');
    if (ctx && rawData) {
        new Chart(ctx, {
            type: 'radar',
            data: {
                labels: rawData.labels,
                datasets: [{
                    label: 'Score',
                    data: rawData.scores,
                    backgroundColor: 'rgba(30, 58, 138, 0.25)',
                    borderColor: '#1e3a8a',
                    pointBackgroundColor: '#1e3a8a',
                    borderWidth: 2
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    r: {
                        beginAtZero: true,
                        max: 5.0,
                        ticks: { stepSize: 1.0 }
                    }
                }
            }
        });
    }
});
</script>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
