package com.example.demo.services;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.models.Chat;
import com.example.demo.models.Message;
import com.example.demo.models.User;
import com.example.demo.models.role.Role;
import com.example.demo.repository.MessageRepository;

import lombok.Data;

@Service
@Data
public class MessageService{
	private final MessageRepository repository;
	
	public Message save(Chat chat, User user, Message message) {
		if(chat.getUsers().contains(user)) {
			message.setUser(user);
			message.setChat(chat);
			message.setTime(LocalDateTime.now());
			
			repository.save(message);
			
			return message;
		}
		
		return null;
	}

	public Message findById(String id) {
		Optional<Message> message = repository.findById(Long.parseLong(id));
		if(message.isPresent()) {
			return message.get();
		}
		
		return null;
	}

	public Message update(Message message, User user, Chat chat){
		if(message.getUser().equals(user) && 
				chat.getMessages().contains(message)) {
			repository.delete(message);
			return message;
		}
		
		return null;
	}

	public Message delete(Message message, User user, Chat chat) {
		if(chat.getUserRole(user.getId()).equals(Role.ROLE_ADMIN) ||
				chat.getUserRole(user.getId()).equals(Role.ROLE_MODERATOR)) {
			repository.delete(message);
			
			return message;
		}
		
		return null;
	}

}
