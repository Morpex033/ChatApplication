package com.example.demo.services;

import java.util.UUID;

import org.springframework.dao.DataAccessException;
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
public class UserService{
	private final UserRepository repository;
	private final PasswordEncoder passwordEncoder;
	
	public User save(User user) {
		if(repository.existsByEmail(user.getEmail())) {
			log.error("Email alredy taken");
			throw new IllegalArgumentException();
		}
		
		if(user.getPassword() != null) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
		}else {
			log.error(new NullPointerException().getMessage(), new NullPointerException("Password is null, set to empty string"));
			user.setPassword("");
		}
		
		user.setActive(true);
		user.getRoles().add(Role.ROLE_ADMIN);
		
		try {
			return repository.save(user);
		}catch(DataAccessException exception) {
			log.error(exception.getMessage(), exception);
			throw exception;
		}
	}
	
	public User findById(String id) {
		return repository.findById(UUID.fromString(id)).orElse(null);
	}

	public User findByEmail(String email) {
		return repository.findByEmail(email);
	}
	
	public void update(String oldUserId, User updatedUser) {
		User user = repository.findById(UUID.fromString(oldUserId)).orElse(null);
		copyNotNullDetails(user, updatedUser);
		if (user == null || updatedUser == null){
	        throw new IllegalArgumentException("Invalid user id for update");
	    }
		
		try {
			repository.save(user);
		}catch(DataAccessException exception) {
			log.error(exception.getMessage(), exception);
			throw exception;
		}
	}

	public void delete(User user) {
		repository.delete(user);
	}
	
	private User copyNotNullDetails(User existingUser, User updatedUser) {
		
		if (updatedUser.getUsername() != null && !updatedUser.getUsername().isEmpty()) {
	        existingUser.setUsername(updatedUser.getUsername());
	    }
	    if (updatedUser.getEmail() != null && !updatedUser.getEmail().isEmpty()) {
	        existingUser.setEmail(updatedUser.getEmail());
	    }
	    
	    return existingUser;
	}
}
