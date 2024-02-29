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

import com.example.demo.dto.request.MessageRequest;
import com.example.demo.dto.response.ExceptionResponse;
import com.example.demo.dto.response.StringIdResponse;
import com.example.demo.models.Message;
import com.example.demo.service.MessageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
@Slf4j
public class MessageController {
	private final MessageService messageService;

	@PostMapping
	public ResponseEntity<?> createMessage(@RequestBody MessageRequest request, Authentication authentication) {
		Message message;
		try {
			message = messageService.save(request, authentication);
		} catch (IllegalStateException | DataAccessException |
				 IllegalArgumentException | NullPointerException exception) {
			log.error(exception.getMessage(), exception);
			return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), HttpStatus.FORBIDDEN);
		}

		return new ResponseEntity<>(new StringIdResponse(message.getId()), HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getMessage(@PathVariable("id") String id) {
		Message message;
		try {
			message = messageService.findById(id);
		} catch (DataAccessException exception) {
			log.error(exception.getMessage(), exception);
			return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), HttpStatus.NOT_FOUND);
		}

		return ResponseEntity.ok(message);
	}

	@DeleteMapping
	public ResponseEntity<?> deleteMessage(@RequestBody MessageRequest request, Authentication authentication) {
		try {
			messageService.delete(request, authentication);
		} catch (IllegalStateException | DataAccessException |
				 IllegalArgumentException | NullPointerException exception) {
			log.error(exception.getMessage(), exception);
			return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), HttpStatus.FORBIDDEN);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping
	public ResponseEntity<?> updateMessage(@RequestBody MessageRequest request, Authentication authentication) {
		try {
			messageService.update(request, authentication);
		} catch (IllegalStateException | DataAccessException |
				 NullPointerException | IllegalArgumentException exception) {
			log.error(exception.getMessage(), exception);
			return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), HttpStatus.FORBIDDEN);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}
}
