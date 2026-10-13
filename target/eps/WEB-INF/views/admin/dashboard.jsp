<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Admin Dashboard"/>
</jsp:include>

<div class="d-flex justify-content-between align-items-center mb-4">
    <div>
        <h2 class="fw-bold mb-1">Administrator Dashboard</h2>
        <p class="text-muted mb-0">System-wide performance analytics, employee directories, and evaluation cycles</p>
    </div>
    <div class="d-flex gap-2">
        <a href="${pageContext.request.contextPath}/admin/cycles" class="btn btn-primary btn-sm">
            <i class="bi bi-calendar-plus me-1"></i> Manage Cycles
        </a>
        <a href="${pageContext.request.contextPath}/admin/reports" class="btn btn-outline-dark btn-sm">
            <i class="bi bi-file-earmark-bar-graph me-1"></i> Organization Reports
        </a>
    </div>
</div>

<!-- Active Cycle Alert Banner -->
<c:choose>
    <c:when test="${not empty activeCycle}">
        <div class="alert alert-primary border-primary d-flex justify-content-between align-items-center mb-4">
            <div class="d-flex align-items-center">
                <i class="bi bi-play-circle-fill text-primary fs-3 me-3"></i>
                <div>
                    <h6 class="fw-bold mb-0">Active Review Cycle: ${activeCycle.title}</h6>
                    <small class="text-muted">
                        Duration: ${activeCycle.startDate} to ${activeCycle.endDate} &bull; 
                        ${activeCycle.completedEvaluationsCount} of ${activeCycle.totalEligibleEmployees} Evaluations Completed
                    </small>
                </div>
            </div>
            <div>
                <span class="badge bg-success px-3 py-2">Cycle Active</span>
            </div>
        </div>
    </c:when>
    <c:otherwise>
        <div class="alert alert-warning d-flex justify-content-between align-items-center mb-4">
            <div>
                <i class="bi bi-exclamation-triangle-fill me-2"></i>
                <strong>No Active Review Cycle.</strong> Evaluations cannot be submitted until a cycle is active.
            </div>
            <a href="${pageContext.request.contextPath}/admin/cycles" class="btn btn-sm btn-warning">Activate Cycle</a>
        </div>
    </c:otherwise>
</c:choose>

<!-- High-Level Metric Cards -->
<div class="row g-3 mb-4">
    <div class="col-sm-6 col-xl-3">
        <div class="card stat-card p-3">
            <div class="d-flex align-items-center">
                <div class="stat-icon bg-primary bg-opacity-10 text-primary me-3">
                    <i class="bi bi-people-fill"></i>
                </div>
                <div>
                    <h6 class="text-muted small text-uppercase mb-1">Total Employees</h6>
                    <h3 class="fw-bold mb-0">${stats.totalEmployees}</h3>
                </div>
            </div>
        </div>
    </div>
    <div class="col-sm-6 col-xl-3">
        <div class="card stat-card p-3">
            <div class="d-flex align-items-center">
                <div class="stat-icon bg-success bg-opacity-10 text-success me-3">
                    <i class="bi bi-check-circle-fill"></i>
                </div>
                <div>
                    <h6 class="text-muted small text-uppercase mb-1">Evaluations Done</h6>
                    <h3 class="fw-bold mb-0">${stats.completedEvaluationsCount}</h3>
                </div>
            </div>
        </div>
    </div>
    <div class="col-sm-6 col-xl-3">
        <div class="card stat-card p-3">
            <div class="d-flex align-items-center">
                <div class="stat-icon bg-info bg-opacity-10 text-info me-3">
                    <i class="bi bi-building"></i>
                </div>
                <div>
                    <h6 class="text-muted small text-uppercase mb-1">Departments</h6>
                    <h3 class="fw-bold mb-0">${stats.totalDepartments}</h3>
                </div>
            </div>
        </div>
    </div>
    <div class="col-sm-6 col-xl-3">
        <div class="card stat-card p-3">
            <div class="d-flex align-items-center">
                <div class="stat-icon bg-warning bg-opacity-10 text-warning me-3">
                    <i class="bi bi-star-fill"></i>
                </div>
                <div>
                    <h6 class="text-muted small text-uppercase mb-1">Avg Score</h6>
                    <h3 class="fw-bold mb-0">${stats.overallAverageScore} <span class="fs-6 text-muted">/ 5.0</span></h3>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Charts Section -->
<div class="row g-4 mb-4">
    <!-- Department Performance Comparison Chart -->
    <div class="col-lg-8">
        <div class="card border-0 shadow-sm rounded-3 h-100">
            <div class="card-header bg-white border-0 py-3 d-flex justify-content-between align-items-center">
                <h6 class="fw-bold mb-0 text-dark">
                    <i class="bi bi-bar-chart-line text-primary me-2"></i>Department Performance Averages
                </h6>
                <span class="badge bg-light text-muted">Current Cycle</span>
            </div>
            <div class="card-body">
                <div class="chart-container" style="height: 280px;">
                    <canvas id="deptPerformanceChart"></canvas>
                </div>
            </div>
        </div>
    </div>

    <!-- Rating Distribution Doughnut Chart -->
    <div class="col-lg-4">
        <div class="card border-0 shadow-sm rounded-3 h-100">
            <div class="card-header bg-white border-0 py-3">
                <h6 class="fw-bold mb-0 text-dark">
                    <i class="bi bi-pie-chart text-success me-2"></i>Rating Distribution
                </h6>
            </div>
            <div class="card-body">
                <div class="chart-container" style="height: 280px;">
                    <canvas id="ratingDistributionChart"></canvas>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Recent Completed Evaluations Table -->
<div class="card border-0 shadow-sm rounded-3 mb-4">
    <div class="card-header bg-white border-0 py-3 d-flex justify-content-between align-items-center">
        <h6 class="fw-bold mb-0 text-dark">
            <i class="bi bi-clock-history text-secondary me-2"></i>Recent Evaluations
        </h6>
        <a href="${pageContext.request.contextPath}/admin/reports" class="btn btn-sm btn-outline-primary">
            View All Reports
        </a>
    </div>
    <div class="table-responsive">
        <table class="table table-hover align-middle table-eps mb-0">
            <thead>
                <tr>
                    <th>Employee</th>
                    <th>Code</th>
                    <th>Department</th>
                    <th>Evaluator</th>
                    <th>Score</th>
                    <th>Rating</th>
                    <th>Status</th>
                    <th>Date</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${recentEvaluations}" var="ev" begin="0" end="7">
                    <tr>
                        <td class="fw-semibold">${ev.employeeName}</td>
                        <td><span class="badge bg-light text-dark border">${ev.employeeCode}</span></td>
                        <td>${ev.departmentName}</td>
                        <td>${ev.evaluatorName}</td>
                        <td class="fw-bold text-primary">${ev.totalScore != null ? ev.totalScore : 'N/A'}</td>
                        <td>
                            <c:if test="${not empty ev.rating}">
                                <span class="badge ${ev.ratingBadgeClass}">${ev.rating}</span>
                            </c:if>
                        </td>
                        <td>
                            <span class="badge ${ev.statusBadgeClass}">${ev.status}</span>
                        </td>
                        <td class="text-muted small">${ev.evaluatedAt != null ? ev.evaluatedAt.toLocalDate() : 'Pending'}</td>
                    </tr>
                </c:forEach>
                <c:if test="${empty recentEvaluations}">
                    <tr>
                        <td colspan="8" class="text-center py-4 text-muted">
                            <i class="bi bi-inbox fs-3 d-block mb-1"></i> No evaluations recorded for this cycle yet.
                        </td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>
</div>

<!-- Embedded Chart Initialization Script -->
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

    const ctxDept = document.getElementById('deptPerformanceChart');
    if (ctxDept && deptLabels.length > 0) {
        new Chart(ctxDept, {
            type: 'bar',
            data: {
                labels: deptLabels,
                datasets: [{
                    label: 'Average Score (out of 5.0)',
                    data: deptScores,
                    backgroundColor: 'rgba(59, 130, 246, 0.75)',
                    borderColor: '#2563eb',
                    borderWidth: 1.5,
                    borderRadius: 6
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true,
                        max: 5.0,
                        ticks: { stepSize: 1.0 }
                    }
                }
            }
        });
    }

    // 2. Rating Distribution Doughnut Chart
    const ratingLabels = [
        'Outstanding', 'Exceeds Expectations', 'Meets Expectations', 'Needs Improvement', 'Unsatisfactory'
    ];
    const ratingCounts = [
        ${stats.ratingDistribution['Outstanding'] != null ? stats.ratingDistribution['Outstanding'] : 0},
        ${stats.ratingDistribution['Exceeds Expectations'] != null ? stats.ratingDistribution['Exceeds Expectations'] : 0},
        ${stats.ratingDistribution['Meets Expectations'] != null ? stats.ratingDistribution['Meets Expectations'] : 0},
        ${stats.ratingDistribution['Needs Improvement'] != null ? stats.ratingDistribution['Needs Improvement'] : 0},
        ${stats.ratingDistribution['Unsatisfactory'] != null ? stats.ratingDistribution['Unsatisfactory'] : 0}
    ];

    const ctxRating = document.getElementById('ratingDistributionChart');
    if (ctxRating) {
        new Chart(ctxRating, {
            type: 'doughnut',
            data: {
                labels: ratingLabels,
                datasets: [{
                    data: ratingCounts,
                    backgroundColor: [
                        '#10b981', // Outstanding - Green
                        '#3b82f6', // Exceeds - Blue
                        '#06b6d4', // Meets - Teal
                        '#f59e0b', // Needs Improvement - Amber
                        '#ef4444'  // Unsatisfactory - Red
                    ]
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: { boxWidth: 12, font: { size: 11 } }
                    }
                }
            }
        });
    }
});
</script>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
