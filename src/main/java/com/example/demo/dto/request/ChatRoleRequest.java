package com.example.demo.dto.request;

import java.util.UUID;

import com.example.demo.models.Chat;
import com.example.demo.models.User;
import com.example.demo.models.role.Role;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a request object for assigning a {@link Role} to a {@link User} in a {@link Chat}.
 *
 * @author Andrey Sharipov
 * @version 1.0
 * @see Chat
 * @see User
 * @see Role
 */
@Setter
@Getter
public class ChatRoleRequest {
	/**
	 * Private {@link UUID} field containing the {@link User} id.
	 *
	 * @see User
	 */
	private UUID userId;
	/**
	 * Private {@link Role} field containing the user's role.
	 *
	 * @see Role
	 */
	private Role role;
	/**
	 * Private {@link Chat} which the {@link User} and his {@link Role} are assigned.
	 *
	 * @see Chat
	 * @see User
	 * @see Role
	 */
	private Chat chat;
}
