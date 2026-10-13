<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Team Evaluations"/>
</jsp:include>

<div class="d-flex justify-content-between align-items-center mb-4">
    <div>
        <h2 class="fw-bold mb-1">Team Evaluations</h2>
        <p class="text-muted mb-0">Complete performance ratings and qualitative assessments for your direct reports</p>
    </div>
    <c:if test="${not empty activeCycle}">
        <span class="badge bg-success px-3 py-2 fs-6">
            <i class="bi bi-calendar-check me-1"></i> ${activeCycle.title}
        </span>
    </c:if>
</div>

<div class="card border-0 shadow-sm rounded-3">
    <div class="table-responsive">
        <table class="table table-hover align-middle table-eps mb-0">
            <thead>
                <tr>
                    <th>Emp Code</th>
                    <th>Employee Name</th>
                    <th>Designation</th>
                    <th>Department</th>
                    <th>Evaluation Status</th>
                    <th>Score</th>
                    <th>Rating</th>
                    <th class="text-end">Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${directReports}" var="emp">
                    <c:set var="eval" value="${evaluationsMap[emp.employeeId]}"/>
                    <tr>
                        <td><span class="badge bg-light text-dark border font-monospace">${emp.employeeCode}</span></td>
                        <td>
                            <div class="fw-semibold text-dark">${emp.fullName}</div>
                            <div class="text-muted small">${emp.email}</div>
                        </td>
                        <td class="small text-secondary">${emp.designation}</td>
                        <td>${emp.departmentName}</td>
                        <td>
                            <c:choose>
                                <c:when test="${not empty eval and (eval.status == 'SUBMITTED' or eval.status == 'APPROVED')}">
                                    <span class="badge bg-success"><i class="bi bi-check-circle-fill me-1"></i>Submitted</span>
                                </c:when>
                                <c:when test="${not empty eval and eval.status == 'DRAFT'}">
                                    <span class="badge bg-warning text-dark"><i class="bi bi-pencil-fill me-1"></i>Draft Saved</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-secondary"><i class="bi bi-clock-fill me-1"></i>Pending</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td class="fw-bold text-primary">
                            ${not empty eval and not empty eval.totalScore ? eval.totalScore : '&mdash;'}
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${not empty eval and not empty eval.rating}">
                                    <span class="badge ${eval.ratingBadgeClass}">${eval.rating}</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="text-muted small">&mdash;</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td class="text-end">
                            <c:choose>
                                <c:when test="${not empty activeCycle}">
                                    <a href="${pageContext.request.contextPath}/manager/evaluations?action=evaluate&employeeId=${emp.employeeId}" 
                                       class="btn btn-sm ${not empty eval and (eval.status == 'SUBMITTED' or eval.status == 'APPROVED') ? 'btn-outline-primary' : 'btn-primary'}">
                                        <i class="bi ${not empty eval and (eval.status == 'SUBMITTED' or eval.status == 'APPROVED') ? 'bi-eye' : 'bi-pencil-square'} me-1"></i>
                                        ${not empty eval and (eval.status == 'SUBMITTED' or eval.status == 'APPROVED') ? 'Edit / View' : (not empty eval and eval.status == 'DRAFT' ? 'Continue Draft' : 'Begin Evaluation')}
                                    </a>
                                </c:when>
                                <c:otherwise>
                                    <button class="btn btn-sm btn-outline-secondary" disabled>Cycle Closed</button>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty directReports}">
                    <tr>
                        <td colspan="8" class="text-center py-4 text-muted">
                            <i class="bi bi-inbox fs-2 d-block mb-1"></i> No employees assigned to your team.
                        </td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
