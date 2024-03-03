package com.example.demo.models;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a user entity with associated authentication token.
 *
 * <p>This class extends Spring Security's {@link User} class to include an additional field for storing the authentication token.</p>
 *
 * @author Andrey Sharipov
 * @version 1.0
 * @see User
 */
@Setter
@Getter
public class TokenUser extends User {
	/**
	 * The authentication token associated with the user.
	 *
	 * @see Token
	 */
	private final Token token;
	/**
	 * Constructs a new {@code TokenUser} with the specified username, password, authorities, and token.
	 *
	 * @param username    the username
	 * @param password    the password
	 * @param authorities the authorities granted to the user
	 * @param token       the authentication token associated with the user
	 */
	public TokenUser(String username, String password, Collection<? extends GrantedAuthority> authorities,
			Token token) {
		super(username, password, authorities);
		this.token = token;
	}
	/**
	 * Constructs a new {@code TokenUser} with the specified username, password, enabled status, account expiration status,
	 * credentials expiration status, account locking status, authorities, and token.
	 *
	 * @param username               the username
	 * @param password               the password
	 * @param enabled                whether the user account is enabled
	 * @param accountNonExpired      whether the user account is non-expired
	 * @param credentialsNonExpired  whether the user credentials are non-expired
	 * @param accountNonLocked       whether the user account is non-locked
	 * @param authorities            the authorities granted to the user
	 * @param token                  the authentication token associated with the user
	 */
	public TokenUser(String username, String password, boolean enabled, boolean accountNonExpired,
			boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities,
			Token token) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
		this.token = token;
	}

}