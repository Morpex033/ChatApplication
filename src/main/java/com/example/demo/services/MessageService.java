package com.example.demo.services;

import java.time.LocalDateTime;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.example.demo.models.Chat;
import com.example.demo.models.Message;
import com.example.demo.models.User;
import com.example.demo.models.role.Role;
import com.example.demo.repository.MessageRepository;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Service
@Data
@Slf4j
public class MessageService{
	private final MessageRepository repository;
	
	public void save(Chat chat, User user, Message message) {
		if(chat.getUsers().contains(user)) {
			message.setUser(user);
			message.setChat(chat);
			message.setTime(LocalDateTime.now());
			
			try {
				repository.save(message);
			}catch(DataAccessException exception) {
				log.error(exception.getMessage(), exception);
				throw exception;
			}
			
		}else {
	        throw new IllegalArgumentException("User is not a member of the chat");
	    }
	}

	public Message findById(String id) {
		return repository.findById(Long.parseLong(id)).orElse(null);
	}

	public void update(Message message, User user, Chat chat){
		if(message.getUser().equals(user) && 
				chat.getMessages().contains(message)) {
			try {
				repository.save(message);
		
			}catch(DataAccessException exception) {
				log.error(exception.getMessage(), exception);
				throw exception;
			}
		}else {
			throw new IllegalArgumentException("User is not a author of the message");
		}
	}

	public void delete(Message message, User user, Chat chat) {
		if(chat.getUserRole(user.getId()).equals(Role.ROLE_ADMIN) ||
				chat.getUserRole(user.getId()).equals(Role.ROLE_MODERATOR)) {
			try {
				repository.delete(message);
		
			}catch(DataAccessException exception) {
				log.error(exception.getMessage(), exception);
				throw exception;
			}
		}else {
			throw new IllegalArgumentException("User is not a author of the message");
		}
	}

}
