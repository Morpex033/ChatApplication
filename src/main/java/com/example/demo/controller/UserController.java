package com.example.demo.controller;

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

import com.example.demo.models.User;
import com.example.demo.models.role.Role;
import com.example.demo.services.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
	private final UserService userService;

	@PostMapping("/registration")
	public ResponseEntity<String> createUser(@RequestBody User newUser) {
		User user;
		try {
			user = userService.save(newUser);
		} catch (IllegalStateException | DataAccessException exception) {
			log.error(exception.getMessage(), exception);
			return new ResponseEntity<>(exception.getMessage(), HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(user.getId().toString(), HttpStatus.CREATED);
	}

	@GetMapping("/{id}")
	public ResponseEntity<User> getUser(@PathVariable("id") String id) {
		User user;
		try {
			user = userService.findById(id);
		}
		catch (DataAccessException exception){
			log.error(exception.getMessage(), exception);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(user, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteUser(@PathVariable("id") String id, Authentication auth) {
		User user;
		try {
			user = userService.findById(id);
		}
		catch (DataAccessException exception){
			log.error(exception.getMessage(), exception);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		if (auth.getAuthorities().contains(Role.ROLE_ADMIN)) {
			userService.delete(user);
			return new ResponseEntity<>(HttpStatus.OK);
		}

		return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	}

	@PutMapping
	public ResponseEntity<String> editUser(@RequestBody User user, Authentication auth) {
		try {
			userService.update(user, auth);
		} catch (IllegalStateException | DataAccessException exception) {
			log.error(exception.getMessage(), exception);
			return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
