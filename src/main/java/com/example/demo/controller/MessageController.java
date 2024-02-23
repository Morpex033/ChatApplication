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
import com.example.demo.models.Message;
import com.example.demo.models.User;
import com.example.demo.services.MessageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessageController {
	private final MessageService messageService;
	
	@PostMapping
	public ResponseEntity<String> createMessage(@RequestBody Message message,
			@RequestBody Chat chat,
			@RequestBody User user){
		try{
			messageService.save(chat, user, message);
		}catch(IllegalArgumentException | DataAccessException exception){
			return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
		}
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Message> getMessage(@PathVariable("id") String id){
		Message message = messageService.findById(id);
		
		if(message == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		return ResponseEntity.ok(message);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteChat(@PathVariable("id") String id,
			@RequestBody User user,
			@RequestBody Chat chat){
		Message message = messageService.findById(id);
		
		if(message == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		try{
			messageService.delete(message, user, chat);
		}catch(IllegalArgumentException | DataAccessException exception){
			return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
		}
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<String> updateChat(@PathVariable("id") String id,
			@RequestBody User user,
			@RequestBody Chat chat){
		Message message = messageService.findById(id);
		
		if(message == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		try{
			messageService.update(message, user, chat);
		}catch(IllegalArgumentException | DataAccessException exception) {
			return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
		}
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
