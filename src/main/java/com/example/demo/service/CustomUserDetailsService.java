package com.example.demo.service;

import java.time.Instant;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;

import com.example.demo.models.Token;
import com.example.demo.models.TokenUser;
import com.example.demo.repository.UserRepository;

import lombok.Data;

/**
 * Service class for managing custom user details.
 *
 * @author Andrey Sharipov
 * @version 1.0
 */
@Service
@Data
public class CustomUserDetailsService
		implements UserDetailsService, AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {
	private final UserRepository userRepository;
	/**
	 * Loads user details by username.
	 *
	 * @param email the email address of the user
	 * @return the UserDetails object representing the user
	 * @throws UsernameNotFoundException if the user is not found
	 */
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		return userRepository.findByEmail(email);
	}
	/**
	 * Loads user details by authentication token.
	 *
	 * @param authenticationToken the authentication token
	 * @return the UserDetails object representing the user
	 * @throws IllegalArgumentException if the principal is not of type Token
	 */
	@Override
	public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken authenticationToken)
			throws IllegalArgumentException {
		if (authenticationToken.getPrincipal() instanceof Token token) {
			return new TokenUser(token.subject(), "nopassword", true, true, token.expiresAt().isAfter(Instant.now()),
					true, token.authoryties().stream().map(SimpleGrantedAuthority::new).toList(), token);
		}

		throw new IllegalArgumentException("Principal must be of type Token");
	}
}
