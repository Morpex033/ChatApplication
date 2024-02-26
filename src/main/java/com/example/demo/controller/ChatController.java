package com.example.demo.controller;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.request.ChatRequest;
import com.example.demo.dto.request.ChatRoleRequest;
import com.example.demo.dto.request.ChatUserRequest;
import com.example.demo.dto.response.ChatDto;
import com.example.demo.models.Chat;
import com.example.demo.services.ChatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {
	private final ChatService chatService;

	@GetMapping("/{id}")
	public ResponseEntity<ChatDto> getChat(@PathVariable("id") String id) {
		ChatDto chat;
		try {
			chat = chatService.findById(id);
		}catch(IllegalStateException exception) {
			log.error(exception.getMessage(), exception);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return ResponseEntity.ok(chat);
	}

	@PostMapping
	public ResponseEntity<String> createChat(@RequestBody ChatRequest request, Authentication auth) {
		Chat chat;
		try {
			chat = chatService.save(request, auth);
			chatService.setAdminUser(chat, auth);
		} catch (DataAccessException | IllegalStateException exception) {
			log.error(exception.getMessage(), exception);
			return new ResponseEntity<>(exception.getMessage(), HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(chat.getId().toString(), HttpStatus.CREATED);
	}

	@DeleteMapping
	public ResponseEntity<String> deleteChat(@RequestBody ChatRequest request, Authentication auth) {
		try {
			chatService.delete(request, auth);
		} catch (DataAccessException | IllegalStateException exception) {
			log.error(exception.getMessage(), exception);
			return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping("/edit")
	public ResponseEntity<String> updateChat(@RequestBody ChatRequest request, Authentication auth) {
		try {
			chatService.update(request, auth);
		} catch (DataAccessException | IllegalStateException exception) {
			log.error(exception.getMessage(), exception);
			return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping("/role")
	public ResponseEntity<String> addRoleToUser(@RequestBody ChatRoleRequest request, Authentication auth) {
		try {
			chatService.setRoleToUser(request, auth);
		} catch (DataAccessException | IllegalStateException exception) {
			log.error(exception.getMessage(), exception);
			return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping
	public ResponseEntity<String> addUser(@RequestBody ChatRoleRequest request, Authentication auth) {
		try {
			chatService.addUser(request, auth);
		} catch (DataAccessException | IllegalStateException exception) {
			log.error(exception.getMessage(), exception);
			return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@DeleteMapping("/user")
	public ResponseEntity<String> removeUser(@RequestBody ChatUserRequest request, Authentication auth){
		try {
			chatService.removeUser(request, auth);
		} catch (DataAccessException | IllegalStateException exception) {
			log.error(exception.getMessage(), exception);
			return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
