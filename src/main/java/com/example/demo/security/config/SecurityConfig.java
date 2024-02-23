package com.example.demo.security.config;

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

import com.example.demo.services.CustomUserDetailsService;
import com.example.demo.services.TokenCookieService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.jwk.OctetSequenceKey;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {
	private final CustomUserDetailsService userDetailsService;
	
	@Bean
	TokenCookieAuthenticationStrategy tokenCookieAuthenticationStrategy(
			@Value("${COOKIE_TOKEN_KEY}") String cookieTokenKey
			) throws ParseException, JOSEException {
		return new TokenCookieAuthenticationStrategy(
				new TokenCookieService(
						new DirectEncrypter(OctetSequenceKey.parse(cookieTokenKey)),
						new DirectDecrypter(OctetSequenceKey.parse(cookieTokenKey))));
	}
	
	@Bean
	TokenCookieAuthenticationConfigurer tokenCookieAuthenticationConfigurer(
			@Value("${COOKIE_TOKEN_KEY}") String cookieTokenKey
			) throws ParseException, JOSEException {
		return new TokenCookieAuthenticationConfigurer(
				new TokenCookieService(
						new DirectEncrypter(OctetSequenceKey.parse(cookieTokenKey)),
						new DirectDecrypter(OctetSequenceKey.parse(cookieTokenKey))));
	}
	
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http,
    		TokenCookieAuthenticationStrategy tokenCookieAuthenticationStrategy,
    		TokenCookieAuthenticationConfigurer tokenCookieAuthenticationConfigurer) throws Exception {
        http
        .httpBasic(Customizer.withDefaults())
        .with(tokenCookieAuthenticationConfigurer, Customizer.withDefaults())
        .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorize -> authorize
            	.requestMatchers("/api/user/registration", "/error").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(sessionManagment -> sessionManagment
            		.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            		.sessionAuthenticationStrategy(tokenCookieAuthenticationStrategy))
            .logout(LogoutConfigurer::permitAll);
        
        return http.build();
    }
    
    @Bean
	AuthenticationManager authenticationManager(
			UserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder) {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(this.userDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder);

		return new ProviderManager(authenticationProvider);
	}
    
    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(8);
    }

}
