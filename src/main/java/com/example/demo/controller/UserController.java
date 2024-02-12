package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.User;
import com.example.demo.services.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;
	
	@PostMapping("/registration")
	public ResponseEntity<String> createUser(@RequestBody User user) {
		if(userService.save(user) == null) {
			return new ResponseEntity<>("Email already taken", HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>("New user created", HttpStatus.OK);
	}
	
	@GetMapping("/Hello")
	public ResponseEntity<String> getHello(){
		return new ResponseEntity<>("Hello, " + SecurityContextHolder.getContext().getAuthentication().getName(),HttpStatus.OK);
	}
}
