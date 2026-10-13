<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="My Dashboard"/>
</jsp:include>

<div class="d-flex justify-content-between align-items-center mb-4">
    <div>
        <h2 class="fw-bold mb-1">Welcome, ${employee.fullName}</h2>
        <p class="text-muted mb-0">${employee.designation} &bull; ${employee.departmentName} &bull; Code: <strong>${employee.employeeCode}</strong></p>
    </div>
    <a href="${pageContext.request.contextPath}/employee/history" class="btn btn-outline-primary btn-sm">
        <i class="bi bi-clock-history me-1"></i> Full Evaluation History
    </a>
</div>

<c:choose>
    <c:when test="${not empty latestEvaluation}">
        <!-- Latest Performance Review Highlight Card -->
        <div class="row g-4 mb-4">
            <div class="col-lg-4">
                <div class="card border-0 shadow-sm rounded-3 h-100 text-center p-4">
                    <span class="badge bg-light text-muted border align-self-center px-3 py-1 mb-2">Latest Review</span>
                    <h5 class="fw-bold text-dark mb-1">${latestEvaluation.cycleTitle}</h5>
                    <div class="text-muted small mb-3">Evaluated by: <strong>${latestEvaluation.evaluatorName}</strong></div>

                    <div class="my-auto py-3">
                        <div class="display-3 fw-bold text-primary mb-1">
                            ${latestEvaluation.totalScore != null ? latestEvaluation.totalScore : 'N/A'}
                        </div>
                        <div class="text-muted small mb-3">Total Score (out of 5.0)</div>
                        <span class="badge ${latestEvaluation.ratingBadgeClass} fs-6 px-3 py-2">
                            ${latestEvaluation.rating}
                        </span>
                    </div>

                    <div class="border-top pt-3 mt-3">
                        <a href="${pageContext.request.contextPath}/employee/evaluation-view?id=${latestEvaluation.id}" 
                           class="btn btn-primary w-100">
                            <i class="bi bi-file-text me-1"></i> View Full Evaluation Report
                        </a>
                    </div>
                </div>
            </div>

            <!-- Radar / Criteria Performance Chart -->
            <div class="col-lg-8">
                <div class="card border-0 shadow-sm rounded-3 h-100 p-3">
                    <div class="card-header bg-white border-0 py-2 d-flex justify-content-between align-items-center">
                        <h6 class="fw-bold mb-0 text-dark">
                            <i class="bi bi-radar text-info me-2"></i>Performance Criteria Breakdown
                        </h6>
                        <span class="badge bg-light text-muted">Scale 1 - 5</span>
                    </div>
                    <div class="card-body">
                        <div class="chart-container" style="height: 280px;">
                            <canvas id="employeeRadarChart"></canvas>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Manager Feedback & Notes Section -->
        <div class="card border-0 shadow-sm rounded-3 mb-4">
            <div class="card-header bg-white border-0 py-3">
                <h6 class="fw-bold mb-0 text-dark">
                    <i class="bi bi-chat-quote-fill text-success me-2"></i>Manager Feedback & Development Guidance
                </h6>
            </div>
            <div class="card-body p-4">
                <div class="mb-4">
                    <h6 class="fw-bold text-secondary small text-uppercase">Summary Comments</h6>
                    <p class="text-dark mb-0 bg-light p-3 rounded-2 border">
                        ${latestEvaluation.summaryFeedback != null ? latestEvaluation.summaryFeedback : 'No summary feedback recorded.'}
                    </p>
                </div>
                <div class="row g-3">
                    <div class="col-md-6">
                        <div class="p-3 rounded-2 border border-success-subtle bg-success bg-opacity-10">
                            <h6 class="fw-bold text-success mb-2"><i class="bi bi-trophy-fill me-1"></i>Key Strengths</h6>
                            <p class="small text-dark mb-0">
                                ${latestEvaluation.strengths != null ? latestEvaluation.strengths : 'None noted.'}
                            </p>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="p-3 rounded-2 border border-warning-subtle bg-warning bg-opacity-10">
                            <h6 class="fw-bold text-warning-emphasis mb-2"><i class="bi bi-arrow-up-right-circle-fill me-1"></i>Growth & Improvement Goals</h6>
                            <p class="small text-dark mb-0">
                                ${latestEvaluation.areasForImprovement != null ? latestEvaluation.areasForImprovement : 'None noted.'}
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Embedded Radar Chart Initialization -->
        <script>
        document.addEventListener('DOMContentLoaded', () => {
            const labels = [
                <c:forEach items="${latestEvaluation.scores}" var="s" varStatus="st">
                    "${s.criteriaName}"${!st.last ? ',' : ''}
                </c:forEach>
            ];
            const data = [
                <c:forEach items="${latestEvaluation.scores}" var="s" varStatus="st">
                    ${s.score != null ? s.score : 0}${!st.last ? ',' : ''}
                </c:forEach>
            ];

            const ctx = document.getElementById('employeeRadarChart');
            if (ctx && labels.length > 0) {
                new Chart(ctx, {
                    type: 'radar',
                    data: {
                        labels: labels,
                        datasets: [{
                            label: 'My Scores',
                            data: data,
                            backgroundColor: 'rgba(59, 130, 246, 0.25)',
                            borderColor: '#2563eb',
                            pointBackgroundColor: '#1e40af',
                            pointBorderColor: '#fff',
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
    </c:when>
    <c:otherwise>
        <!-- Empty State -->
        <div class="card border-0 shadow-sm rounded-3 text-center p-5 mb-4">
            <i class="bi bi-clipboard-data display-3 text-muted mb-3"></i>
            <h4 class="fw-bold text-dark">No Performance Evaluations Yet</h4>
            <p class="text-muted mx-auto" style="max-width: 500px;">
                Your performance review will appear here once your manager completes an evaluation during an active review cycle.
            </p>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
