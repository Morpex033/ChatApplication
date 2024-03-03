package com.example.demo.models;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
import lombok.Data;

/**
 * Represents a message entity in the application.
 *
 * <p>This class defines the structure of a message entity, including its unique identifier, content,
 * timestamp, associated user ID, and associated chat ID.</p>
 *
 * @author Andrey Sharipov
 * @version 1.0
 */
@Data
@Document(collection = "message")
public class Message {
	/**
	 * The unique identifier for the message.
	 */
	@Id
	private String id;
	/**
	 * The content of the message.
	 */
	private String context;
	/**
	 * The timestamp indicating when the message was sent.
	 */
	private LocalDateTime time;
	/**
	 * The unique identifier of the {@link User} who sent the message.
	 *
	 * @see User
	 */
	private UUID userId;
	/**
	 * The unique identifier of the {@link Chat} to which the message belongs.
	 *
	 * @see Chat
	 */
	private UUID chatId;
}
