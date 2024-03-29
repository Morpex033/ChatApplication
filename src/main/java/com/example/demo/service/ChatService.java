package com.example.demo.service;

import java.util.UUID;

import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.demo.dto.request.ChatRequest;
import com.example.demo.dto.request.ChatRoleRequest;
import com.example.demo.dto.request.ChatUserRequest;
import com.example.demo.dto.response.ChatResponse;
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

/**
 * Service class for managing chat-related operations.
 *
 * @author Andrey Sharipov
 * @version 1.0
 */
@Data
@Slf4j
@Service
public class ChatService {
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final UserRoleChatRepository userRoleChatRepository;
    /**
     * Saves a new chat.
     *
     * @param request   the ChatRequest object containing chat data
     * @param auth      the Authentication object representing the authenticated user
     * @return          the saved Chat object
     */
    public Chat save(ChatRequest request, Authentication auth) {
        if (auth.getPrincipal() != null) {
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
        } else {
            throw new NullPointerException("User must be authenticated");
        }
    }
    /**
     * Retrieves a chat by its ID.
     *
     * @param id    the ID of the chat to retrieve
     * @return      the ChatResponse object containing chat details
     */
    public ChatResponse findById(String id) {
        Chat chat = chatRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new IllegalStateException("Chat does not exist"));

        return new ChatResponse(chat.getId(), chat.getName(), chat.getUsers(), messageRepository.findAll().stream()
                .filter(message -> message.getChatId().equals(chat.getId())).toList());
    }
    /**
     * Updates an existing chat.
     *
     * @param request   the ChatRequest object containing updated chat data
     * @param auth      the Authentication object representing the authenticated user
     */
    public void update(ChatRequest request, Authentication auth) {
        if (auth.getPrincipal() != null) {
            User user = (User) auth.getPrincipal();
            Chat chat = chatRepository.findById(request.getChat().getId())
                    .orElseThrow(() -> new IllegalStateException("Chat does not exist"));

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
        } else {
            throw new NullPointerException("User must be authenticated");
        }
    }
    /**
     * Deletes a chat.
     *
     * @param request   the ChatRequest object containing the chat to delete
     * @param auth      the Authentication object representing the authenticated user
     */
    public void delete(ChatRequest request, Authentication auth) {
        if (auth.getPrincipal() != null) {
            User user = (User) auth.getPrincipal();
            Chat chat = chatRepository.findById(request.getChat().getId())
                    .orElseThrow(() -> new IllegalStateException("Chat not found"));
            if (chat.getUserRole().stream().filter(userId -> userId.getUserId().equals(user.getId()))
                    .anyMatch(role -> role.getRole().equals(Role.ROLE_ADMIN))) {
                try {
                    messageRepository.deleteAllByChatId(chat.getId());
                    userRoleChatRepository.deleteAllByChat(chat);
                    chatRepository.delete(chat);
                } catch (DataAccessException exception) {
                    log.error(exception.getMessage(), exception);
                    throw exception;
                }
            } else {
                throw new IllegalStateException("User must be admin role");
            }
        } else {
            throw new NullPointerException("User must be authenticated");
        }
    }
    /**
     * Sets the admin role to a user in a chat.
     *
     * @param chat  the Chat object to set the admin role in
     * @param auth  the Authentication object representing the authenticated user
     */
    public void setAdminUser(Chat chat, Authentication auth) {
        if (auth.getPrincipal() != null) {
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
        } else {
            throw new NullPointerException("User must be authenticated");
        }
    }
    /**
     * Copies non-null details from an updated chat to an existing chat.
     *
     * @param existingChat  the existing Chat object
     * @param updatedChat   the updated Chat object
     */
    public void copyNotNullDetails(Chat existingChat, Chat updatedChat) {
        if (updatedChat.getName() != null && !updatedChat.getName().isEmpty()) {
            existingChat.setName(updatedChat.getName());
        }
    }
    /**
     * Sets a role to a user in a chat.
     *
     * @param request   the ChatRoleRequest object containing the chat ID, user ID, and role to set
     * @param auth      the Authentication object representing the authenticated user
     */
    public void setRoleToUser(ChatRoleRequest request, Authentication auth) {
        if (auth.getPrincipal() != null) {
            User user = (User) auth.getPrincipal();
            Chat chat = chatRepository.findById(request.getChat().getId())
                    .orElseThrow(() -> new IllegalStateException("Chat does not exist"));
            if (chat.getUserRole().parallelStream().filter(userId -> userId.getUserId().equals(user.getId()))
                    .anyMatch(role -> role.getRole().equals(Role.ROLE_ADMIN))) {
                UserRoleChat userRole = chat.getUserRole().stream()
                        .filter(filter -> filter.getUserId().equals(request.getUserId())).findFirst()
                        .orElseThrow(() -> new IllegalStateException("User does not exist in this chat"));

                userRole.setRole(request.getRole());

                chat.getUserRole().add(userRole);

                try {
                    chatRepository.save(chat);
                } catch (DataAccessException exception) {
                    log.error(exception.getMessage(), exception);
                    throw exception;
                }
            }
        } else {
            throw new NullPointerException("User must be authenticated");
        }
    }
    /**
     * Adds a user to a chat.
     *
     * @param request   the ChatUserRequest object containing the chat ID and user ID
     * @param auth      the Authentication object representing the authenticated user
     */
    public void addUser(ChatUserRequest request, Authentication auth) {
        if (auth.getPrincipal() != null) {
            User user = (User) auth.getPrincipal();
            Chat chat = chatRepository.findById(request.getChat().getId())
                    .orElseThrow(() -> new IllegalStateException("Chat does not exist"));

            if (chat.getUsers().stream().anyMatch(filter -> filter.getId().equals(user.getId()))) {
                UserRoleChat userRole = new UserRoleChat();
                userRole.setChat(chat);
                userRole.setUserId(request.getUserId());
                userRole.setRole(Role.ROLE_USER);

                chat.getUsers().add(userRepository.findById(request.getUserId())
                        .orElseThrow(() -> new IllegalStateException("User does not exist")));
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
        } else {
            throw new NullPointerException("User must be authenticated");
        }
    }
    /**
     * Removes a user from a chat.
     *
     * @param request   the ChatUserRequest object containing the chat ID and user ID
     * @param auth      the Authentication object representing the authenticated user
     */
    public void removeUser(ChatUserRequest request, Authentication auth) {
        if (auth.getPrincipal() != null) {
            User user = (User) auth.getPrincipal();
            Chat chat = chatRepository.findById(request.getChat().getId())
                    .orElseThrow(() -> new IllegalStateException("Chat does not exist"));
            if (chat.getUserRole().parallelStream().filter(id -> id.getUserId().equals(user.getId()))
                    .anyMatch(role -> role.getRole().equals(Role.ROLE_ADMIN))) {
                chat.getUsers().remove(userRepository.findById(request.getUserId())
                        .orElseThrow(() -> new IllegalStateException("User does not exist in this chat")));

                UserRoleChat userRole = chat.getUserRole().stream()
                        .filter(filter -> filter.getUserId().equals(request.getUserId())).findFirst()
                        .orElseThrow(() -> new IllegalStateException("User does not exist in this chat"));
                chat.getUserRole().remove(userRole);

                try {
                    chatRepository.save(chat);
                    userRoleChatRepository.delete(userRole);
                } catch (DataAccessException exception) {
                    log.error(exception.getMessage(), exception);
                    throw exception;
                }
            }
        } else {
            throw new NullPointerException("User must be authenticated");
        }
    }
}
