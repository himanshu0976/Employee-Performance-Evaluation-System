package com.eps.filter;

import com.eps.model.Role;
import com.eps.model.User;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Authentication and Role-Based Authorization Filter.
 * Enforces session verification and URL access control per user role.
 */
@WebFilter("/*")
public class AuthFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(AuthFilter.class);

    @Override
    public void init(FilterConfig filterConfig) {
        logger.info("AuthFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String contextPath = request.getContextPath();
        String uri = request.getRequestURI().substring(contextPath.length());

        // Allow public assets and login/logout endpoints
        if (isPublicResource(uri)) {
            chain.doFilter(req, res);
            return;
        }

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        // If not logged in, redirect to login page
        if (currentUser == null) {
            if (!"/login".equals(uri) && !"/".equals(uri)) {
                if (session == null) {
                    session = request.getSession(true);
                }
                session.setAttribute("redirectUrl", request.getRequestURI());
            }
            response.sendRedirect(contextPath + "/login");
            return;
        }

        // Role-Based Authorization enforcement
        Role role = currentUser.getRole();

        if (uri.startsWith("/admin") && role != Role.ADMIN) {
            logger.warn("Unauthorized access attempt to {} by user '{}' with role {}", uri, currentUser.getUsername(), role);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Administrator role required.");
            return;
        }

        if (uri.startsWith("/manager") && role != Role.MANAGER && role != Role.ADMIN) {
            // Note: Admin can optionally view, but strictly manager routes expect evaluator context
            logger.warn("Unauthorized access attempt to {} by user '{}' with role {}", uri, currentUser.getUsername(), role);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Manager role required.");
            return;
        }

        if (uri.startsWith("/employee") && role != Role.EMPLOYEE && role != Role.ADMIN) {
            logger.warn("Unauthorized access attempt to {} by user '{}' with role {}", uri, currentUser.getUsername(), role);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Employee role required.");
            return;
        }

        // Root path redirection based on role
        if ("/".equals(uri) || "/home".equals(uri)) {
            response.sendRedirect(contextPath + currentUser.getDashboardUrl());
            return;
        }

        // Authorized: continue filter chain
        chain.doFilter(req, res);
    }

    private boolean isPublicResource(String uri) {
        return uri.startsWith("/assets/") ||
               uri.equals("/login") ||
               uri.equals("/logout") ||
               uri.equals("/favicon.ico");
    }

    @Override
    public void destroy() {
    }
}
