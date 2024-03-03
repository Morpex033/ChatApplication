package com.example.demo.dto.request;

import com.example.demo.models.Chat;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a request object used for {@link Chat} operations.
 *
 * @author Andrey Sharipov
 * @version 1.0
 * @see Chat
 */
@Setter
@Getter
public class ChatRequest {
	/**
	 * The {@link Chat} object containing relevant data for the chat operation.
	 *
	 * @see Chat
	 */
	private Chat chat;

}
