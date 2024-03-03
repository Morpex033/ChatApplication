package com.example.demo.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.models.User;

/**
 * Repository interface for performing CRUD operations on User entities.
 *
 * <p>This interface extends Spring Data's {@link JpaRepository} interface,
 * specifying the entity type as {@link User} and the identifier type as {@link UUID}.</p>
 *
 * @author Andrey Sharipov
 * @version 1.0
 * @see JpaRepository
 * @see User
 */
public interface UserRepository extends JpaRepository<User, UUID>{
	/**
	 * Retrieves a user by their email address.
	 *
	 * @param email the email address of the user
	 * @return the user with the specified email address, or null if not found
	 */
	User findByEmail(String email);
	/**
	 * Checks if a user exists with the given email address.
	 *
	 * @param email the email address to check
	 * @return true if a user with the specified email address exists, false otherwise
	 */
	boolean existsByEmail(String email);
}
