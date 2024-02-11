package com.example.demo.services;

import java.util.Optional;

import org.springframework.stereotype.Service;
import com.example.demo.models.Message;
import com.example.demo.repository.MessageRepository;

import lombok.Data;

@Service
@Data
public class MessageService{
	private final MessageRepository repository;
	
	public Message save(Message message) {
		return repository.save(message);
	}

	public Optional<Message> findById(Long id) {
		return repository.findById(id);
	}

	public Message update(Message message) {
		return repository.save(message);
	}

	public void delete(Message message) {
		repository.delete(message);
	}

}
