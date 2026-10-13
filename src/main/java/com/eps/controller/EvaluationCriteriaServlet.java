package com.eps.controller;

import com.eps.exception.AppException;
import com.eps.model.EvaluationCriteria;
import com.eps.service.EvaluationCriteriaService;
import com.eps.service.impl.EvaluationCriteriaServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Controller for managing evaluation criteria and weight allocations.
 */
@WebServlet(name = "EvaluationCriteriaServlet", urlPatterns = {"/admin/criteria"})
public class EvaluationCriteriaServlet extends HttpServlet {

    private EvaluationCriteriaService criteriaService;

    @Override
    public void init() {
        this.criteriaService = new EvaluationCriteriaServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<EvaluationCriteria> criteriaList = criteriaService.getAllCriteria();
        BigDecimal totalWeightage = criteriaService.getTotalActiveWeightage();

        request.setAttribute("criteriaList", criteriaList);
        request.setAttribute("totalWeightage", totalWeightage);

        request.getRequestDispatcher("/WEB-INF/views/admin/criteria.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String action = request.getParameter("action");

        try {
            if ("add".equalsIgnoreCase(action)) {
                EvaluationCriteria c = new EvaluationCriteria();
                c.setName(request.getParameter("name"));
                c.setDescription(request.getParameter("description"));
                c.setWeightage(new BigDecimal(request.getParameter("weightage")));
                c.setActive("true".equalsIgnoreCase(request.getParameter("active")));

                criteriaService.createCriteria(c);
                request.getSession().setAttribute("flashSuccess", "Criteria '" + c.getName() + "' created successfully.");

            } else if ("edit".equalsIgnoreCase(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                EvaluationCriteria c = criteriaService.getCriteriaById(id);
                c.setName(request.getParameter("name"));
                c.setDescription(request.getParameter("description"));
                c.setWeightage(new BigDecimal(request.getParameter("weightage")));
                c.setActive("true".equalsIgnoreCase(request.getParameter("active")));

                criteriaService.updateCriteria(c);
                request.getSession().setAttribute("flashSuccess", "Criteria '" + c.getName() + "' updated successfully.");

            } else if ("toggle".equalsIgnoreCase(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                boolean active = "true".equalsIgnoreCase(request.getParameter("active"));
                criteriaService.toggleStatus(id, active);
                request.getSession().setAttribute("flashSuccess", "Criteria status updated.");

            } else if ("delete".equalsIgnoreCase(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                criteriaService.deleteCriteria(id);
                request.getSession().setAttribute("flashSuccess", "Criteria deleted.");
            }
        } catch (AppException e) {
            request.getSession().setAttribute("flashError", e.getMessage());
        } catch (Exception e) {
            request.getSession().setAttribute("flashError", "Failed to process criteria action: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/admin/criteria");
    }
}
