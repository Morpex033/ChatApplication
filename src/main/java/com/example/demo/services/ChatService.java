package com.example.demo.services;

import java.util.NoSuchElementException;
import java.util.Optional;

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

	public Chat save(Chat chat, User user) {
		if(repository.existsById(chat.getId())) {
			return null;
		}
		chat.setUserRole(user.getId(), Role.ROLE_ADMIN);
		chat.getUsers().add(user);
		repository.save(chat);
		
		return chat;
	}

	public Chat findById(String id) {
		Optional<Chat> chat = repository.findById(Long.parseLong(id));
		try {
			return chat.get();
		}catch(NoSuchElementException e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public Chat update(String id, Chat updatedChat, User user) {
		Optional<Chat> chat = repository.findById(Long.parseLong(id));
		if(chat.isPresent() && 
				(chat.get().getUserRole(user.getId()).equals(Role.ROLE_ADMIN) || 
						chat.get().getUserRole(user.getId()).equals(Role.ROLE_MODERATOR))) {
			repository.save(updatedChat);
			return updatedChat;
		}
		
		return null;
	}

	public Chat delete(Chat chat, User user) {
		if(chat.getUserRole(user.getId()).equals(Role.ROLE_ADMIN)) {
			repository.delete(chat);
			
			return chat;
		}
		
		return null;
	}

}
