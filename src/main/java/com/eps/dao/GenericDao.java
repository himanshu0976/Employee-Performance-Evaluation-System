package com.eps.dao;

import java.util.List;
import java.util.Optional;

/**
 * Generic DAO interface demonstrating generics and collections in OOP design.
 *
 * @param <T>  The domain entity type
 * @param <ID> The entity primary key identifier type
 */
public interface GenericDao<T, ID> {

    /**
     * Finds an entity by its primary key.
     */
    Optional<T> findById(ID id);

    /**
     * Retrieves all entities.
     */
    List<T> findAll();

    /**
     * Persists a new entity.
     *
     * @param entity Entity to save
     * @return Saved entity with generated primary key if applicable
     */
    T save(T entity);

    /**
     * Updates an existing entity.
     *
     * @param entity Entity to update
     * @return true if updated, false otherwise
     */
    boolean update(T entity);

    /**
     * Deletes an entity by primary key.
     *
     * @param id Entity ID
     * @return true if deleted, false otherwise
     */
    boolean deleteById(ID id);
}
