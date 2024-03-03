package com.example.demo.models;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Represents a token entity in the application.
 *
 * <p>This record defines the structure of a token entity, including its unique identifier, subject,
 * list of authorities, creation timestamp, and expiration timestamp.</p>
 *
 * @author Andrey Sharipov
 * @version 1.0
 */
public record Token(UUID id, String subject, List<String> authoryties, Instant createdAt, Instant expiresAt) {
}
