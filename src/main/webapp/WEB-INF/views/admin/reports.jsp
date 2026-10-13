<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Organization Reports"/>
</jsp:include>

<div class="d-flex flex-wrap justify-content-between align-items-center mb-4 gap-3">
    <div>
        <h2 class="fw-bold mb-1">Organization Performance Reports</h2>
        <p class="text-muted mb-0">Cross-departmental evaluation analytics, rating distributions, and CSV data exports</p>
    </div>
    <div>
        <a href="${pageContext.request.contextPath}/admin/reports/export?cycleId=${selectedCycleId}&departmentId=${selectedDeptId}&rating=${selectedRating}" 
           class="btn btn-success shadow-sm">
            <i class="bi bi-file-earmark-spreadsheet me-1"></i> Export to CSV
        </a>
    </div>
</div>

<!-- Filter Bar -->
<div class="card border-0 shadow-sm rounded-3 mb-4">
    <div class="card-body p-3">
        <form method="GET" action="${pageContext.request.contextPath}/admin/reports" class="row g-2 align-items-center">
            <div class="col-md-4">
                <label class="form-label small fw-semibold text-muted mb-1">Review Cycle</label>
                <select name="cycleId" class="form-select form-select-sm" onchange="this.form.submit()">
                    <c:forEach items="${allCycles}" var="c">
                        <option value="${c.id}" ${c.id == selectedCycleId ? 'selected' : ''}>
                            ${c.title} (${c.status})
                        </option>
                    </c:forEach>
                </select>
            </div>
            <div class="col-md-4">
                <label class="form-label small fw-semibold text-muted mb-1">Department</label>
                <select name="departmentId" class="form-select form-select-sm" onchange="this.form.submit()">
                    <option value="">-- All Departments --</option>
                    <c:forEach items="${allDepts}" var="d">
                        <option value="${d.id}" ${d.id == selectedDeptId ? 'selected' : ''}>${d.name}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="col-md-3">
                <label class="form-label small fw-semibold text-muted mb-1">Performance Rating</label>
                <select name="rating" class="form-select form-select-sm" onchange="this.form.submit()">
                    <option value="">-- All Ratings --</option>
                    <option value="Outstanding" ${selectedRating == 'Outstanding' ? 'selected' : ''}>Outstanding (4.5 - 5.0)</option>
                    <option value="Exceeds Expectations" ${selectedRating == 'Exceeds Expectations' ? 'selected' : ''}>Exceeds Expectations (3.5 - 4.49)</option>
                    <option value="Meets Expectations" ${selectedRating == 'Meets Expectations' ? 'selected' : ''}>Meets Expectations (2.5 - 3.49)</option>
                    <option value="Needs Improvement" ${selectedRating == 'Needs Improvement' ? 'selected' : ''}>Needs Improvement (1.5 - 2.49)</option>
                    <option value="Unsatisfactory" ${selectedRating == 'Unsatisfactory' ? 'selected' : ''}>Unsatisfactory (&lt; 1.5)</option>
                </select>
            </div>
            <div class="col-md-1 d-flex align-items-end">
                <a href="${pageContext.request.contextPath}/admin/reports" class="btn btn-sm btn-outline-secondary w-100" title="Reset Filters">
                    <i class="bi bi-arrow-counterclockwise"></i>
                </a>
            </div>
        </form>
    </div>
</div>

<!-- Charts Row -->
<div class="row g-4 mb-4">
    <!-- Department Breakdown Chart -->
    <div class="col-lg-6">
        <div class="card border-0 shadow-sm rounded-3 h-100">
            <div class="card-header bg-white border-0 py-3">
                <h6 class="fw-bold mb-0 text-dark">
                    <i class="bi bi-bar-chart text-primary me-2"></i>Department Score Comparison
                </h6>
            </div>
            <div class="card-body">
                <div class="chart-container" style="height: 270px;">
                    <canvas id="reportDeptChart"></canvas>
                </div>
            </div>
        </div>
    </div>

    <!-- Rating Distribution Chart -->
    <div class="col-lg-6">
        <div class="card border-0 shadow-sm rounded-3 h-100">
            <div class="card-header bg-white border-0 py-3">
                <h6 class="fw-bold mb-0 text-dark">
                    <i class="bi bi-pie-chart text-success me-2"></i>Performance Rating Breakdown
                </h6>
            </div>
            <div class="card-body">
                <div class="chart-container" style="height: 270px;">
                    <canvas id="reportRatingChart"></canvas>
                </div>
            </div>
        </div>
    </div>

    <!-- Criteria Radar Chart -->
    <div class="col-12">
        <div class="card border-0 shadow-sm rounded-3">
            <div class="card-header bg-white border-0 py-3">
                <h6 class="fw-bold mb-0 text-dark">
                    <i class="bi bi-radar text-info me-2"></i>Evaluation Criteria Performance Averages
                </h6>
            </div>
            <div class="card-body">
                <div class="row align-items-center">
                    <div class="col-lg-7">
                        <div class="chart-container" style="height: 280px;">
                            <canvas id="reportCriteriaChart"></canvas>
                        </div>
                    </div>
                    <div class="col-lg-5">
                        <div class="table-responsive">
                            <table class="table table-sm align-middle mb-0">
                                <thead class="table-light">
                                    <tr>
                                        <th>Criterion</th>
                                        <th>Weight</th>
                                        <th>Avg Score</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${criteriaAverages}" var="ca">
                                        <tr>
                                            <td class="fw-semibold small">${ca.criteriaName}</td>
                                            <td class="small text-muted">${ca.weightage}%</td>
                                            <td class="fw-bold text-primary">${ca.averageScore} / 5.0</td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Detailed Evaluation Records Table -->
<div class="card border-0 shadow-sm rounded-3 mb-4">
    <div class="card-header bg-white border-0 py-3 d-flex justify-content-between align-items-center">
        <h6 class="fw-bold mb-0 text-dark">
            <i class="bi bi-table text-secondary me-2"></i>Filtered Evaluation Records (${filteredEvaluations.size()})
        </h6>
    </div>
    <div class="table-responsive">
        <table class="table table-hover align-middle table-eps mb-0">
            <thead>
                <tr>
                    <th>Emp Code</th>
                    <th>Employee Name</th>
                    <th>Department</th>
                    <th>Designation</th>
                    <th>Evaluator</th>
                    <th>Total Score</th>
                    <th>Rating Tier</th>
                    <th>Status</th>
                    <th>Summary Feedback</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${filteredEvaluations}" var="ev">
                    <tr>
                        <td><span class="badge bg-light text-dark border font-monospace">${ev.employeeCode}</span></td>
                        <td class="fw-semibold">${ev.employeeName}</td>
                        <td>${ev.departmentName}</td>
                        <td class="small text-muted">${ev.designation}</td>
                        <td class="small">${ev.evaluatorName}</td>
                        <td class="fw-bold text-primary">${ev.totalScore != null ? ev.totalScore : 'N/A'}</td>
                        <td>
                            <c:if test="${not empty ev.rating}">
                                <span class="badge ${ev.ratingBadgeClass}">${ev.rating}</span>
                            </c:if>
                        </td>
                        <td>
                            <span class="badge ${ev.statusBadgeClass}">${ev.status}</span>
                        </td>
                        <td class="text-truncate small text-muted" style="max-width: 280px;" title="${ev.summaryFeedback}">
                            ${ev.summaryFeedback != null ? ev.summaryFeedback : 'No comments submitted.'}
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty filteredEvaluations}">
                    <tr>
                        <td colspan="9" class="text-center py-4 text-muted">
                            <i class="bi bi-inbox fs-3 d-block mb-1"></i> No evaluation records match the selected filters.
                        </td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>
</div>

<script>
document.addEventListener('DOMContentLoaded', () => {
    // 1. Department Performance Bar Chart
    const deptLabels = [
        <c:forEach items="${deptReports}" var="d" varStatus="st">
            "${d.departmentName}"${!st.last ? ',' : ''}
        </c:forEach>
    ];
    const deptScores = [
        <c:forEach items="${deptReports}" var="d" varStatus="st">
            ${d.averageScore}${!st.last ? ',' : ''}
        </c:forEach>
    ];

    const ctxDept = document.getElementById('reportDeptChart');
    if (ctxDept && deptLabels.length > 0) {
        new Chart(ctxDept, {
            type: 'bar',
            data: {
                labels: deptLabels,
                datasets: [{
                    label: 'Average Score',
                    data: deptScores,
                    backgroundColor: 'rgba(30, 58, 138, 0.75)',
                    borderColor: '#1e3a8a',
                    borderWidth: 1.5,
                    borderRadius: 6
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: { beginAtZero: true, max: 5.0, ticks: { stepSize: 1.0 } }
                }
            }
        });
    }

    // 2. Rating Breakdown Pie Chart
    const ratingDist = ${gson != null ? 'null' : 'true'};
    const ratingLabels = ['Outstanding', 'Exceeds Expectations', 'Meets Expectations', 'Needs Improvement', 'Unsatisfactory'];
    const ratingData = [
        ${stats.ratingDistribution['Outstanding'] != null ? stats.ratingDistribution['Outstanding'] : 0},
        ${stats.ratingDistribution['Exceeds Expectations'] != null ? stats.ratingDistribution['Exceeds Expectations'] : 0},
        ${stats.ratingDistribution['Meets Expectations'] != null ? stats.ratingDistribution['Meets Expectations'] : 0},
        ${stats.ratingDistribution['Needs Improvement'] != null ? stats.ratingDistribution['Needs Improvement'] : 0},
        ${stats.ratingDistribution['Unsatisfactory'] != null ? stats.ratingDistribution['Unsatisfactory'] : 0}
    ];

    const ctxRating = document.getElementById('reportRatingChart');
    if (ctxRating) {
        new Chart(ctxRating, {
            type: 'pie',
            data: {
                labels: ratingLabels,
                datasets: [{
                    data: ratingData,
                    backgroundColor: ['#10b981', '#3b82f6', '#06b6d4', '#f59e0b', '#ef4444']
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { position: 'bottom' }
                }
            }
        });
    }

    // 3. Criteria Average Bar/Radar Chart
    const critLabels = [
        <c:forEach items="${criteriaAverages}" var="ca" varStatus="st">
            "${ca.criteriaName}"${!st.last ? ',' : ''}
        </c:forEach>
    ];
    const critScores = [
        <c:forEach items="${criteriaAverages}" var="ca" varStatus="st">
            ${ca.averageScore}${!st.last ? ',' : ''}
        </c:forEach>
    ];

    const ctxCrit = document.getElementById('reportCriteriaChart');
    if (ctxCrit && critLabels.length > 0) {
        new Chart(ctxCrit, {
            type: 'radar',
            data: {
                labels: critLabels,
                datasets: [{
                    label: 'Criteria Averages (Scale 1-5)',
                    data: critScores,
                    backgroundColor: 'rgba(14, 165, 233, 0.25)',
                    borderColor: '#0284c7',
                    pointBackgroundColor: '#0284c7',
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
