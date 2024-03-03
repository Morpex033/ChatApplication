package com.example.demo.service;

import java.util.UUID;

import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.models.User;
import com.example.demo.models.role.Role;
import com.example.demo.repository.UserRepository;

import lombok.Data;
import lombok.extern.slf4j.*;

/**
 * Service class for managing users.
 * This class provides methods for saving, updating, finding, and deleting users in the database.
 * It also includes a method for copying non-null details from an updated user to an existing user.
 *
 * @author Andrey Sharipov
 * @version 1.0
 */
@Service
@Slf4j
@Data
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    /**
     * Saves a new user to the database.
     *
     * @param user the user to save
     * @return the saved user
     * @throws IllegalArgumentException if the email is already taken
     * @throws NullPointerException     if the password is null
     */
    public User save(User user) {
        if (repository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already taken");
        }

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            throw new NullPointerException("Password is null");
        }

        user.setActive(true);
        user.getRoles().add(Role.ROLE_ADMIN);

        try {
            return repository.save(user);
        } catch (DataAccessException exception) {
            log.error(exception.getMessage(), exception);
            throw exception;
        }
    }
    /**
     * Finds a user by ID.
     *
     * @param id the ID of the user to find
     * @return the found user
     * @throws IllegalStateException if the user is not found
     */
    public User findById(String id) {
        return repository.findById(UUID.fromString(id))
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }
    /**
     * Updates an existing user.
     *
     * @param updatedUser the updated user
     * @param auth        the authentication object
     * @throws NullPointerException if the user is not authenticated
     */
    public void update(User updatedUser, Authentication auth) {
        if (auth.getPrincipal() != null) {
            User user = (User) auth.getPrincipal();
            copyNotNullDetails(user, updatedUser);

            try {
                repository.save(user);
            } catch (DataAccessException exception) {
                log.error(exception.getMessage(), exception);
                throw exception;
            }
        } else {
            throw new NullPointerException("User must be authenticated");
        }

    }
    /**
     * Deletes a user from the database.
     *
     * @param user the user to delete
     */
    public void delete(User user) {
        try {
            repository.delete(user);
        } catch (DataAccessException exception) {
            log.error(exception.getMessage(), exception);
            throw exception;
        }
    }
    /**
     * Copies non-null details from the updated user to the existing user.
     *
     * @param existingUser the existing user
     * @param updatedUser  the updated user
     */
    public void copyNotNullDetails(User existingUser, User updatedUser) {

        if (updatedUser.getUsername() != null && !updatedUser.getUsername().isEmpty()) {
            existingUser.setUsername(updatedUser.getUsername());
        }
        if (updatedUser.getEmail() != null && !updatedUser.getEmail().isEmpty()) {
            existingUser.setEmail(updatedUser.getEmail());
        }
    }
}
