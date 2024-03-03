package com.example.demo.models;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;

import lombok.Data;

/**
 * Represents a chat entity in the application.
 *
 * <p>This class defines the structure of a chat entity, including its unique identifier, name,
 * list of user roles associated with the chat, and the list of users participating in the chat.</p>
 *
 * @author Andrey Sharipov
 * @version 1.0
 */
@Data
@Entity
@Table
public class Chat {
	/**
	 * The unique identifier for the chat.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column
	private UUID id;
	/**
	 * The name of the chat.
	 */
	@Column
	private String name;
	/**
	 * The list of {@link UserRoleChat} associated with the chat.
	 *
	 * @see UserRoleChat
	 */
	@OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<UserRoleChat> userRole = new ArrayList<>();
	/**
	 * The list of {@link User} participating in the chat.
	 *
	 * @see User
	 */
	@ManyToMany
	@JoinTable(name = "chat_user", joinColumns = @JoinColumn(name = "chat_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
	private List<User> users = new ArrayList<>();

}
