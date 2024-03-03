package com.example.demo.models.role;

import org.springframework.security.core.GrantedAuthority;

/**
 * Enumeration representing roles in the application.
 *
 * <p>This enumeration implements the {@link GrantedAuthority} interface, which is used by Spring Security
 * to represent authorities granted to users.</p>
 *
 * @author Andrey Sharipov
 * @version 1.0
 * @see GrantedAuthority
 */
public enum Role implements GrantedAuthority{
	/**
	 * Represents a regular user role.
	 */
	ROLE_USER,
	/**
	 * Represents a moderator role.
	 */
	ROLE_MODERATOR,
	/**
	 * Represents an admin role.
	 */
	ROLE_ADMIN;
	/**
	 * Retrieves the authority of the role.
	 *
	 * @return A string representation of the role's authority.
	 */
	@Override
	public String getAuthority() {
		return name();
	}
}
