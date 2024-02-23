package com.example.demo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.demo.models.Message;

public interface MessageRepository extends MongoRepository<Message, Long> {
}
