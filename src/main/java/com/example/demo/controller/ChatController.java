package com.example.demo.controller;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.controller.dto.ChatRequest;
import com.example.demo.models.Chat;
import com.example.demo.services.ChatService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
	private final ChatService chatService;
	
	@GetMapping("/{id}")
	public ResponseEntity<Chat> getChat(@PathVariable("id") String id) {
		Chat chat = chatService.findById(id);
		if (chat == null) {
			
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return ResponseEntity.ok(chat);
	}
	
	@PostMapping
	public ResponseEntity<String> createChat(@RequestBody ChatRequest request) {
		Chat chat;
		try{
			chat = chatService.save(request.getChat(), request.getUserId());
			chatService.setAdminUser(chat, request.getUserId());
		}catch(DataAccessException | IllegalStateException exception){
			return new ResponseEntity<>(exception.getMessage(), HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(chat.getId().toString(),HttpStatus.CREATED);
	}
	
	@DeleteMapping
	public ResponseEntity<String> deleteChat(@RequestBody ChatRequest request) {
		try{
			chatService.delete(request.getChat(), request.getUserId());
		}catch(DataAccessException | IllegalStateException exception){
			return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
		}
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PutMapping
	public ResponseEntity<String> updateChat(@RequestBody ChatRequest request){
		try{
			chatService.update(request.getChat(), request.getUserId());
		}catch(DataAccessException | IllegalStateException exception){
			return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
		}
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
}
