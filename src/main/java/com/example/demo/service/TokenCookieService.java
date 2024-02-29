package com.example.demo.service;

import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import lombok.Data;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.example.demo.models.Token;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Service
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class TokenCookieService {
	
	private Duration tokenDuration = Duration.ofDays(1);
	
	private final JWEEncrypter jweEncrypter;
	private final JWEDecrypter jweDecrypter;
	private JWEAlgorithm jweAlgorithm = JWEAlgorithm.DIR;
	private EncryptionMethod encryptionMethod = EncryptionMethod.A128CBC_HS256;

	public Token token(Authentication authentication) {
		var now = Instant.now();
		return new Token(UUID.randomUUID(), 
				authentication.getName(), 
				authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority).toList(),
				now, now.plus(tokenDuration));
	}

	public String serializer(Token token){
		var jweHeader = new JWEHeader.Builder(jweAlgorithm, encryptionMethod)
				.keyID(token.id().toString())
				.build();
		var claimsSet = new JWTClaimsSet.Builder()
				.jwtID(token.id().toString())
				.subject(token.subject())
				.issueTime(Date.from(token.createdAt()))
				.expirationTime(Date.from(token.expiresAt()))
				.claim("authorities", token.authoryties())
				.build();
		
		var encryptedJWT = new EncryptedJWT(jweHeader, claimsSet);
		try {
			encryptedJWT.encrypt(this.jweEncrypter);
			
			return encryptedJWT.serialize();
		}catch(JOSEException exception){
			log.error(exception.getMessage(), exception);
		}
		
		return null;
	}
	
	public Token deserialize(String string) throws JOSEException{
		try {
			var encryptedJWT = EncryptedJWT.parse(string);
			encryptedJWT.decrypt(this.jweDecrypter);
			var claimsSet = encryptedJWT.getJWTClaimsSet();
			return new Token(UUID.fromString(claimsSet.getJWTID()),
						claimsSet.getSubject(), 
						claimsSet.getStringListClaim("authorities"),
						claimsSet.getIssueTime().toInstant(),
						claimsSet.getExpirationTime().toInstant());
			
		}catch (ParseException | JOSEException exception) {
			log.error(exception.getMessage(), exception);
		}
		
		return null;
	}
}
