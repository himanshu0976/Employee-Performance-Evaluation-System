<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Manage Departments"/>
</jsp:include>

<div class="d-flex justify-content-between align-items-center mb-4">
    <div>
        <h2 class="fw-bold mb-1">Departments Management</h2>
        <p class="text-muted mb-0">Configure organizational departments and view staffing distribution</p>
    </div>
    <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addDepartmentModal">
        <i class="bi bi-plus-circle me-1"></i> Add Department
    </button>
</div>

<div class="card border-0 shadow-sm rounded-3">
    <div class="table-responsive">
        <table class="table table-hover align-middle table-eps mb-0">
            <thead>
                <tr>
                    <th>Code</th>
                    <th>Department Name</th>
                    <th>Description</th>
                    <th>Staff Count</th>
                    <th class="text-end">Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${departments}" var="d">
                    <tr>
                        <td><span class="badge bg-primary px-2 py-1">${d.code}</span></td>
                        <td class="fw-semibold text-dark">${d.name}</td>
                        <td class="text-muted small" style="max-width: 320px;">${d.description}</td>
                        <td>
                            <span class="badge bg-secondary rounded-pill px-3 py-2">
                                <i class="bi bi-people me-1"></i> ${d.employeeCount} Members
                            </span>
                        </td>
                        <td class="text-end">
                            <button type="button" class="btn btn-sm btn-outline-primary me-1"
                                    data-bs-toggle="modal" data-bs-target="#editDepartmentModal${d.id}">
                                <i class="bi bi-pencil-square"></i> Edit
                            </button>
                            <c:choose>
                                <c:when test="${d.employeeCount == 0}">
                                    <form action="${pageContext.request.contextPath}/admin/departments" method="POST" class="d-inline"
                                          onsubmit="return confirm('Are you sure you want to delete this department?');">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="id" value="${d.id}">
                                        <button type="submit" class="btn btn-sm btn-outline-danger">
                                            <i class="bi bi-trash"></i>
                                        </button>
                                    </form>
                                </c:when>
                                <c:otherwise>
                                    <button class="btn btn-sm btn-outline-secondary" disabled title="Cannot delete department with active staff">
                                        <i class="bi bi-trash"></i>
                                    </button>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>

                    <!-- Edit Department Modal -->
                    <div class="modal fade" id="editDepartmentModal${d.id}" tabindex="-1" aria-hidden="true">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <form action="${pageContext.request.contextPath}/admin/departments" method="POST">
                                    <input type="hidden" name="action" value="edit">
                                    <input type="hidden" name="id" value="${d.id}">
                                    <div class="modal-header">
                                        <h5 class="modal-title fw-bold">Edit Department</h5>
                                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                    </div>
                                    <div class="modal-body">
                                        <div class="mb-3">
                                            <label class="form-label fw-semibold small">Department Name</label>
                                            <input type="text" class="form-control" name="name" value="${d.name}" required>
                                        </div>
                                        <div class="mb-3">
                                            <label class="form-label fw-semibold small">Code</label>
                                            <input type="text" class="form-control" name="code" value="${d.code}" required maxlength="10">
                                        </div>
                                        <div class="mb-3">
                                            <label class="form-label fw-semibold small">Description</label>
                                            <textarea class="form-control" name="description" rows="3">${d.description}</textarea>
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

<!-- Add Department Modal -->
<div class="modal fade" id="addDepartmentModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <form action="${pageContext.request.contextPath}/admin/departments" method="POST">
                <input type="hidden" name="action" value="add">
                <div class="modal-header">
                    <h5 class="modal-title fw-bold">Create New Department</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label class="form-label fw-semibold small">Department Name</label>
                        <input type="text" class="form-control" name="name" placeholder="e.g. Finance" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold small">Department Code</label>
                        <input type="text" class="form-control" name="code" placeholder="e.g. FIN" required maxlength="10">
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold small">Description</label>
                        <textarea class="form-control" name="description" rows="3" placeholder="Brief overview of departmental scope"></textarea>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Create Department</button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
