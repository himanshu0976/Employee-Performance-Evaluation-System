package com.eps.e2e;

import com.eps.ServerRunner;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class E2EHttpFlowTest {
    private static ServerRunner runner;
    private static int port;
    private static String baseUrl;

    @BeforeAll
    static void setUp() throws Exception {
        // Find an available port
        try (ServerSocket socket = new ServerSocket(0)) {
            port = socket.getLocalPort();
        }

        runner = new ServerRunner(port);
        runner.start();
        baseUrl = "http://localhost:" + port + "/eps";
    }

    @AfterAll
    static void tearDown() {
        if (runner != null) {
            runner.stop();
        }
    }

    private HttpClient createClient() {
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        return HttpClient.newBuilder()
                .cookieHandler(cookieManager)
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();
    }

    private String buildFormData(Map<String, String> data) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }
        return builder.toString();
    }

    @Test
    void testLoginPageRenders() throws IOException, InterruptedException {
        HttpClient client = createClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/login"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Employee Performance Evaluation System"), 
                "Login page should render branding title");
    }

    @Test
    void testAdminFullFlow() throws IOException, InterruptedException {
        HttpClient client = createClient();

        // 1. Submit login
        Map<String, String> form = new HashMap<>();
        form.put("username", "admin");
        form.put("password", "Admin@123");

        HttpRequest loginReq = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/login"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(buildFormData(form)))
                .build();

        HttpResponse<String> loginRes = client.send(loginReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(302, loginRes.statusCode());
        assertTrue(loginRes.headers().firstValue("Location").orElse("").contains("/admin/dashboard"));

        // 2. Access Admin Dashboard
        HttpRequest dashReq = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/admin/dashboard"))
                .GET()
                .build();
        HttpResponse<String> dashRes = client.send(dashReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, dashRes.statusCode());
        assertTrue(dashRes.body().contains("Administrator Dashboard") || dashRes.body().contains("Eleanor Vance"));

        // 3. Access Departments CRUD
        HttpRequest deptReq = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/admin/departments"))
                .GET()
                .build();
        HttpResponse<String> deptRes = client.send(deptReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, deptRes.statusCode());
        assertTrue(deptRes.body().contains("Engineering"));

        // 4. Access Employees CRUD
        HttpRequest empReq = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/admin/employees"))
                .GET()
                .build();
        HttpResponse<String> empRes = client.send(empReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, empRes.statusCode());
        assertTrue(empRes.body().contains("Alice Johnson"));

        // 5. Access Criteria
        HttpRequest critReq = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/admin/criteria"))
                .GET()
                .build();
        HttpResponse<String> critRes = client.send(critReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, critRes.statusCode());
        assertTrue(critRes.body().contains("Attendance"));
        assertTrue(critRes.body().contains("Work Quality"));

        // 6. Access Cycles
        HttpRequest cycleReq = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/admin/cycles"))
                .GET()
                .build();
        HttpResponse<String> cycleRes = client.send(cycleReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, cycleRes.statusCode());
        assertTrue(cycleRes.body().contains("Mid-Year Review 2026") || cycleRes.body().contains("Annual Performance Review"));

        // 7. Access Reports
        HttpRequest repReq = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/admin/reports"))
                .GET()
                .build();
        HttpResponse<String> repRes = client.send(repReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, repRes.statusCode());
        assertTrue(repRes.body().contains("Organization Performance Reports"));

        // 8. Access Reports Chart Data (JSON)
        HttpRequest chartReq = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/admin/reports/chart-data"))
                .GET()
                .build();
        HttpResponse<String> chartRes = client.send(chartReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, chartRes.statusCode());
        assertTrue(chartRes.body().contains("departmentReports"));

        // 9. Export CSV
        HttpRequest exportReq = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/admin/reports/export"))
                .GET()
                .build();
        HttpResponse<String> exportRes = client.send(exportReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, exportRes.statusCode());
        assertTrue(exportRes.body().contains("Evaluation ID") || exportRes.body().contains("Employee Name"));

        // 10. Logout
        HttpRequest logoutReq = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/logout"))
                .GET()
                .build();
        HttpResponse<String> logoutRes = client.send(logoutReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(302, logoutRes.statusCode());
    }

    @Test
    void testManagerFlow() throws IOException, InterruptedException {
        HttpClient client = createClient();

        // 1. Manager Login
        Map<String, String> form = new HashMap<>();
        form.put("username", "manager1");
        form.put("password", "Manager@123");

        HttpRequest loginReq = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/login"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(buildFormData(form)))
                .build();

        HttpResponse<String> loginRes = client.send(loginReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(302, loginRes.statusCode());
        assertTrue(loginRes.headers().firstValue("Location").orElse("").contains("/manager/dashboard"));

        // 2. Manager Dashboard
        HttpRequest dashReq = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/manager/dashboard"))
                .GET()
                .build();
        HttpResponse<String> dashRes = client.send(dashReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, dashRes.statusCode());
        assertTrue(dashRes.body().contains("Manager Dashboard") || dashRes.body().contains("Sarah Connor"));

        // 3. Manager Evaluations list
        HttpRequest evalReq = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/manager/evaluations"))
                .GET()
                .build();
        HttpResponse<String> evalRes = client.send(evalReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, evalRes.statusCode());
        assertTrue(evalRes.body().contains("Alice Johnson") || evalRes.body().contains("EMP-1001"));

        // 4. Logout
        HttpRequest logoutReq = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/logout"))
                .GET()
                .build();
        HttpResponse<String> logoutRes = client.send(logoutReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(302, logoutRes.statusCode());
    }

    @Test
    void testEmployeeFlow() throws IOException, InterruptedException {
        HttpClient client = createClient();

        // 1. Employee Login
        Map<String, String> form = new HashMap<>();
        form.put("username", "emp1");
        form.put("password", "Employee@123");

        HttpRequest loginReq = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/login"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(buildFormData(form)))
                .build();

        HttpResponse<String> loginRes = client.send(loginReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(302, loginRes.statusCode());
        assertTrue(loginRes.headers().firstValue("Location").orElse("").contains("/employee/dashboard"));

        // 2. Employee Dashboard
        HttpRequest dashReq = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/employee/dashboard"))
                .GET()
                .build();
        HttpResponse<String> dashRes = client.send(dashReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, dashRes.statusCode());
        assertTrue(dashRes.body().contains("Alice Johnson"));
        assertTrue(dashRes.body().contains("Outstanding"));

        // 3. Employee History
        HttpRequest histReq = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/employee/history"))
                .GET()
                .build();
        HttpResponse<String> histRes = client.send(histReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, histRes.statusCode());
        assertTrue(histRes.body().contains("Evaluation History") || histRes.body().contains("Annual Performance Review"));

        // 4. Logout
        HttpRequest logoutReq = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/logout"))
                .GET()
                .build();
        HttpResponse<String> logoutRes = client.send(logoutReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(302, logoutRes.statusCode());
    }

    @Test
    void testRoleAuthorizationEnforcement() throws IOException, InterruptedException {
        HttpClient client = createClient();

        // Login as employee
        Map<String, String> form = new HashMap<>();
        form.put("username", "emp1");
        form.put("password", "Employee@123");

        HttpRequest loginReq = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/login"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(buildFormData(form)))
                .build();
        client.send(loginReq, HttpResponse.BodyHandlers.ofString());

        // Attempt to access admin dashboard -> should receive 403 Forbidden
        HttpRequest adminReq = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/admin/dashboard"))
                .GET()
                .build();
        HttpResponse<String> adminRes = client.send(adminReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(403, adminRes.statusCode(), "Employee should be forbidden from accessing admin routes");

        // Attempt to access manager evaluations -> should receive 403 Forbidden
        HttpRequest mgrReq = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/manager/evaluations"))
                .GET()
                .build();
        HttpResponse<String> mgrRes = client.send(mgrReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(403, mgrRes.statusCode(), "Employee should be forbidden from accessing manager routes");
    }
}
