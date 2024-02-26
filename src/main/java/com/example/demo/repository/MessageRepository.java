package com.example.demo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.demo.models.Message;

public interface MessageRepository extends MongoRepository<Message, String> {
	void delete(Message message);
	void deleteById(String id);
}
