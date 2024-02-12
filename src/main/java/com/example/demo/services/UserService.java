package com.example.demo.services;

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
			return null;
		}
		user.setActive(true);
		//user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.getRoles().add(Role.ROLE_USER);
		return repository.save(user);
	}

	public User findByEmail(String email) {
		return repository.findByEmail(email);
	}

	public User update(User user) {
		return repository.save(user);
	}


	public void delete(User user) {
		repository.delete(user);
	}
}
