package com.example.demo.models;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
import lombok.Data;

@Data
@Document(collection = "message")
public class Message {
	@Id
	private String id;
	private String context;
	private LocalDateTime time;
	private UUID userId;
	private UUID chatId;
}
