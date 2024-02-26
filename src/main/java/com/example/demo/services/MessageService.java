package com.example.demo.services;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.example.demo.dto.request.MessageRequest;
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
public class MessageService {
	private final MessageRepository messageRepository;
	private final UserRepository userRepository;
	private final ChatRepository chatRepository;

	public Message save(MessageRequest request) {
		Chat chat = chatRepository.findById(UUID.fromString(request.getChatId()))
				.orElseThrow(() -> new IllegalStateException("Chat does not exist"));
		User user = userRepository.findById(UUID.fromString(request.getUserId()))
				.orElseThrow(() -> new IllegalStateException("User does not exist"));
		Message message = request.getMessage();
		if (chat.getUsers().contains(user)) {
			message.setUserId(user.getId());
			message.setChatId(chat.getId());
			message.setTime(LocalDateTime.now());

			try {
				userRepository.save(user);
				chatRepository.save(chat);
				return messageRepository.save(message);
			} catch (DataAccessException exception) {
				log.error(exception.getMessage(), exception);
				throw exception;
			}

		} else {
			throw new IllegalArgumentException("User is not a member of the chat");
		}
	}

	public Message findById(String id) {
		try {
			return messageRepository.findById(id)
					.orElseThrow(() -> new IllegalStateException("Message does not exist"));
		} catch (DataAccessException exception) {
			log.error(exception.getMessage(), exception);
			throw exception;
		}
	}

	public void update(MessageRequest request) {
		Chat chat = chatRepository.findById(UUID.fromString(request.getChatId()))
				.orElseThrow(() -> new IllegalStateException("Chat does not exist"));
		User user = userRepository.findById(UUID.fromString(request.getUserId()))
				.orElseThrow(() -> new IllegalStateException("User does not exist"));
		Message message = messageRepository.findById(request.getMessage().getId())
				.orElseThrow(() -> new IllegalStateException("Message does not exist"));
		if (message.getUserId().equals(user.getId()) && message.getChatId().equals(chat.getId())) {
			copyNotNullDetails(message, request.getMessage());
			try {
				messageRepository.save(message);

			} catch (DataAccessException exception) {
				log.error(exception.getMessage(), exception);
				throw exception;
			}
		} else {
			throw new IllegalArgumentException("User is not a author of the message");
		}
	}

	public void delete(MessageRequest request) {
		Chat chat = chatRepository.findById(UUID.fromString(request.getChatId()))
				.orElseThrow(() -> new IllegalStateException("Chat does not exist"));
		Message message = messageRepository.findById(request.getMessage().getId())
				.orElseThrow(() -> new IllegalStateException("Message does not exist"));
		if (chat.getUsers().stream()
				.anyMatch(temp -> temp.getId().equals(UUID.fromString(request.getUserId()))
						&& (temp.getAuthorities().contains(Role.ROLE_ADMIN)
								|| temp.getAuthorities().contains(Role.ROLE_MODERATOR)))) {
			if (messageRepository.findById(message.getId()).orElse(null).getChatId().equals(chat.getId())) {
				try {
					messageRepository.delete(message);

				} catch (DataAccessException exception) {
					log.error(exception.getMessage(), exception);
					throw exception;
				}
			}
		} else {
			throw new IllegalArgumentException("User is not a author of the message");
		}
	}

	public Message copyNotNullDetails(Message existingMessage, Message newMessage) {
		if (newMessage.getContext() != null && !newMessage.getContext().isEmpty()) {
			existingMessage.setContext(newMessage.getContext());
		}
		existingMessage.setTime(LocalDateTime.now());

		return existingMessage;
	}

}
