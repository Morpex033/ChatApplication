package com.example.demo.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.models.User;
import com.example.demo.models.role.Role;
import com.example.demo.repository.UserRepository;

import lombok.Data;

@Service
@Data
public class UserService{
	private final UserRepository repository;
	private final PasswordEncoder passwordEncoder;
	
	public User save(User user) {
		user.setActive(false);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
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
