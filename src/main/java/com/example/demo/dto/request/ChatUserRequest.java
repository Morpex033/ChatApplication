package com.example.demo.dto.request;

import java.util.UUID;

import com.example.demo.models.Chat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatUserRequest {
	private UUID userId;
	private Chat chat;
}
