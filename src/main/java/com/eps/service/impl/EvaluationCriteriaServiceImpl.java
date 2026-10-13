package com.eps.service.impl;

import com.eps.dao.EvaluationCriteriaDao;
import com.eps.dao.impl.EvaluationCriteriaDaoImpl;
import com.eps.exception.DuplicateResourceException;
import com.eps.exception.ResourceNotFoundException;
import com.eps.exception.ValidationException;
import com.eps.model.EvaluationCriteria;
import com.eps.service.EvaluationCriteriaService;
import com.eps.util.ValidationUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of EvaluationCriteriaService with business validations.
 */
public class EvaluationCriteriaServiceImpl implements EvaluationCriteriaService {

    private final EvaluationCriteriaDao criteriaDao;

    public EvaluationCriteriaServiceImpl() {
        this.criteriaDao = new EvaluationCriteriaDaoImpl();
    }

    public EvaluationCriteriaServiceImpl(EvaluationCriteriaDao criteriaDao) {
        this.criteriaDao = criteriaDao;
    }

    @Override
    public List<EvaluationCriteria> getAllCriteria() {
        return criteriaDao.findAll();
    }

    @Override
    public List<EvaluationCriteria> getActiveCriteria() {
        return criteriaDao.findAllActive();
    }

    @Override
    public EvaluationCriteria getCriteriaById(Integer id) {
        return criteriaDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluation criteria not found with ID: " + id));
    }

    @Override
    public EvaluationCriteria createCriteria(EvaluationCriteria criteria) {
        ValidationUtil.requireNonEmpty(criteria.getName(), "Criteria Name");
        if (criteria.getWeightage() == null || criteria.getWeightage().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Weightage must be greater than 0%.");
        }

        if (criteriaDao.findByName(criteria.getName()).isPresent()) {
            throw new DuplicateResourceException("Criteria with name '" + criteria.getName() + "' already exists.");
        }

        return criteriaDao.save(criteria);
    }

    @Override
    public void updateCriteria(EvaluationCriteria criteria) {
        if (criteria.getId() == null) {
            throw new ValidationException("Criteria ID is required for update.");
        }
        ValidationUtil.requireNonEmpty(criteria.getName(), "Criteria Name");
        if (criteria.getWeightage() == null || criteria.getWeightage().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Weightage must be greater than 0%.");
        }

        EvaluationCriteria existing = getCriteriaById(criteria.getId());

        Optional<EvaluationCriteria> withName = criteriaDao.findByName(criteria.getName());
        if (withName.isPresent() && !withName.get().getId().equals(criteria.getId())) {
            throw new DuplicateResourceException("Criteria name '" + criteria.getName() + "' is already in use.");
        }

        existing.setName(criteria.getName());
        existing.setDescription(criteria.getDescription());
        existing.setWeightage(criteria.getWeightage());
        existing.setActive(criteria.isActive());

        criteriaDao.update(existing);
    }

    @Override
    public void toggleStatus(Integer id, boolean active) {
        getCriteriaById(id);
        criteriaDao.toggleActiveStatus(id, active);
    }

    @Override
    public void deleteCriteria(Integer id) {
        getCriteriaById(id);
        criteriaDao.deleteById(id);
    }

    @Override
    public BigDecimal getTotalActiveWeightage() {
        return criteriaDao.getTotalActiveWeightage();
    }
}
