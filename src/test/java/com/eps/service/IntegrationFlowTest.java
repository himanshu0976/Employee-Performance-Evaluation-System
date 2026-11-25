package com.eps.service;

import com.eps.dao.*;
import com.eps.dao.impl.*;
import com.eps.model.*;
import com.eps.service.impl.*;
import com.eps.util.DBUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end integration tests for core business flows:
 * - Admin, Manager, Employee authentication
 * - Department and Employee queries
 * - Evaluation scoring and rating calculations
 * - Report and CSV generation
 */
public class IntegrationFlowTest {

    private static AuthService authService;
    private static DepartmentService departmentService;
    private static EmployeeService employeeService;
    private static EvaluationService evaluationService;
    private static EvaluationCriteriaService criteriaService;
    private static EvaluationCycleService cycleService;
    private static ReportService reportService;

    @BeforeAll
    public static void setUp() {
        // Triggers DB initialization (MySQL or embedded fallback)
        DBUtil.initDataSource();

        authService = new AuthServiceImpl();
        departmentService = new DepartmentServiceImpl();
        employeeService = new EmployeeServiceImpl();
        evaluationService = new EvaluationServiceImpl();
        criteriaService = new EvaluationCriteriaServiceImpl();
        cycleService = new EvaluationCycleServiceImpl();
        reportService = new ReportServiceImpl();
    }

    @Test
    public void testAdminLoginFlow() {
        User admin = authService.authenticate("admin", "Admin@123");
        assertNotNull(admin);
        assertEquals(Role.ADMIN, admin.getRole());
        assertEquals("/admin/dashboard", admin.getDashboardUrl());
        assertEquals("System Administrator", admin.getRoleDisplayName());
    }

    @Test
    public void testManagerLoginFlow() {
        User manager = authService.authenticate("manager1", "Manager@123");
        assertNotNull(manager);
        assertEquals(Role.MANAGER, manager.getRole());
        assertEquals("/manager/dashboard", manager.getDashboardUrl());
        assertEquals("Manager / Evaluator", manager.getRoleDisplayName());
    }

    @Test
    public void testEmployeeLoginFlow() {
        User emp = authService.authenticate("emp1", "Employee@123");
        assertNotNull(emp);
        assertEquals(Role.EMPLOYEE, emp.getRole());
        assertEquals("/employee/dashboard", emp.getDashboardUrl());
        assertEquals("Employee", emp.getRoleDisplayName());
        assertTrue(emp instanceof Employee);
        Employee employee = (Employee) emp;
        assertEquals("EMP-1001", employee.getEmployeeCode());
        assertEquals("Engineering", employee.getDepartmentName());
    }

    @Test
    public void testDepartmentsRetrieval() {
        List<Department> departments = departmentService.getAllDepartments();
        assertNotNull(departments);
        assertEquals(3, departments.size());
        assertTrue(departments.stream().anyMatch(d -> "Engineering".equals(d.getName())));
        assertTrue(departments.stream().anyMatch(d -> "Marketing".equals(d.getName())));
        assertTrue(departments.stream().anyMatch(d -> "Human Resources".equals(d.getName())));
    }

    @Test
    public void testEvaluationCriteria() {
        List<EvaluationCriteria> criteria = criteriaService.getActiveCriteria();
        assertNotNull(criteria);
        assertEquals(6, criteria.size());
        assertTrue(criteria.stream().anyMatch(c -> "Attendance".equals(c.getName())));
        assertTrue(criteria.stream().anyMatch(c -> "Work Quality".equals(c.getName())));
        assertTrue(criteria.stream().anyMatch(c -> "Productivity".equals(c.getName())));
        assertTrue(criteria.stream().anyMatch(c -> "Teamwork".equals(c.getName())));
        assertTrue(criteria.stream().anyMatch(c -> "Communication".equals(c.getName())));
        assertTrue(criteria.stream().anyMatch(c -> "Goal Achievement".equals(c.getName())));
    }

    @Test
    public void testEvaluationCycles() {
        List<EvaluationCycle> cycles = cycleService.getAllCycles();
        assertNotNull(cycles);
        assertEquals(2, cycles.size());

        Optional<EvaluationCycle> activeCycle = cycleService.getActiveCycle();
        assertTrue(activeCycle.isPresent());
        assertEquals("Mid-Year Review 2026", activeCycle.get().getTitle());
        assertEquals(EvaluationCycle.Status.ACTIVE, activeCycle.get().getStatus());
    }

    @Test
    public void testManagerDirectReportsAndEvaluations() {
        User manager1 = authService.authenticate("manager1", "Manager@123");
        List<Employee> directReports = employeeService.getEmployeesByManager(manager1.getId());
        assertNotNull(directReports);
        assertEquals(5, directReports.size()); // Sarah Connor manages 5 engineering employees

        // Fetch evaluations for employee 1
        List<Evaluation> history = evaluationService.getEvaluationsForEmployee(1);
        assertNotNull(history);
        assertFalse(history.isEmpty());

        Evaluation latest = history.get(0);
        assertNotNull(latest.getTotalScore());
        assertNotNull(latest.getRating());
        assertNotNull(latest.getScores());
        assertFalse(latest.getScores().isEmpty());
    }

    @Test
    public void testOrganizationReportsAndCsv() {
        OrganizationStatsDTO stats = reportService.getOrganizationStats(null);
        assertNotNull(stats);
        assertTrue(stats.getTotalEmployees() >= 10);
        assertTrue(stats.getCompletedEvaluationsCount() >= 10);

        List<DepartmentReportDTO> deptReports = reportService.getDepartmentReports(null);
        assertNotNull(deptReports);
        assertFalse(deptReports.isEmpty());

        java.io.StringWriter sw = new java.io.StringWriter();
        reportService.exportEvaluationsCsv(new java.io.PrintWriter(sw), null, null, null);
        String csv = sw.toString();
        assertNotNull(csv);
        assertTrue(csv.contains("Employee Code"));
        assertTrue(csv.contains("Total Score"));
        assertTrue(csv.contains("Rating"));
    }

    @Test
    public void testAdminDepartmentCRUD() {
        Department dept = new Department();
        dept.setName("Operations & Logistics");
        dept.setCode("OPS");
        dept.setDescription("Supply chain, field operations, and facilities management");

        Department created = departmentService.createDepartment(dept);
        assertNotNull(created);
        assertNotNull(created.getId());

        Department fetched = departmentService.getDepartmentById(created.getId());
        assertEquals("Operations & Logistics", fetched.getName());
        assertEquals("OPS", fetched.getCode());

        fetched.setDescription("Updated operations description");
        departmentService.updateDepartment(fetched);

        Department updated = departmentService.getDepartmentById(created.getId());
        assertEquals("Updated operations description", updated.getDescription());

        departmentService.deleteDepartment(created.getId());
        assertThrows(com.eps.exception.ResourceNotFoundException.class, () -> departmentService.getDepartmentById(created.getId()));
    }

    @Test
    public void testEvaluationRatingBandsExhaustive() {
        assertEquals("Outstanding", Evaluation.determineRating(new BigDecimal("5.00")));
        assertEquals("Outstanding", Evaluation.determineRating(new BigDecimal("4.50")));
        assertEquals("Exceeds Expectations", Evaluation.determineRating(new BigDecimal("4.49")));
        assertEquals("Exceeds Expectations", Evaluation.determineRating(new BigDecimal("3.50")));
        assertEquals("Meets Expectations", Evaluation.determineRating(new BigDecimal("3.49")));
        assertEquals("Meets Expectations", Evaluation.determineRating(new BigDecimal("2.50")));
        assertEquals("Needs Improvement", Evaluation.determineRating(new BigDecimal("2.49")));
        assertEquals("Needs Improvement", Evaluation.determineRating(new BigDecimal("1.50")));
        assertEquals("Unsatisfactory", Evaluation.determineRating(new BigDecimal("1.49")));
        assertEquals("Unsatisfactory", Evaluation.determineRating(new BigDecimal("0.50")));
        assertEquals("Unsatisfactory", Evaluation.determineRating(null));
    }

    @Test
    public void testEvaluationScoreZeroWeightFallback() {
        Evaluation eval = new Evaluation();
        List<EvaluationScore> scores = new java.util.ArrayList<>();

        EvaluationScore s1 = new EvaluationScore();
        s1.setScore(new BigDecimal("4.00"));
        s1.setWeightage(BigDecimal.ZERO);
        scores.add(s1);

        EvaluationScore s2 = new EvaluationScore();
        s2.setScore(new BigDecimal("2.00"));
        s2.setWeightage(BigDecimal.ZERO);
        scores.add(s2);

        eval.setScores(scores);
        eval.calculateTotalScoreAndRating();

        // Fallback to unweighted arithmetic mean: (4.0 + 2.0) / 2 = 3.00
        assertEquals(new BigDecimal("3.00"), eval.getTotalScore());
        assertEquals("Meets Expectations", eval.getRating());
    }

    @Test
    public void testManagerSubmitDraftAndFinal() {
        User manager1 = authService.authenticate("manager1", "Manager@123");
        List<EvaluationCriteria> criteriaList = criteriaService.getActiveCriteria();
        List<EvaluationScore> scores = new java.util.ArrayList<>();

        for (EvaluationCriteria c : criteriaList) {
            EvaluationScore s = new EvaluationScore();
            s.setCriteriaId(c.getId());
            s.setCriteriaName(c.getName());
            s.setWeightage(c.getWeightage());
            s.setScore(new BigDecimal("4.0"));
            s.setFeedback("Solid performance");
            scores.add(s);
        }

        // Save Draft for employee 5 (Ethan Hunt)
        Evaluation draft = evaluationService.saveOrSubmitEvaluation(
                2, 5, manager1.getId(), scores, "Draft notes", "Quick learner", "Deadline adherence", false);
        assertNotNull(draft);
        assertEquals(Evaluation.Status.DRAFT, draft.getStatus());

        // Final Submit for employee 5
        Evaluation submitted = evaluationService.saveOrSubmitEvaluation(
                2, 5, manager1.getId(), scores, "Final review notes", "Fast execution", "Code reviews", true);
        assertNotNull(submitted);
        assertEquals(Evaluation.Status.SUBMITTED, submitted.getStatus());
        assertEquals("Exceeds Expectations", submitted.getRating());
    }

    @Test
    public void testRoleAuthorizationPrivileges() {
        User admin = authService.authenticate("admin", "Admin@123");
        assertTrue(admin.isAdmin());
        assertFalse(admin.isManager());
        assertFalse(admin.isEmployee());

        User manager = authService.authenticate("manager1", "Manager@123");
        assertFalse(manager.isAdmin());
        assertTrue(manager.isManager());
        assertFalse(manager.isEmployee());

        User emp = authService.authenticate("emp1", "Employee@123");
        assertFalse(emp.isAdmin());
        assertFalse(emp.isManager());
        assertTrue(emp.isEmployee());
    }
}
