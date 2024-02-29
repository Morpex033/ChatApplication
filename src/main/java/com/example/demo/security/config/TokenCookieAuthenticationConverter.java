package com.example.demo.security.config;

import java.util.stream.Stream;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import com.example.demo.models.Token;
import com.example.demo.service.TokenCookieService;
import com.nimbusds.jose.JOSEException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class TokenCookieAuthenticationConverter implements AuthenticationConverter {

	private TokenCookieService tokenCookieService;

	@Override
	public Authentication convert(HttpServletRequest request) {
		if (request.getCookies() != null) {
			return Stream.of(request.getCookies()).filter(cookie -> cookie.getName().equals("__Host-auth-token"))
					.findFirst().map(cookie -> {
						Token token = null;
						try {
							token = this.tokenCookieService.deserialize(cookie.getValue());
						} catch (JOSEException e) {
							log.error(e.getMessage(), e);
						}
						return new PreAuthenticatedAuthenticationToken(token, cookie.getValue());
					}).orElse(null);
		}

		return null;
	}

}
