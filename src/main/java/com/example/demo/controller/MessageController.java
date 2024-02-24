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

import com.example.demo.controller.dto.MessageRequest;
import com.example.demo.models.Message;
import com.example.demo.services.MessageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessageController {
	private final MessageService messageService;
	
	@PostMapping
	public ResponseEntity<String> createMessage(@RequestBody MessageRequest request){
		Message message;
		try{
			message = messageService.save(request.getChatId(), request.getUserId(), request.getMessage());
		}catch(IllegalArgumentException | DataAccessException exception){
			return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
		}
		
		return new ResponseEntity<>(message.getId().toString(), HttpStatus.OK);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Message> getMessage(@PathVariable("id") String id){
		Message message = messageService.findById(id);
		
		if(message == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		return ResponseEntity.ok(message);
	}
	
	@DeleteMapping
	public ResponseEntity<String> deleteChat(@RequestBody MessageRequest request){
		try{
			messageService.delete(request.getMessage(), request.getUserId(), request.getChatId());
		}catch(IllegalArgumentException | DataAccessException exception){
			return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
		}
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PutMapping
	public ResponseEntity<String> updateChat(@RequestBody MessageRequest request){
		try{
			messageService.update(request.getMessage(), request.getUserId(), request.getChatId());
		}catch(IllegalArgumentException | DataAccessException exception) {
			return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
		}
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
