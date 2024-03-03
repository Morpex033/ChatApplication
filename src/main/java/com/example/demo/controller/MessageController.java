package com.example.demo.controller;

import com.example.demo.models.Chat;
import com.example.demo.models.User;
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

/**
 * This is {@link RestController}.
 * It processes related requests with {@link Message}.
 * The {@code @RestController} process incoming HTTP requests and return data in JSON or XML format.
 * The {@code @RequestMapping("/api/message")} all controller methods will process requests along the path "/api/message".
 * The {@code @RequiredArgsConstructor} provided by the Lombok project to automatically generate a constructor
 * that takes arguments for all final fields of a class and automatically initializes those fields
 * when an instance of the class is created.
 * The {@code @Slf4j} provided by the Lombok project to automatically generate logging methods in a class.
 *
 * @author Andrey Sharipov
 * @version 1.0
 */
@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
@Slf4j
public class MessageController {
    /**
     * A private {@link MessageService} field that is initialized by {@code @RequiredArgsConstructor}
     *
     * @see MessageService
     */
    private final MessageService messageService;

    /**
     * Controller method that creates a {@link Message} in {@link Chat}
     *
     * @param request {@link MessageRequest}.
     * @param authentication An {@link Authentication} object containing {@link User} authentication information.
     * @return {@link ResponseEntity} with {@link StringIdResponse} in the response body
     * with status {@link HttpStatus#OK}.
     * In case of an error {@link ResponseEntity} with {@link ExceptionResponse} in the response body
     * with status {@link HttpStatus#FORBIDDEN}.
     * @throws IllegalStateException if the {@link Chat} does not exist in the database.
     * @throws IllegalArgumentException if the {@link User} does not exist in the {@link Chat}.
     * @throws DataAccessException if saving the {@link Chat}, {@link User}, {@link Message} to the database failed.
     * @throws NullPointerException if the {@link User} is not authenticated.
     * @see MessageRequest
     * @see Message
     * @see ExceptionResponse
     * @see StringIdResponse
     */
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

    /**
     * Controller method to receive a {@link Message} object.
     *
     * @param id The ID taken from the URL "/api/message/{id}", where {id} is a variable.
     * @return A {@link ResponseEntity} containing the retrieved {@link Message} response object with status {@link HttpStatus#OK},
     * if the {@link Message} with the specified ID is found. Otherwise, an {@link ExceptionResponse}
     * with a {@link HttpStatus#NOT_FOUND} status.
     * @throws DataAccessException if saving the {@link Message} to the database failed.
     * @throws IllegalStateException if the {@link Message} does not exist in the database.
     * @see Message
     * @see ExceptionResponse
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getMessage(@PathVariable("id") String id) {
        Message message;
        try {
            message = messageService.findById(id);
        } catch (DataAccessException | IllegalStateException exception) {
            log.error(exception.getMessage(), exception);
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(message);
    }

    /**
     * Controller method to delete a {@link Message} from a {@link Chat}.
     *
     * @param request {@link MessageRequest}.
     * @param authentication An {@link Authentication} object containing {@link User} authentication information.
     * @return {@link ResponseEntity} with {@link HttpStatus#OK}.
     * In other cases, {@link ResponseEntity} with {@link ExceptionResponse} in the response body,
     * whit status {@link HttpStatus#FORBIDDEN}.
     * @throws IllegalStateException if the {@link Chat} does not exist in the database.
     * @throws IllegalArgumentException if the {@link User} is not the author of the message
     * @throws DataAccessException if saving the {@link User}, {@link User}, {@link Message} to the database failed.
     * @throws NullPointerException if the {@link User} is not authenticated.
     * @see MessageRequest
     * @see ExceptionResponse
     */
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

    /**
     * Controller method for updating {@link Message}.
     *
     * @param request {@link MessageRequest}.
     * @param authentication An {@link Authentication} object containing {@link User} authentication information.
     * @return {@link ResponseEntity} with {@link HttpStatus#OK}.
     * In other cases, {@link ResponseEntity} with {@link ExceptionResponse} in the response body,
     * whit status {@link HttpStatus#FORBIDDEN}.
     * @see MessageRequest
     * @see ExceptionResponse
     */
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
