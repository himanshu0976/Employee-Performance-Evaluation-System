<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Manager Dashboard"/>
</jsp:include>

<div class="d-flex justify-content-between align-items-center mb-4">
    <div>
        <h2 class="fw-bold mb-1">Manager Workspace</h2>
        <p class="text-muted mb-0">Evaluate direct reports, submit criterion scores, and deliver constructive feedback</p>
    </div>
    <a href="${pageContext.request.contextPath}/manager/evaluations" class="btn btn-primary">
        <i class="bi bi-clipboard2-check me-1"></i> Start Evaluations
    </a>
</div>

<!-- Active Review Cycle Alert -->
<c:choose>
    <c:when test="${not empty activeCycle}">
        <div class="card border-0 shadow-sm rounded-3 mb-4 border-start border-4 border-success">
            <div class="card-body p-4">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <div>
                        <span class="badge bg-success mb-1">Active Cycle</span>
                        <h4 class="fw-bold mb-0 text-dark">${activeCycle.title}</h4>
                        <div class="text-muted small">
                            Timeline: ${activeCycle.startDate} to ${activeCycle.endDate}
                        </div>
                    </div>
                    <div class="text-end">
                        <span class="fs-4 fw-bold text-primary">${submittedCount} / ${directReports.size()}</span>
                        <div class="text-muted small">Team Evaluations Submitted</div>
                    </div>
                </div>

                <div class="progress" style="height: 10px;">
                    <div class="progress-bar bg-success progress-bar-striped progress-bar-animated" 
                         role="progressbar" style="width: ${progressPercent}%;">
                    </div>
                </div>
            </div>
        </div>
    </c:when>
    <c:otherwise>
        <div class="alert alert-info d-flex align-items-center mb-4">
            <i class="bi bi-info-circle-fill fs-3 me-3"></i>
            <div>
                <strong>No Review Cycle Active.</strong> All evaluation periods are currently closed. You can view previous evaluations once cycles reopen.
            </div>
        </div>
    </c:otherwise>
</c:choose>

<!-- Metrics Row -->
<div class="row g-3 mb-4">
    <div class="col-md-4">
        <div class="card stat-card p-3">
            <div class="d-flex align-items-center">
                <div class="stat-icon bg-primary bg-opacity-10 text-primary me-3">
                    <i class="bi bi-people-fill"></i>
                </div>
                <div>
                    <h6 class="text-muted small text-uppercase mb-1">Direct Reports</h6>
                    <h3 class="fw-bold mb-0">${directReports.size()}</h3>
                </div>
            </div>
        </div>
    </div>
    <div class="col-md-4">
        <div class="card stat-card p-3">
            <div class="d-flex align-items-center">
                <div class="stat-icon bg-success bg-opacity-10 text-success me-3">
                    <i class="bi bi-check2-circle"></i>
                </div>
                <div>
                    <h6 class="text-muted small text-uppercase mb-1">Completed</h6>
                    <h3 class="fw-bold mb-0">${submittedCount}</h3>
                </div>
            </div>
        </div>
    </div>
    <div class="col-md-4">
        <div class="card stat-card p-3">
            <div class="d-flex align-items-center">
                <div class="stat-icon bg-warning bg-opacity-10 text-warning me-3">
                    <i class="bi bi-hourglass-top"></i>
                </div>
                <div>
                    <h6 class="text-muted small text-uppercase mb-1">Pending / Drafts</h6>
                    <h3 class="fw-bold mb-0">${pendingCount}</h3>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Team Members List -->
<div class="card border-0 shadow-sm rounded-3">
    <div class="card-header bg-white border-0 py-3">
        <h6 class="fw-bold mb-0 text-dark">
            <i class="bi bi-person-lines-fill text-primary me-2"></i>Assigned Team Members
        </h6>
    </div>
    <div class="table-responsive">
        <table class="table table-hover align-middle table-eps mb-0">
            <thead>
                <tr>
                    <th>Emp Code</th>
                    <th>Full Name</th>
                    <th>Department</th>
                    <th>Designation</th>
                    <th>Evaluation Status</th>
                    <th>Score & Rating</th>
                    <th class="text-end">Action</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${directReports}" var="emp">
                    <c:set var="eval" value="${teamEvaluations[emp.employeeId]}"/>
                    <tr>
                        <td><span class="badge bg-light text-dark border font-monospace">${emp.employeeCode}</span></td>
                        <td>
                            <div class="fw-semibold">${emp.fullName}</div>
                            <div class="text-muted small">${emp.email}</div>
                        </td>
                        <td>${emp.departmentName}</td>
                        <td class="small text-secondary">${emp.designation}</td>
                        <td>
                            <c:choose>
                                <c:when test="${not empty eval and (eval.status == 'SUBMITTED' or eval.status == 'APPROVED')}">
                                    <span class="badge bg-success"><i class="bi bi-check-circle me-1"></i>Submitted</span>
                                </c:when>
                                <c:when test="${not empty eval and eval.status == 'DRAFT'}">
                                    <span class="badge bg-warning text-dark"><i class="bi bi-pencil me-1"></i>Draft Saved</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-secondary"><i class="bi bi-clock me-1"></i>Pending</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${not empty eval and not empty eval.totalScore}">
                                    <span class="fw-bold text-primary">${eval.totalScore}</span>
                                    <span class="badge ${eval.ratingBadgeClass} ms-1">${eval.rating}</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="text-muted small">&mdash;</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td class="text-end">
                            <c:if test="${not empty activeCycle}">
                                <a href="${pageContext.request.contextPath}/manager/evaluations?action=evaluate&employeeId=${emp.employeeId}" 
                                   class="btn btn-sm ${not empty eval and (eval.status == 'SUBMITTED' or eval.status == 'APPROVED') ? 'btn-outline-secondary' : 'btn-primary'}">
                                    <i class="bi ${not empty eval and (eval.status == 'SUBMITTED' or eval.status == 'APPROVED') ? 'bi-eye' : 'bi-pencil-square'} me-1"></i>
                                    ${not empty eval and (eval.status == 'SUBMITTED' or eval.status == 'APPROVED') ? 'Review' : (not empty eval and eval.status == 'DRAFT' ? 'Continue Draft' : 'Evaluate')}
                                </a>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty directReports}">
                    <tr>
                        <td colspan="7" class="text-center py-4 text-muted">
                            <i class="bi bi-people fs-2 d-block mb-1"></i> No direct reports are currently assigned to you.
                        </td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
