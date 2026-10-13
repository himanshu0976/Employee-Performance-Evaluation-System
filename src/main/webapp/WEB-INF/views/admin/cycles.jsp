<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Evaluation Cycles"/>
</jsp:include>

<div class="d-flex justify-content-between align-items-center mb-4">
    <div>
        <h2 class="fw-bold mb-1">Evaluation Cycles</h2>
        <p class="text-muted mb-0">Plan, launch, and monitor organizational performance review periods</p>
    </div>
    <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addCycleModal">
        <i class="bi bi-calendar-plus me-1"></i> New Review Cycle
    </button>
</div>

<div class="card border-0 shadow-sm rounded-3">
    <div class="table-responsive">
        <table class="table table-hover align-middle table-eps mb-0">
            <thead>
                <tr>
                    <th>Cycle Title</th>
                    <th>Timeline</th>
                    <th>Status</th>
                    <th>Evaluation Progress</th>
                    <th class="text-end">Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${cycles}" var="c">
                    <tr>
                        <td>
                            <div class="fw-bold text-dark">${c.title}</div>
                            <div class="text-muted small">${c.description}</div>
                        </td>
                        <td>
                            <div class="small fw-semibold text-secondary">
                                <i class="bi bi-calendar-event me-1"></i> ${c.startDate} to ${c.endDate}
                            </div>
                        </td>
                        <td>
                            <span class="badge ${c.statusBadgeClass} px-3 py-2 fw-semibold">
                                ${c.status}
                            </span>
                        </td>
                        <td style="min-width: 220px;">
                            <div class="d-flex justify-content-between small text-muted mb-1">
                                <span>${c.completedEvaluationsCount} of ${c.totalEligibleEmployees} Completed</span>
                                <span>
                                    ${c.totalEligibleEmployees > 0 ? Math.round((c.completedEvaluationsCount * 100.0) / c.totalEligibleEmployees) : 0}%
                                </span>
                            </div>
                            <div class="progress" style="height: 6px;">
                                <div class="progress-bar ${c.status == 'ACTIVE' ? 'bg-success' : 'bg-primary'}" 
                                     role="progressbar" 
                                     style="width: ${c.totalEligibleEmployees > 0 ? (c.completedEvaluationsCount * 100.0) / c.totalEligibleEmployees : 0}%;">
                                </div>
                            </div>
                        </td>
                        <td class="text-end">
                            <button type="button" class="btn btn-sm btn-outline-primary me-1"
                                    data-bs-toggle="modal" data-bs-target="#editCycleModal${c.id}">
                                <i class="bi bi-pencil-square"></i>
                            </button>

                            <!-- Quick State Transition Dropdown -->
                            <div class="btn-group btn-group-sm">
                                <button type="button" class="btn btn-outline-secondary dropdown-toggle" data-bs-toggle="dropdown">
                                    Change Status
                                </button>
                                <ul class="dropdown-menu dropdown-menu-end">
                                    <c:if test="${c.status != 'ACTIVE'}">
                                        <li>
                                            <form action="${pageContext.request.contextPath}/admin/cycles" method="POST">
                                                <input type="hidden" name="action" value="set-status">
                                                <input type="hidden" name="id" value="${c.id}">
                                                <input type="hidden" name="status" value="ACTIVE">
                                                <button type="submit" class="dropdown-item text-success">
                                                    <i class="bi bi-play-circle me-2"></i>Set Active
                                                </button>
                                            </form>
                                        </li>
                                    </c:if>
                                    <c:if test="${c.status != 'COMPLETED'}">
                                        <li>
                                            <form action="${pageContext.request.contextPath}/admin/cycles" method="POST">
                                                <input type="hidden" name="action" value="set-status">
                                                <input type="hidden" name="id" value="${c.id}">
                                                <input type="hidden" name="status" value="COMPLETED">
                                                <button type="submit" class="dropdown-item text-secondary">
                                                    <i class="bi bi-check-all me-2"></i>Mark Completed
                                                </button>
                                            </form>
                                        </li>
                                    </c:if>
                                    <c:if test="${c.status != 'UPCOMING'}">
                                        <li>
                                            <form action="${pageContext.request.contextPath}/admin/cycles" method="POST">
                                                <input type="hidden" name="action" value="set-status">
                                                <input type="hidden" name="id" value="${c.id}">
                                                <input type="hidden" name="status" value="UPCOMING">
                                                <button type="submit" class="dropdown-item text-warning">
                                                    <i class="bi bi-hourglass-split me-2"></i>Set Upcoming
                                                </button>
                                            </form>
                                        </li>
                                    </c:if>
                                </ul>
                            </div>
                        </td>
                    </tr>

                    <!-- Edit Cycle Modal -->
                    <div class="modal fade" id="editCycleModal${c.id}" tabindex="-1" aria-hidden="true">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <form action="${pageContext.request.contextPath}/admin/cycles" method="POST">
                                    <input type="hidden" name="action" value="edit">
                                    <input type="hidden" name="id" value="${c.id}">
                                    <div class="modal-header">
                                        <h5 class="modal-title fw-bold">Edit Review Cycle</h5>
                                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                    </div>
                                    <div class="modal-body">
                                        <div class="mb-3">
                                            <label class="form-label fw-semibold small">Cycle Title</label>
                                            <input type="text" class="form-control" name="title" value="${c.title}" required>
                                        </div>
                                        <div class="mb-3">
                                            <label class="form-label fw-semibold small">Description</label>
                                            <textarea class="form-control" name="description" rows="2">${c.description}</textarea>
                                        </div>
                                        <div class="row g-2 mb-3">
                                            <div class="col-6">
                                                <label class="form-label fw-semibold small">Start Date</label>
                                                <input type="date" class="form-control" name="startDate" value="${c.startDate}" required>
                                            </div>
                                            <div class="col-6">
                                                <label class="form-label fw-semibold small">End Date</label>
                                                <input type="date" class="form-control" name="endDate" value="${c.endDate}" required>
                                            </div>
                                        </div>
                                        <div class="mb-3">
                                            <label class="form-label fw-semibold small">Cycle Status</label>
                                            <select class="form-select" name="status">
                                                <option value="UPCOMING" ${c.status == 'UPCOMING' ? 'selected' : ''}>UPCOMING</option>
                                                <option value="ACTIVE" ${c.status == 'ACTIVE' ? 'selected' : ''}>ACTIVE</option>
                                                <option value="COMPLETED" ${c.status == 'COMPLETED' ? 'selected' : ''}>COMPLETED</option>
                                            </select>
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

<!-- Add Cycle Modal -->
<div class="modal fade" id="addCycleModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <form action="${pageContext.request.contextPath}/admin/cycles" method="POST">
                <input type="hidden" name="action" value="add">
                <div class="modal-header">
                    <h5 class="modal-title fw-bold">Initiate Evaluation Cycle</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label class="form-label fw-semibold small">Cycle Title</label>
                        <input type="text" class="form-control" name="title" placeholder="e.g. Annual Review 2026" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold small">Description</label>
                        <textarea class="form-control" name="description" rows="2" placeholder="Review scope and objectives"></textarea>
                    </div>
                    <div class="row g-2 mb-3">
                        <div class="col-6">
                            <label class="form-label fw-semibold small">Start Date</label>
                            <input type="date" class="form-control" name="startDate" required>
                        </div>
                        <div class="col-6">
                            <label class="form-label fw-semibold small">End Date</label>
                            <input type="date" class="form-control" name="endDate" required>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold small">Initial Status</label>
                        <select class="form-select" name="status">
                            <option value="UPCOMING">UPCOMING (Pre-planning)</option>
                            <option value="ACTIVE" selected>ACTIVE (Open for reviews)</option>
                            <option value="COMPLETED">COMPLETED (Archived)</option>
                        </select>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Create Cycle</button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
