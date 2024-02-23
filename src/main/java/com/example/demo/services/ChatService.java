package com.example.demo.services;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.example.demo.models.Chat;
import com.example.demo.models.User;
import com.example.demo.models.role.Role;
import com.example.demo.repository.ChatRepository;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Service
public class ChatService{
	private final ChatRepository repository;

	public void save(Chat chat, User user) {
		if(repository.existsById(chat.getId())) {
			throw new IllegalStateException("User not exists");
		}
		chat.setUserRole(user.getId(), Role.ROLE_ADMIN);
		chat.getUsers().add(user);
		try {
			repository.save(chat);
		}catch(DataAccessException exception) {
			log.error(exception.getMessage(), exception);
			throw exception;
		}
	}

	public Chat findById(String id) {
		return repository.findById(Long.parseLong(id)).orElse(null);
	}

	public void update(String id, Chat updatedChat, User user) {
		Chat chat = repository.findById(Long.parseLong(id)).orElse(null);
		if(chat != null) { 
			if((chat.getUserRole(user.getId()).equals(Role.ROLE_ADMIN) || 
					chat.getUserRole(user.getId()).equals(Role.ROLE_MODERATOR))) {
				try {
					repository.save(updatedChat);
				}catch(DataAccessException exception) {
					log.error(exception.getMessage(), exception);
					throw exception;
				}
			}else {
				throw new IllegalStateException("User mus be admin or moderator role");
			}
		}else {
			throw new IllegalStateException("Chat does not exist");
		}
	}

	public void delete(Chat chat, User user) {
		if(chat.getUserRole(user.getId()).equals(Role.ROLE_ADMIN)) {
			try {
				repository.delete(chat);
			}catch(DataAccessException exception) {
				log.error(exception.getMessage(), exception);
				throw exception;
			}
		}else {
			throw new IllegalStateException("User must be admin role");
		}
	}

}
