package com.example.demo.models;

import java.util.UUID;

import com.example.demo.models.role.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a user role within a {@link Chat} in the application.
 *
 * <p>This class defines the structure of a user role within a chat, including its unique identifier, user ID,
 * associated chat, and role.</p>
 *
 * @author Andrey Sharipov
 * @version 1.0
 * @see User
 * @see Role
 * @see Chat
 */
@Entity
@Data
@NoArgsConstructor
public class UserRoleChat {
	/**
	 * The unique identifier for the user role within a {@link Chat}.
	 *
	 * @see Chat
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	/**
	 * The unique identifier of the {@link User} associated with the {@link Role}.
	 *
	 * @see User
	 * @see Role
	 */
	@Column
	private UUID userId;
	/**
	 * The {@link Chat} associated with the user role.
	 *
	 * @see Chat
	 */
	@JoinColumn(name = "chatId")
	@ManyToOne
	@JsonIgnore
	private Chat chat;
	/**
	 * The role assigned to the {@link User} within the {@link Chat}.
	 *
	 * @see User
	 * @see Chat
	 */
	@Column
	private Role role;
}
