package com.example.demo.dto.request;

import java.util.UUID;

import com.example.demo.models.Chat;
import com.example.demo.models.role.Role;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChatRoleRequest {
	private UUID userId;
	private Role role;
	private Chat chat;
}
