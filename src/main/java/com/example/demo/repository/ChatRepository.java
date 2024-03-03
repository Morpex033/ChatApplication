package com.example.demo.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.models.Chat;

/**
 * Repository interface for performing CRUD operations on Chat entities.
 *
 * <p>This interface extends Spring Data's {@link JpaRepository} interface,
 * specifying the entity type as {@link Chat} and the identifier type as {@link UUID}.</p>
 *
 * @author Andrey Sharipov
 * @version 1.0
 * @see JpaRepository
 * @see Chat
 */
public interface ChatRepository extends JpaRepository<Chat, UUID>{
}
