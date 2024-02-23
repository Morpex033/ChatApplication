package com.example.demo.models;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Document(collection = "message")
public class Message {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Long id;
	@Column
	private String context;
	@Column
	private LocalDateTime time;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "author")
	private User user;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	private Chat chat;
}
