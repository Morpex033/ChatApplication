package com.example.demo.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.AuthenticationEntryPointFailureHandler;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.csrf.CsrfFilter;

import com.example.demo.repository.UserRepository;
import com.example.demo.service.CustomUserDetailsService;
import com.example.demo.service.TokenCookieService;

import lombok.RequiredArgsConstructor;

/**
 * Configuration class for token cookie authentication.
 * Configures the necessary components for token cookie authentication.
 *
 * @author Andrey Sharipov
 * @version 1.0
 */
@RequiredArgsConstructor
public class TokenCookieAuthenticationConfigurer
		extends AbstractHttpConfigurer<TokenCookieAuthenticationConfigurer, HttpSecurity> {

	private final TokenCookieService tokenCookieService;

	private UserRepository userRepository;

	@Override
	public void init(HttpSecurity builder) throws Exception {
		// Configure logout handler
		builder.logout(logout -> logout.addLogoutHandler(new CookieClearingLogoutHandler("__Host-auth-token")));
	}

	@Override
	public void configure(HttpSecurity builder) throws Exception {
		// Create and configure authentication filter
		var cookieAuthenticationFilter = new AuthenticationFilter(builder.getSharedObject(AuthenticationManager.class),
				new TokenCookieAuthenticationConverter(this.tokenCookieService));
		cookieAuthenticationFilter.setSuccessHandler((request, response, authentication) -> {
		});
		cookieAuthenticationFilter
				.setFailureHandler(new AuthenticationEntryPointFailureHandler(new Http403ForbiddenEntryPoint()));
		// Create and configure authentication provider
		var authenticationProvider = new PreAuthenticatedAuthenticationProvider();
		authenticationProvider.setPreAuthenticatedUserDetailsService(new CustomUserDetailsService(this.userRepository));
		// Add authentication filter and provider
		builder.addFilterAfter(cookieAuthenticationFilter, CsrfFilter.class)
				.authenticationProvider(authenticationProvider);
	}

}
