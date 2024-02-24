package com.example.demo.services;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.example.demo.models.Chat;
import com.example.demo.models.Message;
import com.example.demo.models.User;
import com.example.demo.models.role.Role;
import com.example.demo.repository.ChatRepository;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.UserRepository;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Service
@Data
@Slf4j
public class MessageService{
	private final MessageRepository messageRepository;
	private final UserRepository userRepository;
	private final ChatRepository chatRepository;
	
	public Message save(String chatId, String userId, Message message) {
		Chat chat = chatRepository.findById(UUID.fromString(chatId)).orElse(null);
		User user = userRepository.findById(UUID.fromString(userId)).orElse(null);
		if(chat.getUsers().contains(user)) {
			message.setUser(user);
			message.setChat(chat);
			message.setTime(LocalDateTime.now());
			
			try {
				return messageRepository.save(message);
			}catch(DataAccessException exception) {
				log.error(exception.getMessage(), exception);
				throw exception;
			}
			
		}else {
	        throw new IllegalArgumentException("User is not a member of the chat");
	    }
	}

	public Message findById(String id) {
		return messageRepository.findById(Long.parseLong(id)).orElse(null);
	}

	public void update(Message message, String userId, String chatId){
		Chat chat = chatRepository.findById(UUID.fromString(chatId)).orElse(null);
		User user = userRepository.findById(UUID.fromString(userId)).orElse(null);
		if(message.getUser().equals(user) && 
				chat.getMessages().contains(message)) {
			try {
				messageRepository.save(message);
		
			}catch(DataAccessException exception) {
				log.error(exception.getMessage(), exception);
				throw exception;
			}
		}else {
			throw new IllegalArgumentException("User is not a author of the message");
		}
	}

	public void delete(Message message, String userId, String chatId) {
		Chat chat = chatRepository.findById(UUID.fromString(chatId)).orElse(null);
		if(chat.getUsers().stream().anyMatch(temp -> 
			temp.getId().equals(UUID.fromString(userId)) &&(
					temp.getAuthorities().contains(Role.ROLE_ADMIN) ||
					temp.getAuthorities().contains(Role.ROLE_MODERATOR)
				))) {
			try {
				messageRepository.delete(message);
		
			}catch(DataAccessException exception) {
				log.error(exception.getMessage(), exception);
				throw exception;
			}
		}else {
			throw new IllegalArgumentException("User is not a author of the message");
		}
	}

}
