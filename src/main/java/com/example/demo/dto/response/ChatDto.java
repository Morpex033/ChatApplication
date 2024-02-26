package com.example.demo.dto.response;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.demo.models.Message;
import com.example.demo.models.User;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatDto {
	private UUID id;
	private String name;
	private List<User> users = new ArrayList<>();
	private List<Message> messages = new ArrayList<>();
}
