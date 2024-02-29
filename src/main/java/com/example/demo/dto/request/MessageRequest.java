package com.example.demo.dto.request;

import com.example.demo.models.Message;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageRequest {
	private String chatId;
	private Message message;
}
