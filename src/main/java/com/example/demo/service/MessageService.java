package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
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

/**
 * Service class for managing messages.
 *
 * @author Andrey Sharipov
 * @version 1.0
 */
@Service
@Data
@Slf4j
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    /**
     * Saves a new message.
     *
     * @param request         the request containing the message information
     * @param authentication  the authentication object
     * @return the saved message
     * @throws IllegalArgumentException if the user is not a member of the chat
     * @throws NullPointerException     if the user is not authenticated
     */
    public Message save(MessageRequest request, Authentication authentication) {
        if (authentication.getPrincipal() != null) {
            User user = (User) authentication.getPrincipal();
            Chat chat = chatRepository.findById(request.getChatId())
                    .orElseThrow(() -> new IllegalStateException("Chat does not exist"));
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
        } else {
            throw new NullPointerException("User must be authenticated");
        }
    }
    /**
     * Retrieves a message by its ID.
     *
     * @param id the ID of the message
     * @return the retrieved message
     * @throws IllegalStateException if the message does not exist
     * @throws DataAccessException  if there is an error accessing data
     */
    public Message findById(String id) {
        try {
            return messageRepository.findById(id)
                    .orElseThrow(() -> new IllegalStateException("Message does not exist"));
        } catch (DataAccessException exception) {
            log.error(exception.getMessage(), exception);
            throw exception;
        }
    }
    /**
     * Updates an existing message.
     *
     * @param request         the request containing the updated message
     * @param authentication  the authentication object
     * @throws IllegalArgumentException if the user is not the author of the message
     * @throws NullPointerException     if the user is not authenticated
     */
    public void update(MessageRequest request, Authentication authentication) {
        if (authentication.getPrincipal() != null) {
            User user = (User) authentication.getPrincipal();
            Chat chat = chatRepository.findById(request.getChatId())
                    .orElseThrow(() -> new IllegalStateException("Chat does not exist"));
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
        } else {
            throw new NullPointerException("User must be authenticated");
        }
    }
    /**
     * Deletes a message.
     *
     * @param request         the request containing the message to delete
     * @param authentication  the authentication object
     * @throws IllegalArgumentException if the user is not the author of the message or does not have appropriate role
     * @throws NullPointerException     if the user is not authenticated
     */
    public void delete(MessageRequest request, Authentication authentication) {
        if (authentication.getPrincipal() != null) {
            User user = (User) authentication.getPrincipal();
            Chat chat = chatRepository.findById(request.getChatId())
                    .orElseThrow(() -> new IllegalStateException("Chat does not exist"));
            Message message = messageRepository.findById(request.getMessage().getId())
                    .orElseThrow(() -> new IllegalStateException("Message does not exist"));
            if (chat.getUserRole().stream()
                    .filter(filter -> filter.getUserId().equals(user.getId()))
                    .anyMatch(temp -> temp.getRole().equals(Role.ROLE_ADMIN) ||
                            temp.getRole().equals(Role.ROLE_MODERATOR)) &&
					message.getUserId().equals(user.getId())) {
                if (message.getChatId().equals(chat.getId())) {
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
        } else {
            throw new NullPointerException("User must be authenticated");
        }
    }
    /**
     * Copies non-null details from one message to another.
     *
     * @param existingMessage the existing message
     * @param newMessage      the new message with updated details
     */
    public void copyNotNullDetails(Message existingMessage, Message newMessage) {
        if (newMessage.getContext() != null && !newMessage.getContext().isEmpty()) {
            existingMessage.setContext(newMessage.getContext());
        }
	}

}
