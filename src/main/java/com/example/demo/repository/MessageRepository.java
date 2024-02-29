package com.example.demo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.demo.models.Message;

import java.util.UUID;

public interface MessageRepository extends MongoRepository<Message, String> {
	void deleteAllByChatId(UUID chatId);
}
