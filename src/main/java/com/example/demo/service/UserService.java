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

@Service
@Slf4j
@Data
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public User save(User user) {
        if (repository.existsByEmail(user.getEmail())) {
            log.error("Email alredy taken");
            throw new IllegalArgumentException();
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

    public User findById(String id) {
        try {
            return repository.findById(UUID.fromString(id))
                    .orElseThrow(() -> new IllegalStateException("User not found"));
        } catch (DataAccessException exception) {
            log.error(exception.getMessage(), exception);
            throw exception;
        }
    }

    public void update(User updatedUser, Authentication auth) {
        if(auth.getPrincipal() != null) {
            User user = (User) auth.getPrincipal();
            copyNotNullDetails(user, updatedUser);
            if (user == null) {
                throw new IllegalArgumentException("User not authenticated");
            }

            try {
                repository.save(user);
            } catch (DataAccessException exception) {
                log.error(exception.getMessage(), exception);
                throw exception;
            }
        }else{
            throw new NullPointerException("User must be authenticated");
        }

    }

    public void delete(User user) {
        try {
            repository.delete(user);
        } catch (DataAccessException exception) {
            log.error(exception.getMessage(), exception);
            throw exception;
        }
    }

    public void copyNotNullDetails(User existingUser, User updatedUser) {

        if (updatedUser.getUsername() != null && !updatedUser.getUsername().isEmpty()) {
            existingUser.setUsername(updatedUser.getUsername());
        }
        if (updatedUser.getEmail() != null && !updatedUser.getEmail().isEmpty()) {
            existingUser.setEmail(updatedUser.getEmail());
        }
    }
}
