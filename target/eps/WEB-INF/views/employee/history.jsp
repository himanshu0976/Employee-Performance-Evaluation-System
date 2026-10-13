<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Evaluation History"/>
</jsp:include>

<div class="d-flex justify-content-between align-items-center mb-4">
    <div>
        <h2 class="fw-bold mb-1">My Performance Evaluation History</h2>
        <p class="text-muted mb-0">Record of all past review cycles, overall scores, ratings, and feedback</p>
    </div>
    <a href="${pageContext.request.contextPath}/employee/dashboard" class="btn btn-outline-secondary btn-sm">
        <i class="bi bi-arrow-left me-1"></i> Back to Dashboard
    </a>
</div>

<div class="card border-0 shadow-sm rounded-3">
    <div class="table-responsive">
        <table class="table table-hover align-middle table-eps mb-0">
            <thead>
                <tr>
                    <th>Review Cycle</th>
                    <th>Evaluator</th>
                    <th>Total Score</th>
                    <th>Performance Rating</th>
                    <th>Status</th>
                    <th>Date Evaluated</th>
                    <th class="text-end">Action</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${history}" var="ev">
                    <tr>
                        <td>
                            <div class="fw-bold text-dark">${ev.cycleTitle}</div>
                            <span class="badge bg-light text-muted border">${ev.cycleStatus}</span>
                        </td>
                        <td>
                            <div class="small fw-semibold">${ev.evaluatorName}</div>
                        </td>
                        <td>
                            <span class="fs-5 fw-bold text-primary">
                                ${ev.totalScore != null ? ev.totalScore : '&mdash;'}
                            </span>
                            <span class="text-muted small">/ 5.0</span>
                        </td>
                        <td>
                            <c:if test="${not empty ev.rating}">
                                <span class="badge ${ev.ratingBadgeClass} px-3 py-2">${ev.rating}</span>
                            </c:if>
                        </td>
                        <td>
                            <span class="badge ${ev.statusBadgeClass}">${ev.status}</span>
                        </td>
                        <td class="text-muted small">
                            ${ev.evaluatedAt != null ? ev.evaluatedAt.toLocalDate() : 'In Progress'}
                        </td>
                        <td class="text-end">
                            <a href="${pageContext.request.contextPath}/employee/evaluation-view?id=${ev.id}" 
                               class="btn btn-sm btn-outline-primary">
                                <i class="bi bi-eye me-1"></i> View Details
                            </a>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty history}">
                    <tr>
                        <td colspan="7" class="text-center py-5 text-muted">
                            <i class="bi bi-folder2-open fs-2 d-block mb-2"></i>
                            No performance evaluations have been logged for your account yet.
                        </td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
