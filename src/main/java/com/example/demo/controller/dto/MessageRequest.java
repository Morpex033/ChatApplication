package com.example.demo.controller.dto;

import com.example.demo.models.Message;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageRequest {
	private String userId;
	private String chatId;
	private Message message;
}
