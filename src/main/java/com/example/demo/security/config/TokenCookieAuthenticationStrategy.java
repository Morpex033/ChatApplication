package com.example.demo.security.config;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import com.example.demo.services.TokenCookieService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TokenCookieAuthenticationStrategy implements SessionAuthenticationStrategy {

	private TokenCookieService tokenCookieService;

	@Override
	public void onAuthentication(Authentication authentication, HttpServletRequest request,
			HttpServletResponse response) throws SessionAuthenticationException {
		if (authentication instanceof UsernamePasswordAuthenticationToken) {
			var token = this.tokenCookieService.token(authentication);
			var tokenString = this.tokenCookieService.serealizer(token);

			var cookie = new Cookie("__Host-auth-token", tokenString);
			cookie.setPath("/");
			cookie.setDomain(null);
			cookie.setSecure(true);
			cookie.setHttpOnly(true);
			cookie.setMaxAge((int) ChronoUnit.SECONDS.between(Instant.now(), token.expiresAt()));

			response.addCookie(cookie);
		}
	}

}
