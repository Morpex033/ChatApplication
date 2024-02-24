package com.example.demo.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.models.Chat;

public interface ChatRepository extends JpaRepository<Chat, UUID>{
}
