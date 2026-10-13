<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Evaluate Employee"/>
</jsp:include>

<div class="mb-4">
    <a href="${pageContext.request.contextPath}/manager/evaluations" class="btn btn-outline-secondary btn-sm mb-2">
        <i class="bi bi-arrow-left me-1"></i> Back to Team Evaluations
    </a>
    <div class="d-flex flex-wrap justify-content-between align-items-center">
        <div>
            <h2 class="fw-bold mb-1">Performance Evaluation</h2>
            <p class="text-muted mb-0">Cycle: <strong class="text-dark">${activeCycle.title}</strong> &bull; Assessing Employee Performance</p>
        </div>
        <c:if test="${not empty evaluation and (evaluation.status == 'SUBMITTED' or evaluation.status == 'APPROVED')}">
            <span class="badge bg-success px-3 py-2 fs-6">
                <i class="bi bi-check-all me-1"></i> Already Submitted (${evaluation.rating})
            </span>
        </c:if>
    </div>
</div>

<div class="row g-4">
    <!-- Left Column: Employee Profile & Live Score Calculator -->
    <div class="col-lg-4">
        <!-- Employee Card -->
        <div class="card border-0 shadow-sm rounded-3 mb-4">
            <div class="card-header bg-primary text-white py-3">
                <h6 class="fw-bold mb-0"><i class="bi bi-person-badge-fill me-2"></i>Employee Details</h6>
            </div>
            <div class="card-body">
                <div class="text-center mb-3">
                    <div class="rounded-circle bg-light d-inline-flex p-3 mb-2 border">
                        <i class="bi bi-person text-secondary display-5"></i>
                    </div>
                    <h5 class="fw-bold mb-0">${employee.fullName}</h5>
                    <span class="badge bg-light text-dark border font-monospace mt-1">${employee.employeeCode}</span>
                </div>
                <hr>
                <div class="small">
                    <div class="d-flex justify-content-between mb-2">
                        <span class="text-muted">Designation:</span>
                        <span class="fw-semibold text-end">${employee.designation}</span>
                    </div>
                    <div class="d-flex justify-content-between mb-2">
                        <span class="text-muted">Department:</span>
                        <span class="fw-semibold text-end">${employee.departmentName}</span>
                    </div>
                    <div class="d-flex justify-content-between mb-2">
                        <span class="text-muted">Email:</span>
                        <span class="fw-semibold text-end">${employee.email}</span>
                    </div>
                    <div class="d-flex justify-content-between">
                        <span class="text-muted">Hire Date:</span>
                        <span class="fw-semibold text-end">${employee.hireDate}</span>
                    </div>
                </div>
            </div>
        </div>

        <!-- Live Score Summary Card -->
        <div class="card border-0 shadow-sm rounded-3 mb-4 border-top border-4 border-primary">
            <div class="card-body text-center p-4">
                <h6 class="text-muted text-uppercase small fw-bold mb-2">Estimated Weighted Score</h6>
                <div class="display-4 fw-bold text-primary mb-2" id="liveCalculatedScore">
                    ${evaluation != null and evaluation.totalScore != null ? evaluation.totalScore : '0.00'}
                </div>
                <div class="mb-3">
                    <span class="badge ${evaluation != null ? evaluation.ratingBadgeClass : 'bg-secondary'} fs-6 px-3 py-2" id="liveCalculatedRating">
                        ${evaluation != null and evaluation.rating != null ? evaluation.rating : 'Pending Calculation'}
                    </span>
                </div>
                <div class="border-top pt-3 text-start small text-muted">
                    <strong class="d-block mb-1 text-dark">Rating Scale Reference:</strong>
                    <ul class="list-unstyled mb-0" style="font-size: 0.8rem;">
                        <li><span class="badge bg-success me-1">4.50 - 5.00</span> Outstanding</li>
                        <li><span class="badge bg-primary me-1">3.50 - 4.49</span> Exceeds Expectations</li>
                        <li><span class="badge bg-info text-dark me-1">2.50 - 3.49</span> Meets Expectations</li>
                        <li><span class="badge bg-warning text-dark me-1">1.50 - 2.49</span> Needs Improvement</li>
                        <li><span class="badge bg-danger me-1">&lt; 1.50</span> Unsatisfactory</li>
                    </ul>
                </div>
            </div>
        </div>
    </div>

    <!-- Right Column: Evaluation Form -->
    <div class="col-lg-8">
        <form action="${pageContext.request.contextPath}/manager/evaluations" method="POST" id="evaluationForm">
            <input type="hidden" name="cycleId" value="${activeCycle.id}">
            <input type="hidden" name="employeeId" value="${employee.employeeId}">

            <!-- Criteria Section -->
            <div class="card border-0 shadow-sm rounded-3 mb-4">
                <div class="card-header bg-white border-0 py-3 d-flex justify-content-between align-items-center">
                    <h5 class="fw-bold mb-0 text-dark">
                        <i class="bi bi-sliders text-primary me-2"></i>Evaluation Criteria Grading
                    </h5>
                    <span class="badge bg-light text-muted">Scale 1 (Low) to 5 (High)</span>
                </div>
                <div class="card-body p-4">
                    <c:forEach items="${criteriaList}" var="crit" varStatus="st">
                        <c:set var="prevScore" value="${scoreMap[crit.id]}"/>
                        <div class="p-3 mb-3 bg-light rounded-3 border">
                            <div class="d-flex justify-content-between align-items-center mb-2">
                                <div>
                                    <h6 class="fw-bold mb-0 text-dark">${st.count}. ${crit.name}</h6>
                                    <small class="text-muted">${crit.description}</small>
                                </div>
                                <span class="badge bg-primary bg-opacity-10 text-primary border border-primary-subtle px-2 py-1">
                                    Weight: ${crit.weightage}%
                                </span>
                            </div>

                            <div class="row g-2 align-items-center mt-2">
                                <div class="col-md-4">
                                    <label class="form-label small fw-semibold text-secondary mb-1">Score (1.0 to 5.0)</label>
                                    <select class="form-select form-select-sm criterion-score-select" 
                                            name="score_${crit.id}" 
                                            data-weight="${crit.weightage}" required>
                                        <option value="">-- Choose Score --</option>
                                        <option value="5.0" ${prevScore != null and prevScore.score == 5.0 ? 'selected' : ''}>5.0 - Exceptional / Flawless</option>
                                        <option value="4.5" ${prevScore != null and prevScore.score == 4.5 ? 'selected' : ''}>4.5 - Excellent</option>
                                        <option value="4.0" ${prevScore != null and prevScore.score == 4.0 ? 'selected' : ''}>4.0 - Very Good</option>
                                        <option value="3.5" ${prevScore != null and prevScore.score == 3.5 ? 'selected' : ''}>3.5 - Above Average</option>
                                        <option value="3.0" ${prevScore != null and prevScore.score == 3.0 ? 'selected' : ''}>3.0 - Satisfactory / Standard</option>
                                        <option value="2.5" ${prevScore != null and prevScore.score == 2.5 ? 'selected' : ''}>2.5 - Marginal</option>
                                        <option value="2.0" ${prevScore != null and prevScore.score == 2.0 ? 'selected' : ''}>2.0 - Below Standard</option>
                                        <option value="1.5" ${prevScore != null and prevScore.score == 1.5 ? 'selected' : ''}>1.5 - Poor</option>
                                        <option value="1.0" ${prevScore != null and prevScore.score == 1.0 ? 'selected' : ''}>1.0 - Critical Failure</option>
                                    </select>
                                </div>
                                <div class="col-md-8">
                                    <label class="form-label small fw-semibold text-secondary mb-1">Criterion Qualitative Feedback</label>
                                    <input type="text" class="form-control form-control-sm" 
                                           name="feedback_${crit.id}" 
                                           value="${prevScore != null ? prevScore.feedback : ''}" 
                                           placeholder="Observations regarding ${crit.name}...">
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>

            <!-- Qualitative Feedback Section -->
            <div class="card border-0 shadow-sm rounded-3 mb-4">
                <div class="card-header bg-white border-0 py-3">
                    <h5 class="fw-bold mb-0 text-dark">
                        <i class="bi bi-chat-left-quote-fill text-success me-2"></i>Overall Feedback & Development
                    </h5>
                </div>
                <div class="card-body p-4">
                    <div class="mb-3">
                        <label class="form-label fw-semibold small">Executive Summary Comments</label>
                        <textarea class="form-control" name="summaryFeedback" rows="3" 
                                  placeholder="Summarize the employee's overall performance during this period...">${evaluation != null ? evaluation.summaryFeedback : ''}</textarea>
                    </div>

                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label fw-semibold small text-success">Key Strengths & Achievements</label>
                            <textarea class="form-control" name="strengths" rows="3" 
                                      placeholder="Notable strengths demonstrated...">${evaluation != null ? evaluation.strengths : ''}</textarea>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold small text-danger">Areas for Improvement & Action Goals</label>
                            <textarea class="form-control" name="areasForImprovement" rows="3" 
                                      placeholder="Specific growth opportunities and action items...">${evaluation != null ? evaluation.areasForImprovement : ''}</textarea>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Action Buttons -->
            <div class="d-flex justify-content-between align-items-center mb-5">
                <a href="${pageContext.request.contextPath}/manager/evaluations" class="btn btn-light">
                    Cancel
                </a>
                <div class="d-flex gap-2">
                    <button type="submit" name="action" value="save-draft" class="btn btn-outline-secondary px-4">
                        <i class="bi bi-save me-1"></i> Save Draft
                    </button>
                    <button type="submit" name="action" value="submit" class="btn btn-primary px-4 fw-semibold"
                            onclick="return confirm('Are you sure you want to submit this evaluation? Total scores and rating tiers will be finalized.');">
                        <i class="bi bi-send-check me-1"></i> Submit Evaluation
                    </button>
                </div>
            </div>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
