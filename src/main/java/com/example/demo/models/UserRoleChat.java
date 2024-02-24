package com.example.demo.models;

import java.util.UUID;

import com.example.demo.models.role.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class UserRoleChat {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	@Column
	private UUID userId;
	@JoinColumn(name = "chatId")
	@ManyToOne
	@JsonIgnore
	private Chat chat;
	@Column
	private Role role;
}
