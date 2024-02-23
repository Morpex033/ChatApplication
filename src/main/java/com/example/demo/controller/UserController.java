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

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;
	
	@PostMapping("/registration")
	public ResponseEntity<String> createUser(@RequestBody User user) {
		
		try {
			userService.save(user);
		}catch(IllegalStateException | DataAccessException exception) {
			return new ResponseEntity<>(exception.getMessage(), HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<User> getUser(@PathVariable("id") String id){
		User user = userService.findById(id);
		if(user == null) {
			
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<>(user, HttpStatus.OK);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteUser(@PathVariable("id") String id, Authentication auth){
		User user = userService.findById(id);
		if(user == null) {
			
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		if(auth.getAuthorities().contains(Role.ROLE_ADMIN)) {
		userService.delete(user);
		return new ResponseEntity<>(HttpStatus.OK);
		}
		
		return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<String> editUser(@PathVariable("id") String id, @RequestBody User user){
		try {
			userService.update(id, user);
		}catch(IllegalStateException | DataAccessException exception) {
			return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_MODIFIED);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
