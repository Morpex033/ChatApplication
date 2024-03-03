package com.example.demo.controller;

import com.example.demo.models.Chat;
import com.example.demo.service.ChatService;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.response.ExceptionResponse;
import com.example.demo.dto.response.UUIDResponse;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.models.User;
import com.example.demo.models.role.Role;
import com.example.demo.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * This is {@link RestController}.
 * It processes related requests with {@link User}.
 * The {@code @RestController} process incoming HTTP requests and return data in JSON or XML format.
 * The {@code @RequestMapping("/api/user")} all controller methods will process requests along the path "/api/user".
 * The {@code @RequiredArgsConstructor} provided by the Lombok project to automatically generate a constructor
 * that takes arguments for all final fields of a class and automatically initializes those fields
 * when an instance of the class is created.
 * The {@code @Slf4j} provided by the Lombok project to automatically generate logging methods in a class.
 *
 * @author Andrey Sharipov
 * @version 1.0
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
	/**
	 * A private {@link UserService} field that is initialized by {@code @RequiredArgsConstructor}
	 *
	 * @see UserService
	 */
	private final UserService userService;

	/**
	 * Controller method that creates a {@link User}.
	 *
	 * @param newUser {@link User} .
	 * @return A {@link ResponseEntity} object containing {@link UUIDResponse}
	 * which contains the id of the created chat with status {@link HttpStatus#OK}.
	 * In other cases, {@link ResponseEntity} with {@link ExceptionResponse} in the response body,
	 * with status {@link HttpStatus#CONFLICT}.
	 * @throws DataAccessException if saving the {@link User} to the database failed.
	 * @throws NullPointerException if the {@link User} password is null or empty.
	 * @throws IllegalArgumentException if the {@link User} already exits in database.
	 * @see User
	 * @see ExceptionResponse
	 * @see UUIDResponse
	 */
	@PostMapping("/registration")
	public ResponseEntity<?> createUser(@RequestBody User newUser) {
		User user;
		try {
			user = userService.save(newUser);
		} catch (IllegalArgumentException | DataAccessException | NullPointerException exception) {
			log.error(exception.getMessage(), exception);
			return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(new UUIDResponse(user.getId()), HttpStatus.OK);
	}

	/**
	 * Controller method that sends the {@link User}.
	 *
	 * @param id The ID taken from the URL "/api/user/{id}", where {id} is a variable.
	 * @return A {@link ResponseEntity} containing the retrieved {@link UserResponse} object with status {@link HttpStatus#OK},
	 * if the {@link User} with the specified ID is found. Otherwise, an {@link ExceptionResponse}
	 * with a {@link HttpStatus#NOT_FOUND} status.
	 * @throws IllegalStateException if the {@link User} does not exist in database.
	 * @see User
	 * @see ExceptionResponse
	 * @see UserResponse
	 */
	@GetMapping("/{id}")
	public ResponseEntity<?> getUser(@PathVariable("id") String id) {
		User user;
		try {
			user = userService.findById(id);
		} catch (IllegalStateException exception) {
			log.error(exception.getMessage(), exception);
			return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(new UserResponse(user.getUsername(), user.getEmail(), user.getRoles()),
				HttpStatus.OK);
	}

	/**
	 * Controller method deleting a {@link User}.
	 *
	 * @param id The ID taken from the URL "/api/user/{id}", where {id} is a variable.
	 * @param auth An {@link Authentication} object containing {@link User} authentication information.
	 * @return If successful, {@link ResponseEntity} with status {@link HttpStatus#OK}.
	 * In case of an error {@link ResponseEntity} with {@link ExceptionResponse} in the response body and status {@link HttpStatus#NOT_FOUND}.
	 * If the {@link User} does not have enough rights {@link ResponseEntity} with the status {@link HttpStatus#FORBIDDEN}.
	 * @throws IllegalStateException if the {@link User} does not exist in database.
	 * @see User
	 * @see ExceptionResponse
	 * @see Role
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable("id") String id, Authentication auth) {
		User user;
		try {
			user = userService.findById(id);
		} catch (IllegalStateException exception) {
			log.error(exception.getMessage(), exception);
			return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), HttpStatus.NOT_FOUND);
		}
		if (auth.getAuthorities().contains(Role.ROLE_ADMIN)) {
			userService.delete(user);
			return new ResponseEntity<>(HttpStatus.OK);
		}

		return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	}

	/**
	 * Controller method for {@link User} update.
	 *
	 * @param user {@link User}.
	 * @param auth An {@link Authentication} object containing {@link User} authentication information.
	 * @return {@link ResponseEntity} with status {@link HttpStatus#OK}.
	 * In case of an error {@link ResponseEntity} with {@link ExceptionResponse} in the response body and status {@link HttpStatus#FORBIDDEN}.
	 * @throws DataAccessException if saving the {@link User} to the database failed.
	 * @throws NullPointerException if the {@link User} is not authenticated.
	 * @see User
	 * @see ExceptionResponse
	 */
	@PutMapping
	public ResponseEntity<?> editUser(@RequestBody User user, Authentication auth) {
		try {
			userService.update(user, auth);
		} catch (DataAccessException | NullPointerException exception) {
			log.error(exception.getMessage(), exception);
			return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), HttpStatus.FORBIDDEN);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
