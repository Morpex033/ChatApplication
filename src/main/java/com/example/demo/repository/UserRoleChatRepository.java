package com.example.demo.repository;

import java.util.UUID;

import com.example.demo.models.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.models.UserRoleChat;

/**
 * Repository interface for performing CRUD operations on UserRoleChat entities.
 *
 * <p>This interface extends Spring Data's {@link JpaRepository} interface,
 * specifying the entity type as {@link UserRoleChat} and the identifier type as {@link UUID}.</p>
 *
 * @author Andrey Sharipov
 * @version 1.0
 * @see JpaRepository
 * @see UserRoleChat
 */
public interface UserRoleChatRepository extends JpaRepository<UserRoleChat, UUID>{
    /**
     * Deletes all user role entries associated with a specific chat.
     *
     * @param chat the chat for which user roles should be deleted
     */
    void deleteAllByChat(Chat chat);
}
