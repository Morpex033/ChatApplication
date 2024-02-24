package com.example.demo.services;

import java.util.UUID;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.example.demo.models.Chat;
import com.example.demo.models.UserRoleChat;
import com.example.demo.models.role.Role;
import com.example.demo.repository.ChatRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.UserRoleChatRepository;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Service
public class ChatService{
	private final ChatRepository chatRepository;
	private final UserRepository userRepository;
	private final UserRoleChatRepository userRoleChatRepository;

	public Chat save(Chat chat, String userId) {
		if(userRepository.existsById(UUID.fromString(userId))) {
			chat.getUsers().add(userRepository.findById(UUID.fromString(userId)).orElse(null));
			try {
				return chatRepository.save(chat);
			}catch(DataAccessException exception) {
				log.error(exception.getMessage(), exception);
				throw exception;
			}
		}else {
			throw new IllegalStateException("User not exists");
		}
	}

	public Chat findById(String id) {
		return chatRepository.findById(UUID.fromString(id)).orElse(null);
	}

	public void update(Chat chat, String userId) {
		if(this.chatRepository.existsById(chat.getId())) { 
			if(chat.getUserRole().stream()
					.filter(user -> user.getUserId().equals(UUID.fromString(userId)))
					.anyMatch(role -> role.getRole().equals(Role.ROLE_ADMIN) || role.getRole().equals(Role.ROLE_MODERATOR))) {
				try {
					chatRepository.save(chat);
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

	public void delete(Chat deletedChat, String userId) {
		Chat chat = chatRepository.findById(deletedChat.getId()).orElse(null);
		if(chat.getUserRole().stream()
				.filter(user -> user.getUserId().equals(UUID.fromString(userId)))
				.anyMatch(role -> role.getRole().equals(Role.ROLE_ADMIN))) {
			try {
				chatRepository.delete(chat);
			}catch(DataAccessException exception) {
				log.error(exception.getMessage(), exception);
				throw exception;
			}
		}else {
			throw new IllegalStateException("User must be admin role");
		}
	}
	
	public void setAdminUser(Chat chat, String userId) {
		UserRoleChat userRole = new UserRoleChat();
		userRole.setUserId(UUID.fromString(userId));
		userRole.setChat(chat);
		userRole.setRole(Role.ROLE_ADMIN);
		
		chat.getUserRole().add(userRole);
		try {
		chatRepository.save(chat);
		}catch(DataAccessException exception) {
			log.error(exception.getMessage(), exception);
			throw exception;
		}
	}
		
}
