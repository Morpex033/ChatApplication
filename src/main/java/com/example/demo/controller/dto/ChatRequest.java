package com.example.demo.controller.dto;

import com.example.demo.models.Chat;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChatRequest {
	
	private String userId;
	
	private Chat chat;

}
