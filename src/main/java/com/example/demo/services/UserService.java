package com.example.demo.services;

import java.util.NoSuchElementException;
import java.util.Optional;

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
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.getRoles().add(Role.ROLE_USER);
		return repository.save(user);
	}
	
	public User findById(String id) {
		Optional<User> user = repository.findById(Long.parseLong(id));
		try {
			return user.get();
		}catch(NoSuchElementException e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public User findByEmail(String email) {
		return repository.findByEmail(email);
	}

	public User update(String oldUserId, User updatedUser) {
		Optional<User> oldUser = repository.findById(Long.parseLong(oldUserId));
		if (oldUser.isPresent() && oldUser.get().getId().equals(updatedUser.getId())) {
			repository.save(updatedUser);
			
			return updatedUser;
		}
		
		return null;
	}


	public void delete(User user) {
		repository.delete(user);
	}
}
