package com.example.demo.dto.request;

import java.util.UUID;

import com.example.demo.models.Chat;

import com.example.demo.models.User;
import com.example.demo.models.role.Role;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a request object for adding a {@link User} by id to the {@link Chat}.
 *
 * @author Andrey Sharipov
 * @version 1.0
 * @see User
 * @see Chat
 */
@Getter
@Setter
public class ChatUserRequest {
	/**
	 * Private {@link UUID} field containing the {@link User} id.
	 *
	 * @see User
	 */
	private UUID userId;
	/**
	 * Private {@link Chat} field in which the {@link User} will be added.
	 *
	 * @see Chat
	 * @see User
	 * @see Role
	 */
	private Chat chat;
}
