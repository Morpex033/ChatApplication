package com.example.demo.services;

import java.util.UUID;

import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.demo.dto.request.ChatRequest;
import com.example.demo.dto.request.ChatRoleRequest;
import com.example.demo.dto.request.ChatUserRequest;
import com.example.demo.dto.response.ChatDto;
import com.example.demo.models.Chat;
import com.example.demo.models.User;
import com.example.demo.models.UserRoleChat;
import com.example.demo.models.role.Role;
import com.example.demo.repository.ChatRepository;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.UserRoleChatRepository;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Service
public class ChatService {
	private final ChatRepository chatRepository;
	private final UserRepository userRepository;
	private final MessageRepository messageRepository;
	private final UserRoleChatRepository userRoleChatRepository;

	public Chat save(ChatRequest request, Authentication auth) {
		User user = (User) auth.getPrincipal();
		Chat chat = request.getChat();
		if (userRepository.existsById(user.getId())) {
			chat.getUsers().add(user);
			try {
				return chatRepository.save(chat);
			} catch (DataAccessException exception) {
				log.error(exception.getMessage(), exception);
				throw exception;
			}
		} else {
			throw new IllegalStateException("User not exists");
		}
	}

	public ChatDto findById(String id) {
		Chat chat = chatRepository.findById(UUID.fromString(id))
				.orElseThrow(() -> new IllegalStateException("Chat does not exist"));
		
		return new ChatDto(chat.getId(), chat.getName(), chat.getUsers(), messageRepository.findAll().stream()
				.filter(message -> message.getChatId().equals(chat.getId())).toList());
	}

	public void update(ChatRequest request, Authentication auth) {
		Chat chat = chatRepository.findById(request.getChat().getId())
				.orElseThrow(() -> new IllegalStateException("Chat does not exist"));
		User user = (User) auth.getPrincipal();

		copyNotNullDetails(chat, request.getChat());
		if (chat.getUserRole().stream().filter(userId -> userId.getUserId().equals(user.getId())).anyMatch(
				role -> role.getRole().equals(Role.ROLE_ADMIN) || role.getRole().equals(Role.ROLE_MODERATOR))) {
			try {
				chatRepository.save(chat);
			} catch (DataAccessException exception) {
				log.error(exception.getMessage(), exception);
				throw exception;
			}
		} else {
			throw new IllegalStateException("User mus be admin or moderator role");
		}
	}

	public void delete(ChatRequest request, Authentication auth) {
		Chat chat = chatRepository.findById(request.getChat().getId()).orElse(null);
		User user = (User) auth.getPrincipal();
		if (chat.getUserRole().stream().filter(userId -> userId.getUserId().equals(user.getId()))
				.anyMatch(role -> role.getRole().equals(Role.ROLE_ADMIN))) {
			try {
				userRoleChatRepository.deleteAll(chat.getUserRole());
				chatRepository.delete(chat);
			} catch (DataAccessException exception) {
				log.error(exception.getMessage(), exception);
				throw exception;
			}
		} else {
			throw new IllegalStateException("User must be admin role");
		}
	}

	public void setAdminUser(Chat chat, Authentication auth) {
		User user = (User) auth.getPrincipal();
		UserRoleChat userRole = new UserRoleChat();
		userRole.setUserId(user.getId());
		userRole.setChat(chat);
		userRole.setRole(Role.ROLE_ADMIN);

		chat.getUserRole().add(userRole);
		try {
			chatRepository.save(chat);
		} catch (DataAccessException exception) {
			log.error(exception.getMessage(), exception);
			throw exception;
		}
	}

	private Chat copyNotNullDetails(Chat existingChat, Chat updatedChat) {

		if (updatedChat.getName() != null && !updatedChat.getName().isEmpty()) {
			existingChat.setName(updatedChat.getName());
		}

		return existingChat;
	}

	public void setRoleToUser(ChatRoleRequest request, Authentication auth) {
		User user = (User) auth.getPrincipal();
		Chat chat = chatRepository.findById(request.getChat().getId())
				.orElseThrow(() -> new IllegalStateException("Chat does not exist"));
		if (chat.getUserRole().parallelStream().filter(userId -> userId.getUserId().equals(user.getId()))
				.anyMatch(role -> role.getRole().equals(Role.ROLE_ADMIN))) {
			UserRoleChat userRole = new UserRoleChat();
			userRole.setUserId(request.getUserId());
			userRole.setChat(chat);
			userRole.setRole(request.getRole());

			chat.getUserRole().add(userRole);

			try {
				chatRepository.save(chat);
			} catch (DataAccessException exception) {
				log.error(exception.getMessage(), exception);
				throw exception;
			}
		}
	}

	public void addUser(ChatRoleRequest request, Authentication auth) {
		User user = (User) auth.getPrincipal();
		Chat chat = chatRepository.findById(request.getChat().getId())
				.orElseThrow(() -> new IllegalStateException("Chat does not exist"));

		if (chat.getUsers().contains(user)) {
			UserRoleChat userRole = new UserRoleChat();
			userRole.setChat(chat);
			userRole.setUserId(request.getUserId());
			userRole.setRole(Role.ROLE_USER);

			chat.getUsers().add(user);
			chat.getUserRole().add(userRole);

			try {
				chatRepository.save(chat);
			} catch (DataAccessException exception) {
				log.error(exception.getMessage(), exception);
				throw exception;
			}
		} else {
			throw new IllegalStateException("User does not have access to this chat");
		}
	}

	public void removeUser(ChatUserRequest request, Authentication auth) {
		User user = (User) auth.getPrincipal();
		Chat chat = chatRepository.findById(request.getChat().getId())
				.orElseThrow(() -> new IllegalStateException("Chat does not exist"));
		if (chat.getUserRole().parallelStream().filter(id -> id.getUserId().equals(user.getId()))
				.anyMatch(role -> role.getRole().equals(Role.ROLE_ADMIN))) {
			chat.getUsers().remove(userRepository.findById(request.getUserId())
					.orElseThrow(() -> new IllegalStateException("User does not exist in this chat")));

			chat.getUserRole()
					.remove(chat.getUserRole().stream().filter(filter -> filter.getUserId().equals(request.getUserId()))
							.findFirst()
							.orElseThrow(() -> new IllegalStateException("User does not exist in this chat")));

			try {
				chatRepository.save(chat);
			} catch (DataAccessException exception) {
				log.error(exception.getMessage(), exception);
				throw exception;
			}
		}
	}
}
