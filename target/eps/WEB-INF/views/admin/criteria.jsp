<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Evaluation Criteria"/>
</jsp:include>

<div class="d-flex justify-content-between align-items-center mb-4">
    <div>
        <h2 class="fw-bold mb-1">Evaluation Criteria Management</h2>
        <p class="text-muted mb-0">Configure performance assessment dimensions, definitions, and percentage weightages</p>
    </div>
    <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addCriteriaModal">
        <i class="bi bi-plus-circle me-1"></i> Add Criterion
    </button>
</div>

<!-- Weight Allocation Status Banner -->
<div class="card border-0 shadow-sm rounded-3 mb-4">
    <div class="card-body p-3 d-flex justify-content-between align-items-center">
        <div>
            <h6 class="fw-bold mb-1">
                <i class="bi bi-pie-chart-fill text-primary me-2"></i>Total Active Weight Allocation
            </h6>
            <div class="text-muted small">Standard total should equal 100.00% across all active criteria for balanced evaluation scoring.</div>
        </div>
        <div class="text-end">
            <span class="fs-4 fw-bold ${totalWeightage == 100.00 ? 'text-success' : 'text-danger'}">
                ${totalWeightage}%
            </span>
            <c:if test="${totalWeightage != 100.00}">
                <div class="badge bg-warning text-dark small d-block">Weights do not total 100%</div>
            </c:if>
        </div>
    </div>
</div>

<div class="card border-0 shadow-sm rounded-3">
    <div class="table-responsive">
        <table class="table table-hover align-middle table-eps mb-0">
            <thead>
                <tr>
                    <th>#</th>
                    <th>Criterion Name</th>
                    <th>Description</th>
                    <th>Weightage (%)</th>
                    <th>Status</th>
                    <th class="text-end">Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${criteriaList}" var="c" varStatus="st">
                    <tr>
                        <td class="text-muted small fw-bold">${st.count}</td>
                        <td class="fw-semibold text-dark">${c.name}</td>
                        <td class="text-muted small" style="max-width: 360px;">${c.description}</td>
                        <td>
                            <span class="badge bg-primary bg-opacity-10 text-primary border border-primary-subtle px-3 py-2 fw-bold">
                                ${c.weightage}%
                            </span>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${c.active}">
                                    <span class="badge bg-success">Active</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-secondary">Disabled</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td class="text-end">
                            <button type="button" class="btn btn-sm btn-outline-primary me-1"
                                    data-bs-toggle="modal" data-bs-target="#editCriteriaModal${c.id}">
                                <i class="bi bi-pencil-square"></i> Edit
                            </button>
                            <form action="${pageContext.request.contextPath}/admin/criteria" method="POST" class="d-inline">
                                <input type="hidden" name="action" value="toggle">
                                <input type="hidden" name="id" value="${c.id}">
                                <input type="hidden" name="active" value="${!c.active}">
                                <button type="submit" class="btn btn-sm ${c.active ? 'btn-outline-warning' : 'btn-outline-success'}">
                                    <i class="bi ${c.active ? 'bi-pause-circle' : 'bi-play-circle'}"></i> 
                                    ${c.active ? 'Disable' : 'Enable'}
                                </button>
                            </form>
                        </td>
                    </tr>

                    <!-- Edit Criterion Modal -->
                    <div class="modal fade" id="editCriteriaModal${c.id}" tabindex="-1" aria-hidden="true">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <form action="${pageContext.request.contextPath}/admin/criteria" method="POST">
                                    <input type="hidden" name="action" value="edit">
                                    <input type="hidden" name="id" value="${c.id}">
                                    <div class="modal-header">
                                        <h5 class="modal-title fw-bold">Edit Criterion: ${c.name}</h5>
                                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                    </div>
                                    <div class="modal-body">
                                        <div class="mb-3">
                                            <label class="form-label fw-semibold small">Criterion Name</label>
                                            <input type="text" class="form-control" name="name" value="${c.name}" required>
                                        </div>
                                        <div class="mb-3">
                                            <label class="form-label fw-semibold small">Weightage (%)</label>
                                            <input type="number" step="0.01" class="form-control" name="weightage" value="${c.weightage}" required min="0.1" max="100">
                                        </div>
                                        <div class="mb-3">
                                            <label class="form-label fw-semibold small">Description</label>
                                            <textarea class="form-control" name="description" rows="3" required>${c.description}</textarea>
                                        </div>
                                        <div class="form-check form-switch">
                                            <input class="form-check-input" type="checkbox" name="active" value="true" id="cActive${c.id}" ${c.active ? 'checked' : ''}>
                                            <label class="form-check-label fw-semibold small" for="cActive${c.id}">Active in evaluations</label>
                                        </div>
                                    </div>
                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancel</button>
                                        <button type="submit" class="btn btn-primary">Save Changes</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<!-- Add Criteria Modal -->
<div class="modal fade" id="addCriteriaModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <form action="${pageContext.request.contextPath}/admin/criteria" method="POST">
                <input type="hidden" name="action" value="add">
                <div class="modal-header">
                    <h5 class="modal-title fw-bold">Add Evaluation Criterion</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label class="form-label fw-semibold small">Criterion Name</label>
                        <input type="text" class="form-control" name="name" placeholder="e.g. Leadership & Initiative" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold small">Weightage (%)</label>
                        <input type="number" step="0.01" class="form-control" name="weightage" placeholder="15.00" required min="0.1" max="100">
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold small">Description</label>
                        <textarea class="form-control" name="description" rows="3" placeholder="Performance indicators and definition for this criterion" required></textarea>
                    </div>
                    <div class="form-check form-switch">
                        <input class="form-check-input" type="checkbox" name="active" value="true" id="addCritActive" checked>
                        <label class="form-check-label fw-semibold small" for="addCritActive">Active in evaluations</label>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Create Criterion</button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
