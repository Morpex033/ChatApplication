package com.example.demo.controller;

import com.example.demo.models.User;
import com.example.demo.models.role.Role;
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
import com.example.demo.dto.response.ChatResponse;
import com.example.demo.dto.response.ExceptionResponse;
import com.example.demo.dto.response.UUIDResponse;
import com.example.demo.models.Chat;
import com.example.demo.service.ChatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * This is {@link RestController}.
 * It processes related requests with {@link Chat}.
 * The {@code @RestController} process incoming HTTP requests and return data in JSON or XML format.
 * The {@code @RequestMapping("/api/chat")} all controller methods will process requests along the path "/api/chat".
 * The {@code @RequiredArgsConstructor} provided by the Lombok project to automatically generate a constructor
 * that takes arguments for all final fields of a class and automatically initializes those fields
 * when an instance of the class is created.
 * The {@code @Slf4j} provided by the Lombok project to automatically generate logging methods in a class.
 *
 * @author Andrey Sharipov
 * @version 1.0
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    /**
     * A private {@link ChatService} field that is initialized by {@code @RequiredArgsConstructor}
     *
     * @see ChatService
     */
    private final ChatService chatService;

    /**
     * Controller method for GET requests to "/api/chat/{id}" which takes an id from url
     * and returns a {@link ResponseEntity} with an {@link HttpStatus#OK} and
     * a {@link ChatResponse} class object in the response body.
     *
     * @param id The ID taken from the URL "/api/chat/{id}", where {id} is a variable.
     * @return A {@link ResponseEntity} containing the retrieved {@link Chat} response object with status {@link HttpStatus#OK},
     * if the {@link Chat} with the specified ID is found. Otherwise, an {@link ExceptionResponse}
     * with a {@link HttpStatus#NOT_FOUND} status.
     * @throws IllegalStateException if the {@link Chat} is not found by id.
     * @see ChatResponse
     * @see ExceptionResponse
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getChat(@PathVariable("id") String id) {
        ChatResponse chat;
        try {
            chat = chatService.findById(id);
        } catch (IllegalStateException exception) {
            log.error(exception.getMessage(), exception);
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(chat);
    }

    /**
     * The controller method receives a POST request to create a chat and
     * sets the user passed to {@link Authentication} as an admin.
     * The body of the request contains {@link ChatRequest}.
     *
     * @param request {@link ChatRequest}.
     * @param auth    An {@link Authentication} object containing {@link User} authentication information.
     * @return A {@link ResponseEntity} object containing {@link UUIDResponse}
     * which contains the id of the created chat with status {@link HttpStatus#OK}.
     * In other cases, {@link ResponseEntity} with {@link ExceptionResponse} in the response body,
     * with status {@link HttpStatus#CONFLICT}.
     * @throws DataAccessException if saving the {@link Chat} to the database failed.
     * @throws IllegalStateException if the {@link User} does not exist in the database.
     * @throws NullPointerException if the {@link User} is not authenticated.
     * @see ChatRequest
     * @see Chat
     * @see UUIDResponse
     * @see ExceptionResponse
     */
    @PostMapping
    public ResponseEntity<?> createChat(@RequestBody ChatRequest request, Authentication auth) {
        Chat chat;
        try {
            chat = chatService.save(request, auth);
            chatService.setAdminUser(chat, auth);
        } catch (DataAccessException | IllegalStateException | NullPointerException exception) {
            log.error(exception.getMessage(), exception);
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(new UUIDResponse(chat.getId()), HttpStatus.OK);
    }

    /**
     * Controller method that deletes {@link Chat}.
     *
     * @param request {@link ChatRequest}.
     * @param auth    An {@link Authentication} object containing {@link User} authentication information.
     * @return A {@link ResponseEntity} with status {@link HttpStatus#OK}.
     * In other cases, {@link ResponseEntity} with {@link ExceptionResponse} in the response body,
     * with status {@link HttpStatus#FORBIDDEN}.
     * @throws DataAccessException if deleting the {@link Chat} of the database failed.
     * @throws IllegalStateException if the {@link User} does not exist in the database.
     * @throws NullPointerException if the {@link User} is not authenticated.
     * @see ChatRequest
     * @see ExceptionResponse
     */
    @DeleteMapping
    public ResponseEntity<?> deleteChat(@RequestBody ChatRequest request, Authentication auth) {
        try {
            chatService.delete(request, auth);
        } catch (DataAccessException | IllegalStateException | NullPointerException exception) {
            log.error(exception.getMessage(), exception);
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Controller method for {@link Chat} update.
     *
     * @param request {@link ChatRequest}.
     * @param auth    An {@link Authentication} object containing {@link User} authentication information.
     * @return A {@link ResponseEntity} with status {@link HttpStatus#OK}.
     * In other cases, {@link ResponseEntity} with {@link ExceptionResponse} in the response body,
     * with status {@link HttpStatus#FORBIDDEN}.
     * @throws DataAccessException if saving the {@link Chat} to the database failed.
     * @throws IllegalStateException if the {@link User} does not exist in the database.
     * @throws NullPointerException if the {@link User} is not authenticated.
     * @see ChatRequest
     * @see ExceptionResponse
     */
    @PutMapping("/edit")
    public ResponseEntity<?> updateChat(@RequestBody ChatRequest request, Authentication auth) {
        try {
            chatService.update(request, auth);
        } catch (DataAccessException | IllegalStateException | NullPointerException exception) {
            log.error(exception.getMessage(), exception);
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
    /**
     * Controller method for assigning a {@link Role} to a {@link User} in a {@link Chat}.
     *
     * @param request {@link ChatRoleRequest}.
     * @param auth    An {@link Authentication} object containing {@link User} authentication information.
     * @return A {@link ResponseEntity} with status {@link HttpStatus#OK}.
     * In other cases, {@link ResponseEntity} with {@link ExceptionResponse} in the response body,
     * with status {@link HttpStatus#FORBIDDEN}.
     * @throws DataAccessException if saving the {@link Chat} to the database failed.
     * @throws IllegalStateException if the {@link User} or {@link Chat} does not exist in the database.
     * @throws NullPointerException if the {@link User} is not authenticated.
     * @see ChatRoleRequest
     * @see ExceptionResponse
     */
    @PutMapping("/role")
    public ResponseEntity<?> addRoleToUser(@RequestBody ChatRoleRequest request, Authentication auth) {
        try {
            chatService.setRoleToUser(request, auth);
        } catch (DataAccessException | IllegalStateException | NullPointerException exception) {
            log.error(exception.getMessage(), exception);
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * A controller method that adds a {@link User} to the {@link Chat} and assigns a {@link Role#ROLE_USER} to him.
     *
     * @param request {@link ChatUserRequest}.
     * @param auth    An {@link Authentication} object containing {@link User} authentication information.
     * @return A {@link ResponseEntity} with status {@link HttpStatus#OK}.
     * In other cases, {@link ResponseEntity} with {@link ExceptionResponse} in the response body,
     * with status {@link HttpStatus#FORBIDDEN}.
     * @throws DataAccessException if saving the {@link Chat} to the database failed.
     * @throws IllegalStateException if the {@link User} or {@link Chat} does not exist in the database.
     * @throws NullPointerException if the {@link User} is not authenticated.
     * @see ChatUserRequest
     * @see ExceptionResponse
     */
    @PutMapping
    public ResponseEntity<?> addUser(@RequestBody ChatUserRequest request, Authentication auth) {
        try {
            chatService.addUser(request, auth);
        } catch (DataAccessException | IllegalStateException | NullPointerException exception) {
            log.error(exception.getMessage(), exception);
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * A controller method that removes a {@link User} from a {@link Chat}.
     *
     * @param request {@link ChatUserRequest}.
     * @param auth    An {@link Authentication} object containing {@link User} authentication information.
     * @return A {@link ResponseEntity} with status {@link HttpStatus#OK}.
     * In other cases, {@link ResponseEntity} with {@link ExceptionResponse} in the response body,
     * with status {@link HttpStatus#FORBIDDEN}.
     * @throws DataAccessException if saving the {@link Chat} to the database failed.
     * @throws IllegalStateException if the {@link User} or {@link Chat} does not exist in the database.
     * @throws NullPointerException if the {@link User} is not authenticated.
     * @see ChatUserRequest
     * @see ExceptionResponse
     */
    @DeleteMapping("/user")
    public ResponseEntity<?> removeUser(@RequestBody ChatUserRequest request, Authentication auth) {
        try {
            chatService.removeUser(request, auth);
        } catch (DataAccessException | IllegalStateException | NullPointerException exception) {
            log.error(exception.getMessage(), exception);
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
