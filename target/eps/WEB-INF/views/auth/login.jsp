<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Employee Performance Evaluation System</title>
    <!-- Bootstrap 5.3 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <!-- Custom CSS -->
    <link href="${pageContext.request.contextPath}/assets/css/custom.css" rel="stylesheet">
</head>
<body class="bg-light d-flex align-items-center justify-content-center min-vh-100 py-5">

<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-7 col-lg-5">
            <div class="card auth-card shadow-lg">
                <div class="auth-header text-center">
                    <div class="display-6 fw-bold mb-1">
                        <i class="bi bi-speedometer2 me-2"></i>EPS
                    </div>
                    <div class="text-white-50 small">Employee Performance Evaluation System</div>
                </div>

                <div class="card-body p-4 p-md-5">
                    <h4 class="card-title fw-bold text-center mb-4 text-dark">Sign In</h4>

                    <c:if test="${param.logout == 'success'}">
                        <div class="alert alert-info alert-dismissible fade show small" role="alert">
                            <i class="bi bi-info-circle me-1"></i> You have been successfully logged out.
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>

                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger alert-dismissible fade show small" role="alert">
                            <i class="bi bi-exclamation-triangle me-1"></i> ${errorMessage}
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>

                    <form action="${pageContext.request.contextPath}/login" method="POST" autocomplete="off">
                        <div class="mb-3">
                            <label for="username" class="form-label fw-semibold text-secondary small">Username or Email</label>
                            <div class="input-group">
                                <span class="input-group-text bg-white"><i class="bi bi-person text-muted"></i></span>
                                <input type="text" class="form-control" id="username" name="username" 
                                       value="${username != null ? username : ''}" 
                                       placeholder="e.g. admin or emp1" required autofocus>
                            </div>
                        </div>

                        <div class="mb-4">
                            <label for="password" class="form-label fw-semibold text-secondary small">Password</label>
                            <div class="input-group">
                                <span class="input-group-text bg-white"><i class="bi bi-lock text-muted"></i></span>
                                <input type="password" class="form-control" id="password" name="password" 
                                       placeholder="Enter your password" required>
                                <button class="btn btn-outline-secondary" type="button" id="togglePassword">
                                    <i class="bi bi-eye"></i>
                                </button>
                            </div>
                        </div>

                        <button type="submit" class="btn btn-primary w-100 py-2 fw-semibold shadow-sm">
                            <i class="bi bi-box-arrow-in-right me-1"></i> Sign In to Portal
                        </button>
                    </form>

                    <!-- Quick Demo Credentials Selector -->
                    <div class="mt-4 pt-3 border-top">
                        <p class="text-center text-muted small fw-semibold mb-2">
                            <i class="bi bi-lightning-charge-fill text-warning me-1"></i>Demo Accounts (Click to Fill):
                        </p>
                        <div class="d-flex flex-wrap gap-2 justify-content-center">
                            <button type="button" class="btn btn-sm btn-outline-danger" 
                                    onclick="fillLogin('admin', 'Admin@123')">
                                <i class="bi bi-shield-lock me-1"></i>Admin
                            </button>
                            <button type="button" class="btn btn-sm btn-outline-primary" 
                                    onclick="fillLogin('manager1', 'Manager@123')">
                                <i class="bi bi-person-badge me-1"></i>Manager 1 (Eng)
                            </button>
                            <button type="button" class="btn btn-sm btn-outline-primary" 
                                    onclick="fillLogin('manager2', 'Manager@123')">
                                <i class="bi bi-person-badge me-1"></i>Manager 2 (Mkt/HR)
                            </button>
                            <button type="button" class="btn btn-sm btn-outline-success" 
                                    onclick="fillLogin('emp1', 'Employee@123')">
                                <i class="bi bi-person me-1"></i>Employee (Alice)
                            </button>
                        </div>
                    </div>
                </div>
            </div>
            
            <p class="text-center text-muted small mt-4">
                &copy; 2026 Employee Performance Evaluation System &bull; MVC Architecture
            </p>
        </div>
    </div>
</div>

<!-- Bootstrap 5.3 JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<!-- EPS Main JS -->
<script src="${pageContext.request.contextPath}/assets/js/main.js"></script>

</body>
</html>
