package com.example.demo.dto.response;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.demo.models.Chat;
import com.example.demo.models.Message;
import com.example.demo.models.User;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents a {@link Chat} response object.
 *
 * @author Andrey Sharipov
 * @version 1.0
 * @see Chat
 */
@Data
@AllArgsConstructor
public class ChatResponse {
	/**
	 * Private {@link UUID} field contains ID.
	 */
	private UUID id;
	/**
	 * Private name field contains name of the {@link Chat}.
	 *
	 * @see Chat
	 */
	private String name;
	/**
	 * Private {@link List} field contains list of {@link User} in {@link Chat}.
	 *
	 * @see User
	 * @see Chat
	 */
	private List<User> users = new ArrayList<>();
	/**
	 * Private {@link List} field contains list of {@link Message} in {@link Chat}.
	 *
	 * @see User
	 * @see Message
	 */
	private List<Message> messages = new ArrayList<>();
}
