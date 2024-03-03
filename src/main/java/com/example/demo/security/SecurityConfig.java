package com.example.demo.security;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.demo.service.CustomUserDetailsService;
import com.example.demo.service.TokenCookieService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.jwk.OctetSequenceKey;

import lombok.RequiredArgsConstructor;
/**
 * Configuration class for security settings.
 *
 * @author Andrey Sharipov
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {
	private final CustomUserDetailsService userDetailsService;
	/**
	 * Bean definition for creating TokenCookieAuthenticationStrategy.
	 *
	 * @param cookieTokenKey the key used for encryption and decryption of tokens
	 * @return the configured TokenCookieAuthenticationStrategy
	 * @throws ParseException if there is an error parsing the cookie token key
	 * @throws JOSEException if there is an error in the JOSE (Javascript Object Signing and Encryption) process
	 * @see TokenCookieService
	 * @see DirectEncrypter
	 * @see DirectDecrypter
	 * @see OctetSequenceKey
	 */
	@Bean
	TokenCookieAuthenticationStrategy tokenCookieAuthenticationStrategy(
			@Value("${COOKIE_TOKEN_KEY}") String cookieTokenKey) throws ParseException, JOSEException {
		return new TokenCookieAuthenticationStrategy(
				new TokenCookieService(new DirectEncrypter(OctetSequenceKey.parse(cookieTokenKey)),
						new DirectDecrypter(OctetSequenceKey.parse(cookieTokenKey))));
	}
	/**
	 * Bean definition for creating TokenCookieAuthenticationConfigurer.
	 *
	 * @param cookieTokenKey the key used for encryption and decryption of tokens
	 * @return the configured TokenCookieAuthenticationConfigurer
	 * @throws ParseException if there is an error parsing the cookie token key
	 * @throws JOSEException if there is an error in the JOSE (Javascript Object Signing and Encryption) process
	 * @see TokenCookieService
	 * @see DirectEncrypter
	 * @see DirectDecrypter
	 * @see OctetSequenceKey
	 */
	@Bean
	TokenCookieAuthenticationConfigurer tokenCookieAuthenticationConfigurer(
			@Value("${COOKIE_TOKEN_KEY}") String cookieTokenKey) throws ParseException, JOSEException {
		return new TokenCookieAuthenticationConfigurer(
				new TokenCookieService(new DirectEncrypter(OctetSequenceKey.parse(cookieTokenKey)),
						new DirectDecrypter(OctetSequenceKey.parse(cookieTokenKey))));
	}
	/**
	 * Security filter chain definition.
	 *
	 * @param http the HttpSecurity object to configure
	 * @param tokenCookieAuthenticationStrategy the configured TokenCookieAuthenticationStrategy
	 * @param tokenCookieAuthenticationConfigurer the configured TokenCookieAuthenticationConfigurer
	 * @return the configured SecurityFilterChain
	 * @throws Exception if there is an error during the configuration process
	 * @see TokenCookieAuthenticationStrategy
	 * @see TokenCookieAuthenticationConfigurer
	 */
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http,
			TokenCookieAuthenticationStrategy tokenCookieAuthenticationStrategy,
			TokenCookieAuthenticationConfigurer tokenCookieAuthenticationConfigurer) throws Exception {
		http.httpBasic(Customizer.withDefaults()).with(tokenCookieAuthenticationConfigurer, Customizer.withDefaults())
				.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(authorize -> authorize.requestMatchers("/api/user/registration", "/error")
						.permitAll().anyRequest().authenticated())
				.sessionManagement(
						sessionManagment -> sessionManagment.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
								.sessionAuthenticationStrategy(tokenCookieAuthenticationStrategy))
				.logout(LogoutConfigurer::permitAll);

		return http.build();
	}
	/**
	 * Bean definition for creating AuthenticationManager.
	 *
	 * @param userDetailsService the userDetailsService to use for authentication
	 * @param passwordEncoder the passwordEncoder to use for authentication
	 * @return the configured AuthenticationManager
	 * @see DaoAuthenticationProvider
	 */
	@Bean
	AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder) {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(this.userDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder);

		return new ProviderManager(authenticationProvider);
	}
	/**
	 * Bean definition for creating PasswordEncoder.
	 *
	 * @return the configured PasswordEncoder
	 * @see BCryptPasswordEncoder
	 */
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(8);
	}
}
