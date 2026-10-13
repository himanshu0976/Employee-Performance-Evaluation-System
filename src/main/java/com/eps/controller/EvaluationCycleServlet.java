package com.eps.controller;

import com.eps.exception.AppException;
import com.eps.model.EvaluationCycle;
import com.eps.service.EvaluationCycleService;
import com.eps.service.impl.EvaluationCycleServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller for managing Performance Evaluation Cycles and their operational states.
 */
@WebServlet(name = "EvaluationCycleServlet", urlPatterns = {"/admin/cycles"})
public class EvaluationCycleServlet extends HttpServlet {

    private EvaluationCycleService cycleService;

    @Override
    public void init() {
        this.cycleService = new EvaluationCycleServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<EvaluationCycle> cycles = cycleService.getAllCycles();
        request.setAttribute("cycles", cycles);
        request.getRequestDispatcher("/WEB-INF/views/admin/cycles.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String action = request.getParameter("action");

        try {
            if ("add".equalsIgnoreCase(action)) {
                EvaluationCycle c = new EvaluationCycle();
                c.setTitle(request.getParameter("title"));
                c.setDescription(request.getParameter("description"));
                c.setStartDate(LocalDate.parse(request.getParameter("startDate")));
                c.setEndDate(LocalDate.parse(request.getParameter("endDate")));
                c.setStatus(EvaluationCycle.Status.valueOf(request.getParameter("status")));

                cycleService.createCycle(c);
                request.getSession().setAttribute("flashSuccess", "Evaluation Cycle '" + c.getTitle() + "' created successfully.");

            } else if ("edit".equalsIgnoreCase(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                EvaluationCycle c = cycleService.getCycleById(id);
                c.setTitle(request.getParameter("title"));
                c.setDescription(request.getParameter("description"));
                c.setStartDate(LocalDate.parse(request.getParameter("startDate")));
                c.setEndDate(LocalDate.parse(request.getParameter("endDate")));
                c.setStatus(EvaluationCycle.Status.valueOf(request.getParameter("status")));

                cycleService.updateCycle(c);
                request.getSession().setAttribute("flashSuccess", "Evaluation Cycle updated successfully.");

            } else if ("set-status".equalsIgnoreCase(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                EvaluationCycle.Status status = EvaluationCycle.Status.valueOf(request.getParameter("status"));
                cycleService.setCycleStatus(id, status);
                request.getSession().setAttribute("flashSuccess", "Cycle status updated to " + status + ".");

            } else if ("delete".equalsIgnoreCase(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                cycleService.deleteCycle(id);
                request.getSession().setAttribute("flashSuccess", "Evaluation Cycle deleted.");
            }
        } catch (AppException e) {
            request.getSession().setAttribute("flashError", e.getMessage());
        } catch (Exception e) {
            request.getSession().setAttribute("flashError", "Failed to process evaluation cycle: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/admin/cycles");
    }
}
