package com.example.demo.service;

import com.example.demo.dto.request.MessageRequest;
import com.example.demo.models.Chat;
import com.example.demo.models.Message;
import com.example.demo.models.User;
import com.example.demo.models.UserRoleChat;
import com.example.demo.models.role.Role;
import com.example.demo.repository.ChatRepository;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {
    @InjectMocks
    private MessageService messageService;
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ChatRepository chatRepository;
    @Mock
    private Authentication authentication;

    private MessageRequest messageRequest;

    @BeforeEach
    void setUp() {
        this.messageRequest = new MessageRequest();
    }

    @Test
    void testSaveMessage() {
        User user = new User();
        user.setId(UUID.randomUUID());

        Chat chat = new Chat();
        chat.setId(UUID.randomUUID());
        chat.getUsers().add(user);

        Message message = new Message();
        message.setContext("test");

        messageRequest.setMessage(message);
        messageRequest.setChatId(chat.getId());

        when(authentication.getPrincipal()).thenReturn(user);
        when(chatRepository.findById(any(UUID.class))).thenReturn(Optional.of(chat));

        messageService.save(messageRequest, authentication);

        verify(userRepository, times(1)).save(user);
        verify(chatRepository, times(1)).save(chat);
        verify(messageRepository, times(1)).save(message);
    }

    @Test
    void testSaveMessage_throwNullPointerException() {
        assertThrows(NullPointerException.class, () -> messageService.save(messageRequest, authentication));

        verify(userRepository, never()).save(any());
        verify(chatRepository, never()).save(any());
        verify(messageRepository, never()).save(any());
    }

    @Test
    void testSaveMessage_throwIllegalArgumentException(){
        User user = new User();
        user.setId(UUID.randomUUID());

        Chat chat = new Chat();
        chat.setId(UUID.randomUUID());

        Message message = new Message();
        message.setContext("test");

        messageRequest.setMessage(message);
        messageRequest.setChatId(chat.getId());

        when(authentication.getPrincipal()).thenReturn(user);
        when(chatRepository.findById(any(UUID.class))).thenReturn(Optional.of(chat));

        assertThrows(IllegalArgumentException.class, () -> messageService.save(messageRequest, authentication));

        verify(userRepository, never()).save(any());
        verify(chatRepository, never()).save(any());
        verify(messageRepository, never()).save(any());
    }

    @Test
    void testSaveMessage_throwIllegalStateException(){
        User user = new User();
        user.setId(UUID.randomUUID());

        Chat chat = new Chat();
        chat.setId(UUID.randomUUID());
        chat.getUsers().add(user);

        messageRequest.setChatId(chat.getId());

        when(authentication.getPrincipal()).thenReturn(user);

        assertThrows(IllegalStateException.class, () -> messageService.save(messageRequest, authentication));

        verify(userRepository, never()).save(any());
        verify(chatRepository, never()).save(any());
        verify(messageRepository, never()).save(any());
    }

    @Test
    void testFindById(){
        String id = UUID.randomUUID().toString();

        Message message = new Message();
        message.setId(id);

        when(messageRepository.findById(anyString())).thenReturn(Optional.of(message));

        Message findedMessage = messageService.findById(id);

        assertEquals(message, findedMessage);
    }

    @Test
    void testFindById_throwIllegalStateException(){
        assertThrows(IllegalStateException.class, () -> messageService.findById(UUID.randomUUID().toString()));
    }

    @Test
    void testUpdateMessage(){
        User user = new User();
        user.setId(UUID.randomUUID());

        Chat chat = new Chat();
        chat.setId(UUID.randomUUID());
        chat.getUsers().add(user);

        Message message = new Message();
        message.setContext("test");
        message.setUserId(user.getId());
        message.setChatId(chat.getId());

        messageRequest.setMessage(message);
        messageRequest.setChatId(chat.getId());

        when(authentication.getPrincipal()).thenReturn(user);
        when(chatRepository.findById(any(UUID.class))).thenReturn(Optional.of(chat));
        when(messageRepository.findById(any())).thenReturn(Optional.of(message));

        messageService.update(messageRequest, authentication);

        verify(messageRepository, times(1)).save(message);
    }

    @Test
    void testUpdateMessage_throwNullPointerException() {
        assertThrows(NullPointerException.class, () -> messageService.update(messageRequest, authentication));

        verify(userRepository, never()).save(any());
        verify(chatRepository, never()).save(any());
        verify(messageRepository, never()).save(any());
    }

    @Test
    void testUpdateMessage_throwIllegalArgumentException(){
        User user = new User();
        user.setId(UUID.randomUUID());

        Chat chat = new Chat();
        chat.setId(UUID.randomUUID());

        Message message = new Message();
        message.setContext("test");
        message.setUserId(UUID.randomUUID());
        message.setChatId(UUID.randomUUID());

        messageRequest.setMessage(message);
        messageRequest.setChatId(chat.getId());

        when(authentication.getPrincipal()).thenReturn(user);
        when(chatRepository.findById(any(UUID.class))).thenReturn(Optional.of(chat));
        when(messageRepository.findById(any())).thenReturn(Optional.of(message));

        assertThrows(IllegalArgumentException.class, () -> messageService.update(messageRequest, authentication));

        verify(userRepository, never()).save(any());
        verify(chatRepository, never()).save(any());
        verify(messageRepository, never()).save(any());
    }

    @Test
    void testUpdateMessage_throwIllegalStateException() {
        User user = new User();
        user.setId(UUID.randomUUID());

        Chat chat = new Chat();
        chat.setId(UUID.randomUUID());
        chat.getUsers().add(user);

        Message message = new Message();
        message.setContext("test");
        message.setUserId(user.getId());
        message.setChatId(chat.getId());

        messageRequest.setMessage(message);
        messageRequest.setChatId(chat.getId());

        when(authentication.getPrincipal()).thenReturn(user);

        assertThrows(IllegalStateException.class, () -> messageService.update(messageRequest, authentication));

        verify(userRepository, never()).save(any());
        verify(chatRepository, never()).save(any());
        verify(messageRepository, never()).save(any());

        when(chatRepository.findById(any(UUID.class))).thenReturn(Optional.of(chat));

        assertThrows(IllegalStateException.class, () -> messageService.update(messageRequest, authentication));

        verify(userRepository, never()).save(any());
        verify(chatRepository, never()).save(any());
        verify(messageRepository, never()).save(any());
    }

    @Test
    void testDeleteMessage(){
        User user = new User();
        user.setId(UUID.randomUUID());

        Chat chat = new Chat();
        chat.setId(UUID.randomUUID());
        chat.getUsers().add(user);

        Message message = new Message();
        message.setId(UUID.randomUUID().toString());
        message.setContext("test");
        message.setUserId(user.getId());
        message.setChatId(chat.getId());

        UserRoleChat userRole = new UserRoleChat();
        userRole.setRole(Role.ROLE_ADMIN);
        userRole.setUserId(user.getId());
        userRole.setChat(chat);

        chat.getUserRole().add(userRole);

        messageRequest.setMessage(message);
        messageRequest.setChatId(chat.getId());

        when(authentication.getPrincipal()).thenReturn(user);
        when(chatRepository.findById(any(UUID.class))).thenReturn(Optional.of(chat));
        when(messageRepository.findById(anyString())).thenReturn(Optional.of(message));

        messageService.delete(messageRequest, authentication);

        verify(messageRepository, times(1)).delete(message);
    }

    @Test
    void testDeleteMessage_throwNullPointerException(){
        assertThrows(NullPointerException.class, () -> messageService.delete(messageRequest,authentication));

        verify(messageRepository, never()).delete(any());
    }

    @Test
    void testDeleteMessage_throwIllegalArgumentException(){
        User user = new User();
        user.setId(UUID.randomUUID());

        Chat chat = new Chat();
        chat.setId(UUID.randomUUID());
        chat.getUsers().add(user);

        Message message = new Message();
        message.setId(UUID.randomUUID().toString());
        message.setContext("test");
        message.setUserId(user.getId());
        message.setChatId(chat.getId());

        messageRequest.setMessage(message);
        messageRequest.setChatId(chat.getId());

        when(authentication.getPrincipal()).thenReturn(user);
        when(chatRepository.findById(any(UUID.class))).thenReturn(Optional.of(chat));
        when(messageRepository.findById(anyString())).thenReturn(Optional.of(message));

        assertThrows(IllegalArgumentException.class, () -> messageService.delete(messageRequest, authentication));

        verify(messageRepository, never()).delete(message);
    }

    @Test
    void testDeleteMessage_throwIllegalStateException(){
        User user = new User();
        user.setId(UUID.randomUUID());

        Chat chat = new Chat();
        chat.setId(UUID.randomUUID());
        chat.getUsers().add(user);

        Message message = new Message();
        message.setContext("test");
        message.setUserId(user.getId());
        message.setChatId(chat.getId());

        messageRequest.setMessage(message);
        messageRequest.setChatId(chat.getId());

        when(authentication.getPrincipal()).thenReturn(user);

        assertThrows(IllegalStateException.class, () -> messageService.update(messageRequest, authentication));

        verify(userRepository, never()).save(any());
        verify(chatRepository, never()).save(any());
        verify(messageRepository, never()).save(any());

        when(chatRepository.findById(any(UUID.class))).thenReturn(Optional.of(chat));

        assertThrows(IllegalStateException.class, () -> messageService.update(messageRequest, authentication));

        verify(messageRepository, never()).save(any());
    }

    @Test
    void testCopyNotNullDetails(){
        Message existingMessage = new Message();

        Message newMessage = new Message();
        newMessage.setContext("test");

        messageService.copyNotNullDetails(existingMessage, newMessage);

        assertEquals(newMessage, existingMessage);
    }
}
