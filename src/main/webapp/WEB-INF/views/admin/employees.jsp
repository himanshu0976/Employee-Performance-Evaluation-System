<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Manage Employees"/>
</jsp:include>

<div class="d-flex justify-content-between align-items-center mb-4">
    <div>
        <h2 class="fw-bold mb-1">Employee Directory</h2>
        <p class="text-muted mb-0">Manage employee accounts, job designations, department assignments, and reporting managers</p>
    </div>
    <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addEmployeeModal">
        <i class="bi bi-person-plus-fill me-1"></i> Add Employee
    </button>
</div>

<div class="card border-0 shadow-sm rounded-3">
    <div class="table-responsive">
        <table class="table table-hover align-middle table-eps mb-0">
            <thead>
                <tr>
                    <th>Code</th>
                    <th>Name</th>
                    <th>Department</th>
                    <th>Designation</th>
                    <th>Reporting Manager</th>
                    <th>Status</th>
                    <th class="text-end">Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${employees}" var="emp">
                    <tr>
                        <td><span class="badge bg-light text-dark border font-monospace">${emp.employeeCode}</span></td>
                        <td>
                            <div class="fw-semibold text-dark">${emp.fullName}</div>
                            <div class="text-muted small">${emp.email} &bull; @${emp.username}</div>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${not empty emp.departmentName}">
                                    <span class="badge bg-info bg-opacity-10 text-info-emphasis border border-info-subtle">
                                        ${emp.departmentName}
                                    </span>
                                </c:when>
                                <c:otherwise>
                                    <span class="text-muted fst-italic">Unassigned</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td class="text-secondary small fw-medium">${emp.designation}</td>
                        <td>
                            <c:choose>
                                <c:when test="${not empty emp.managerName}">
                                    <div class="d-flex align-items-center">
                                        <i class="bi bi-person-badge text-primary me-1"></i>
                                        <span class="small fw-semibold">${emp.managerName}</span>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-warning bg-opacity-10 text-warning-emphasis">No Manager</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${emp.active}">
                                    <span class="badge bg-success">Active</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-danger">Inactive</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td class="text-end">
                            <button type="button" class="btn btn-sm btn-outline-primary me-1"
                                    data-bs-toggle="modal" data-bs-target="#editEmployeeModal${emp.employeeId}">
                                <i class="bi bi-pencil-square"></i>
                            </button>
                            <form action="${pageContext.request.contextPath}/admin/employees" method="POST" class="d-inline"
                                  onsubmit="return confirm('Are you sure you want to delete employee ${emp.fullName}? This will remove all their evaluations.');">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="employeeId" value="${emp.employeeId}">
                                <button type="submit" class="btn btn-sm btn-outline-danger">
                                    <i class="bi bi-trash"></i>
                                </button>
                            </form>
                        </td>
                    </tr>

                    <!-- Edit Employee Modal -->
                    <div class="modal fade" id="editEmployeeModal${emp.employeeId}" tabindex="-1" aria-hidden="true">
                        <div class="modal-dialog modal-lg">
                            <div class="modal-content">
                                <form action="${pageContext.request.contextPath}/admin/employees" method="POST">
                                    <input type="hidden" name="action" value="edit">
                                    <input type="hidden" name="employeeId" value="${emp.employeeId}">
                                    <div class="modal-header">
                                        <h5 class="modal-title fw-bold">Edit Employee: ${emp.fullName}</h5>
                                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                    </div>
                                    <div class="modal-body">
                                        <div class="row g-3">
                                            <div class="col-md-6">
                                                <label class="form-label fw-semibold small">Full Name</label>
                                                <input type="text" class="form-control" name="fullName" value="${emp.fullName}" required>
                                            </div>
                                            <div class="col-md-6">
                                                <label class="form-label fw-semibold small">Employee Code</label>
                                                <input type="text" class="form-control" name="employeeCode" value="${emp.employeeCode}" required>
                                            </div>
                                            <div class="col-md-6">
                                                <label class="form-label fw-semibold small">Email Address</label>
                                                <input type="email" class="form-control" name="email" value="${emp.email}" required>
                                            </div>
                                            <div class="col-md-6">
                                                <label class="form-label fw-semibold small">Phone Number</label>
                                                <input type="text" class="form-control" name="phone" value="${emp.phone}">
                                            </div>
                                            <div class="col-md-6">
                                                <label class="form-label fw-semibold small">Department</label>
                                                <select class="form-select" name="departmentId">
                                                    <option value="">-- Select Department --</option>
                                                    <c:forEach items="${departments}" var="d">
                                                        <option value="${d.id}" ${d.id == emp.departmentId ? 'selected' : ''}>${d.name} (${d.code})</option>
                                                    </c:forEach>
                                                </select>
                                            </div>
                                            <div class="col-md-6">
                                                <label class="form-label fw-semibold small">Assign Manager</label>
                                                <select class="form-select" name="managerId">
                                                    <option value="">-- No Manager --</option>
                                                    <c:forEach items="${managers}" var="m">
                                                        <option value="${m.id}" ${m.id == emp.managerId ? 'selected' : ''}>${m.fullName} (@${m.username})</option>
                                                    </c:forEach>
                                                </select>
                                            </div>
                                            <div class="col-md-6">
                                                <label class="form-label fw-semibold small">Designation</label>
                                                <input type="text" class="form-control" name="designation" value="${emp.designation}" required>
                                            </div>
                                            <div class="col-md-6">
                                                <label class="form-label fw-semibold small">Hire Date</label>
                                                <input type="date" class="form-control" name="hireDate" value="${emp.hireDate}">
                                            </div>
                                            <div class="col-md-6">
                                                <label class="form-label fw-semibold small">Salary ($)</label>
                                                <input type="number" step="0.01" class="form-control" name="salary" value="${emp.salary}">
                                            </div>
                                            <div class="col-md-6 d-flex align-items-center mt-4">
                                                <div class="form-check form-switch">
                                                    <input class="form-check-input" type="checkbox" name="active" value="true" id="actSwitch${emp.employeeId}" ${emp.active ? 'checked' : ''}>
                                                    <label class="form-check-label fw-semibold small" for="actSwitch${emp.employeeId}">Account Active</label>
                                                </div>
                                            </div>
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

<!-- Add Employee Modal -->
<div class="modal fade" id="addEmployeeModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <form action="${pageContext.request.contextPath}/admin/employees" method="POST">
                <input type="hidden" name="action" value="add">
                <div class="modal-header">
                    <h5 class="modal-title fw-bold">Register New Employee</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label fw-semibold small">Username</label>
                            <input type="text" class="form-control" name="username" placeholder="e.g. emp11" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold small">Initial Password</label>
                            <input type="password" class="form-control" name="password" placeholder="e.g. Employee@123" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold small">Full Name</label>
                            <input type="text" class="form-control" name="fullName" placeholder="e.g. Samuel Green" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold small">Employee Code</label>
                            <input type="text" class="form-control" name="employeeCode" placeholder="e.g. EMP-1011" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold small">Email Address</label>
                            <input type="email" class="form-control" name="email" placeholder="samuel.green@eps.com" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold small">Phone Number</label>
                            <input type="text" class="form-control" name="phone" placeholder="+1-555-0301">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold small">Department</label>
                            <select class="form-select" name="departmentId">
                                <option value="">-- Select Department --</option>
                                <c:forEach items="${departments}" var="d">
                                    <option value="${d.id}">${d.name} (${d.code})</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold small">Assign Reporting Manager</label>
                            <select class="form-select" name="managerId">
                                <option value="">-- No Manager --</option>
                                <c:forEach items="${managers}" var="m">
                                    <option value="${m.id}">${m.fullName} (@${m.username})</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold small">Job Designation</label>
                            <input type="text" class="form-control" name="designation" placeholder="e.g. Cloud Engineer" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold small">Hire Date</label>
                            <input type="date" class="form-control" name="hireDate">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold small">Salary ($)</label>
                            <input type="number" step="0.01" class="form-control" name="salary" placeholder="75000.00">
                        </div>
                        <div class="col-md-6 d-flex align-items-center mt-4">
                            <div class="form-check form-switch">
                                <input class="form-check-input" type="checkbox" name="active" value="true" id="addActiveSwitch" checked>
                                <label class="form-check-label fw-semibold small" for="addActiveSwitch">Account Active</label>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">Create Employee Account</button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
