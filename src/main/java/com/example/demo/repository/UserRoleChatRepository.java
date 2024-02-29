package com.example.demo.repository;

import java.util.UUID;

import com.example.demo.models.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.models.UserRoleChat;

public interface UserRoleChatRepository extends JpaRepository<UserRoleChat, UUID>{
    void deleteAllByChat(Chat chat);
}
