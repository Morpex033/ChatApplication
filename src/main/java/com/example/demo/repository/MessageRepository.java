package com.example.demo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.demo.models.Message;

import java.util.UUID;

/**
 * Repository interface for performing CRUD operations on Message entities.
 *
 * <p>This interface extends Spring Data's {@link MongoRepository} interface,
 * specifying the entity type as {@link Message} and the identifier type as {@link String}.</p>
 *
 * @author Andrey Sharipov
 * @version 1.0
 * @see MongoRepository
 * @see Message
 */
public interface MessageRepository extends MongoRepository<Message, String> {
	/**
	 * Deletes all messages associated with a specific chat.
	 *
	 * @param chatId the unique identifier of the chat
	 */
	void deleteAllByChatId(UUID chatId);
}
