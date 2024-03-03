package com.example.demo.dto.response;

import java.util.Collection;

import com.example.demo.models.User;
import com.example.demo.models.role.Role;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a {@link User} response object.
 *
 * @author Andrey Sharipov
 * @version 1.0
 * @see User
 */
@Getter
@Setter
@AllArgsConstructor
public class UserResponse {
	/**
	 * Private username field.
	 */
	private String username;
	/**
	 * Private email field.
	 */
	@Email
	private String email;
	/**
	 * Private {@link Collection} filed contains {@link User} {@link Role}.
	 *
	 * @see User
	 * @see Role
	 */
	private Collection<Role> roles;
}
