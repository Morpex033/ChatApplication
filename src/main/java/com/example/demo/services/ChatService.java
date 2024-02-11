package com.example.demo.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.models.Chat;
import com.example.demo.repository.ChatRepository;

import lombok.Data;

@Data
@Service
public class ChatService{
	private final ChatRepository repository;

	public Chat save(Chat chat) {
		return repository.save(chat);
	}

	public Optional<Chat> findById(Long id) {
		return repository.findById(id);
	}

	public Chat update(Chat chat) {
		return repository.save(chat);
	}

	public void delete(Chat chat) {
		repository.delete(chat);
	}

}
