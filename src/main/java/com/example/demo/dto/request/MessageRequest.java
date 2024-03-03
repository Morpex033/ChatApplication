package com.example.demo.dto.request;

import com.example.demo.models.Chat;
import com.example.demo.models.Message;

import com.example.demo.models.User;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Represents a request object for adding {@link Message} to the {@link Chat} by id.
 *
 * @author Andrey Sharipov
 * @version 1.0
 * @see Message
 * @see Chat
 */
@Getter
@Setter
public class MessageRequest {
	/**
	 * Private {@link UUID} field containing the {@link Chat} id.
	 *
	 * @see Chat
	 */
	private UUID chatId;
	/**
	 * Private {@link Message} field.
	 *
	 * @see Message
	 */
	private Message message;
}
