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

import com.example.demo.models.Chat;
import com.example.demo.models.User;
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
	
	@PostMapping("/create")
	public ResponseEntity<String> createChat(@RequestBody Chat chat, 
			@RequestBody User user){
		try{
			chatService.save(chat, user);
		}catch(DataAccessException | IllegalStateException exception){
			return new ResponseEntity<>(exception.getMessage(), HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteChat(@PathVariable("id") String id,
			@RequestBody User user){
		Chat chat = chatService.findById(id);
		if(chat == null) {
			
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		try{
			chatService.delete(chat, user);
		}catch(DataAccessException | IllegalStateException exception){
			return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
		}
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<String> updateChat(@PathVariable("id") String id,
			@RequestBody Chat chat,
			@RequestBody User user){
		try{
			chatService.update(id, chat, user);
		}catch(DataAccessException | IllegalStateException exception){
			return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
		}
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
}
