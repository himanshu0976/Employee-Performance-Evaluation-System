package com.eps.service.impl;

import com.eps.dao.EvaluationCycleDao;
import com.eps.dao.impl.EvaluationCycleDaoImpl;
import com.eps.exception.ResourceNotFoundException;
import com.eps.exception.ValidationException;
import com.eps.model.EvaluationCycle;
import com.eps.service.EvaluationCycleService;
import com.eps.util.ValidationUtil;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of EvaluationCycleService with business validations.
 */
public class EvaluationCycleServiceImpl implements EvaluationCycleService {

    private final EvaluationCycleDao cycleDao;

    public EvaluationCycleServiceImpl() {
        this.cycleDao = new EvaluationCycleDaoImpl();
    }

    public EvaluationCycleServiceImpl(EvaluationCycleDao cycleDao) {
        this.cycleDao = cycleDao;
    }

    @Override
    public List<EvaluationCycle> getAllCycles() {
        return cycleDao.findAll();
    }

    @Override
    public EvaluationCycle getCycleById(Integer id) {
        return cycleDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluation cycle not found with ID: " + id));
    }

    @Override
    public Optional<EvaluationCycle> getActiveCycle() {
        return cycleDao.findActiveCycle();
    }

    @Override
    public EvaluationCycle createCycle(EvaluationCycle cycle) {
        ValidationUtil.requireNonEmpty(cycle.getTitle(), "Cycle Title");
        ValidationUtil.validateDateRange(cycle.getStartDate(), cycle.getEndDate(), "Cycle Dates");

        // If newly created cycle is set to ACTIVE, set any other ACTIVE cycle to COMPLETED
        if (cycle.getStatus() == EvaluationCycle.Status.ACTIVE) {
            cycleDao.findActiveCycle().ifPresent(active -> 
                cycleDao.updateStatus(active.getId(), EvaluationCycle.Status.COMPLETED)
            );
        }

        return cycleDao.save(cycle);
    }

    @Override
    public void updateCycle(EvaluationCycle cycle) {
        if (cycle.getId() == null) {
            throw new ValidationException("Cycle ID is required for update.");
        }
        ValidationUtil.requireNonEmpty(cycle.getTitle(), "Cycle Title");
        ValidationUtil.validateDateRange(cycle.getStartDate(), cycle.getEndDate(), "Cycle Dates");

        EvaluationCycle existing = getCycleById(cycle.getId());

        if (cycle.getStatus() == EvaluationCycle.Status.ACTIVE && existing.getStatus() != EvaluationCycle.Status.ACTIVE) {
            cycleDao.findActiveCycle().ifPresent(active -> {
                if (!active.getId().equals(existing.getId())) {
                    cycleDao.updateStatus(active.getId(), EvaluationCycle.Status.COMPLETED);
                }
            });
        }

        existing.setTitle(cycle.getTitle());
        existing.setDescription(cycle.getDescription());
        existing.setStartDate(cycle.getStartDate());
        existing.setEndDate(cycle.getEndDate());
        existing.setStatus(cycle.getStatus());

        cycleDao.update(existing);
    }

    @Override
    public void setCycleStatus(Integer cycleId, EvaluationCycle.Status status) {
        EvaluationCycle cycle = getCycleById(cycleId);
        if (status == EvaluationCycle.Status.ACTIVE) {
            cycleDao.findActiveCycle().ifPresent(active -> {
                if (!active.getId().equals(cycleId)) {
                    cycleDao.updateStatus(active.getId(), EvaluationCycle.Status.COMPLETED);
                }
            });
        }
        cycleDao.updateStatus(cycleId, status);
    }

    @Override
    public void deleteCycle(Integer cycleId) {
        getCycleById(cycleId);
        cycleDao.deleteById(cycleId);
    }
}
