package com.example.demo.services;

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
	
	public void save(User user) {
		if(repository.existsByEmail(user.getEmail())) {
			log.error("Email alredy taken");
			throw new IllegalArgumentException();
		}
		user.setActive(true);
		
		if(user.getPassword() != null) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
		}else {
			log.error(new NullPointerException().getMessage(), new NullPointerException("Password is null, set to empty string"));
			user.setPassword("");
		}
		
		user.getRoles().add(Role.ROLE_USER);
		
		try {
			repository.save(user);
		}catch(DataAccessException exception) {
			log.error(exception.getMessage(), exception);
			throw exception;
		}
	}
	
	public User findById(String id) {
		return repository.findById(Long.parseLong(id)).orElse(null);
	}

	public User findByEmail(String email) {
		return repository.findByEmail(email);
	}

	public void update(String oldUserId, User updatedUser) {
		if (updatedUser.getId() == null || !updatedUser.getId().equals(Long.parseLong(oldUserId))) {
	        throw new IllegalArgumentException("Invalid user id for update");
	    }
		
		try {
			repository.save(updatedUser);
		}catch(DataAccessException exception) {
			log.error(exception.getMessage(), exception);
			throw exception;
		}
	}

	public void delete(User user) {
		repository.delete(user);
	}
}
