package com.example.demo.models;

import java.time.Instant;
import java.util.List;
import java.util.UUID;


public record Token(UUID id, String subject, List<String> authoryties, Instant createdAt,
		Instant expiresAt) {

}
