package com.eps.dao;

import com.eps.model.Role;
import com.eps.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for User entities.
 */
public interface UserDao extends GenericDao<User, Integer> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findByRole(Role role);

    boolean updatePassword(Integer userId, String newPasswordHash);

    boolean toggleActiveStatus(Integer userId, boolean active);

    int countByRole(Role role);
}
