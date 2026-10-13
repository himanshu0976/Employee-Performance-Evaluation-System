<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${param.title != null ? param.title : 'EPS'} - Employee Performance Evaluation System</title>
    <!-- Bootstrap 5.3 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <!-- Chart.js 4 -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.2/dist/chart.umd.min.js"></script>
    <!-- Custom CSS -->
    <link href="${pageContext.request.contextPath}/assets/css/custom.css" rel="stylesheet">
</head>
<body>

<header>
    <nav class="navbar navbar-expand-lg navbar-dark navbar-eps">
        <div class="container-fluid px-4">
            <a class="navbar-brand fw-bold d-flex align-items-center" href="${pageContext.request.contextPath}/">
                <i class="bi bi-speedometer2 text-info me-2 fs-4"></i>
                <span>EPS <span class="badge bg-light text-dark fw-normal fs-6">Enterprise</span></span>
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#epsNavbar"
                    aria-controls="epsNavbar" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="collapse navbar-collapse" id="epsNavbar">
                <c:if test="${not empty sessionScope.currentUser}">
                    <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                        <!-- Admin Navigation -->
                        <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
                            <li class="nav-item">
                                <a class="nav-link ${pageContext.request.servletPath == '/admin/dashboard' ? 'active' : ''}" 
                                   href="${pageContext.request.contextPath}/admin/dashboard">
                                    <i class="bi bi-grid-1x2 me-1"></i> Dashboard
                                </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link ${pageContext.request.servletPath == '/admin/departments' ? 'active' : ''}" 
                                   href="${pageContext.request.contextPath}/admin/departments">
                                    <i class="bi bi-building me-1"></i> Departments
                                </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link ${pageContext.request.servletPath == '/admin/employees' ? 'active' : ''}" 
                                   href="${pageContext.request.contextPath}/admin/employees">
                                    <i class="bi bi-people me-1"></i> Employees
                                </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link ${pageContext.request.servletPath == '/admin/criteria' ? 'active' : ''}" 
                                   href="${pageContext.request.contextPath}/admin/criteria">
                                    <i class="bi bi-sliders me-1"></i> Criteria
                                </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link ${pageContext.request.servletPath == '/admin/cycles' ? 'active' : ''}" 
                                   href="${pageContext.request.contextPath}/admin/cycles">
                                    <i class="bi bi-calendar-range me-1"></i> Cycles
                                </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link ${pageContext.request.servletPath == '/admin/reports' ? 'active' : ''}" 
                                   href="${pageContext.request.contextPath}/admin/reports">
                                    <i class="bi bi-bar-chart-line me-1"></i> Reports
                                </a>
                            </li>
                        </c:if>

                        <!-- Manager Navigation -->
                        <c:if test="${sessionScope.currentUser.role == 'MANAGER'}">
                            <li class="nav-item">
                                <a class="nav-link ${pageContext.request.servletPath == '/manager/dashboard' ? 'active' : ''}" 
                                   href="${pageContext.request.contextPath}/manager/dashboard">
                                    <i class="bi bi-grid-1x2 me-1"></i> Dashboard
                                </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link ${pageContext.request.servletPath == '/manager/evaluations' ? 'active' : ''}" 
                                   href="${pageContext.request.contextPath}/manager/evaluations">
                                    <i class="bi bi-clipboard2-check me-1"></i> Team Evaluations
                                </a>
                            </li>
                        </c:if>

                        <!-- Employee Navigation -->
                        <c:if test="${sessionScope.currentUser.role == 'EMPLOYEE'}">
                            <li class="nav-item">
                                <a class="nav-link ${pageContext.request.servletPath == '/employee/dashboard' ? 'active' : ''}" 
                                   href="${pageContext.request.contextPath}/employee/dashboard">
                                    <i class="bi bi-speedometer2 me-1"></i> My Dashboard
                                </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link ${pageContext.request.servletPath == '/employee/history' ? 'active' : ''}" 
                                   href="${pageContext.request.contextPath}/employee/history">
                                    <i class="bi bi-clock-history me-1"></i> Evaluation History
                                </a>
                            </li>
                        </c:if>
                    </ul>

                    <!-- User Profile & Logout -->
                    <div class="d-flex align-items-center gap-3">
                        <div class="text-white text-end d-none d-md-block">
                            <div class="fw-semibold small">${sessionScope.currentUser.fullName}</div>
                            <span class="badge bg-info text-dark" style="font-size: 0.72rem;">
                                ${sessionScope.currentUser.roleDisplayName}
                            </span>
                        </div>
                        <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-light btn-sm">
                            <i class="bi bi-box-arrow-right me-1"></i> Logout
                        </a>
                    </div>
                </c:if>
            </div>
        </div>
    </nav>
</header>

<main class="py-4">
    <div class="container-fluid px-4">
        <jsp:include page="/WEB-INF/views/common/alerts.jsp"/>
