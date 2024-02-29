package com.example.demo.service;

import com.example.demo.dto.request.ChatRequest;
import com.example.demo.dto.request.ChatRoleRequest;
import com.example.demo.dto.request.ChatUserRequest;
import com.example.demo.dto.response.ChatResponse;
import com.example.demo.models.Chat;
import com.example.demo.models.Message;
import com.example.demo.models.User;
import com.example.demo.models.UserRoleChat;
import com.example.demo.models.role.Role;
import com.example.demo.repository.ChatRepository;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.UserRoleChatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {
    @InjectMocks
    private ChatService chatService;
    @Mock
    private ChatRepository chatRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private UserRoleChatRepository userRoleChatRepository;
    @Mock
    private Authentication authentication;

    private ChatRequest request;

    @BeforeEach
    void setUp() {
        this.request = new ChatRequest();
    }

    @Test
    void testSaveChat() {
        User user = new User();
        user.setId(UUID.randomUUID());

        Chat chat = new Chat();
        chat.setName("test");

        request.setChat(chat);

        when(authentication.getPrincipal()).thenReturn(user);
        when(userRepository.existsById(any(UUID.class))).thenReturn(true);
        when(chatRepository.save(any(Chat.class))).thenReturn(chat);

        Chat savedChat = chatService.save(request, authentication);

        assertEquals(chat, savedChat);
        verify(chatRepository, times(1)).save(chat);
    }

    @Test
    void testSaveChat_throwNullPointerException() {
        assertThrows(NullPointerException.class, () -> chatService.save(request, authentication));
        verify(chatRepository, never()).save(any(Chat.class));
    }

    @Test
    void testSaveChat_throwIllegalStateException() {
        User user = new User();
        user.setId(UUID.randomUUID());

        when(authentication.getPrincipal()).thenReturn(user);

        assertThrows(IllegalStateException.class, () -> chatService.save(request, authentication));
        verify(chatRepository, never()).save(any(Chat.class));
    }

    @Test
    void testFindById() {
        User user = new User();
        user.setId(UUID.randomUUID());

        Chat chat = new Chat();
        chat.setId(UUID.randomUUID());
        chat.getUsers().add(user);

        List<Message> messageList = new ArrayList<>();
        Message message = new Message();
        message.setChatId(chat.getId());
        message.setUserId(user.getId());
        message.setContext("test");
        messageList.add(message);

        when(chatRepository.findById(any(UUID.class))).thenReturn(Optional.of(chat));
        when(messageRepository.findAll()).thenReturn(messageList);

        ChatResponse response = chatService.findById(chat.getId().toString());

        assertEquals(chat.getId(), response.getId());
        assertEquals(chat.getName(), response.getName());
        assertEquals(chat.getUsers(), response.getUsers());
        assertEquals(messageList, response.getMessages());
    }

    @Test
    void testFindById_throwIllegalStateException() {
        UUID id = UUID.randomUUID();

        assertThrows(IllegalStateException.class, () -> chatService.findById(id.toString()));
    }

    @Test
    void testUpdateChat() {
        User user = new User();
        user.setId(UUID.randomUUID());

        when(authentication.getPrincipal()).thenReturn(user);

        Chat chat = new Chat();
        chat.setId(UUID.randomUUID());

        UserRoleChat userRoleChat = new UserRoleChat();
        userRoleChat.setUserId(user.getId());
        userRoleChat.setRole(Role.ROLE_ADMIN);
        userRoleChat.setChat(chat);

        chat.getUserRole().add(userRoleChat);

        Chat updatedChat = new Chat();
        updatedChat.setId(chat.getId());
        updatedChat.setName("test");

        ChatRequest request = new ChatRequest();
        request.setChat(updatedChat);

        when(chatRepository.findById(any(UUID.class))).thenReturn(Optional.of(chat));

        chatService.update(request, authentication);

        assertEquals(updatedChat.getName(), chat.getName());
        verify(chatRepository, times(1)).save(chat);
    }

    @Test
    void testUpdateChat_throwNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                chatService.update(new ChatRequest(), authentication));

        verify(chatRepository, never()).save(any(Chat.class));
    }

    @Test
    void testUpdateChat_throwIllegalStateException() {
        User user = new User();
        user.setId(UUID.randomUUID());

        Chat chat = new Chat();
        chat.setId(UUID.randomUUID());

        ChatRequest request = new ChatRequest();
        request.setChat(chat);

        when(authentication.getPrincipal()).thenReturn(user);

        assertThrows(IllegalStateException.class, () -> chatService.update(request, authentication));

        verify(chatRepository, never()).save(chat);

        when(chatRepository.findById(any(UUID.class))).thenReturn(Optional.of(chat));

        assertThrows(IllegalStateException.class, () -> chatService.update(request, authentication));

        verify(chatRepository, never()).save(chat);
    }

    @Test
    void testDeleteChat() {
        User user = new User();
        user.setId(UUID.randomUUID());

        Chat chat = new Chat();
        chat.setId(UUID.randomUUID());

        when(authentication.getPrincipal()).thenReturn(user);
        when(chatRepository.findById(any(UUID.class))).thenReturn(Optional.of(chat));

        UserRoleChat userRoleChat = new UserRoleChat();
        userRoleChat.setUserId(user.getId());
        userRoleChat.setRole(Role.ROLE_ADMIN);
        userRoleChat.setChat(chat);

        chat.getUserRole().add(userRoleChat);

        Message message = new Message();
        message.setUserId(user.getId());
        message.setChatId(chat.getId());
        message.setContext("test");

        messageRepository.save(message);

        ChatRequest request = new ChatRequest();
        request.setChat(chat);

        chatService.delete(request, authentication);

        verify(messageRepository, times(1)).deleteAllByChatId(chat.getId());
        verify(userRoleChatRepository, times(1)).deleteAllByChat(chat);
        verify(chatRepository, times(1)).delete(chat);
    }

    @Test
    void testDeleteChat_throwNullPointerException(){
        ChatRequest request = new ChatRequest();
        assertThrows(NullPointerException.class, () -> chatService.delete(request, authentication));

        verify(messageRepository, never()).deleteAllByChatId(any());
        verify(userRoleChatRepository, never()).deleteAllByChat(any());
        verify(chatRepository, never()).delete(any());
    }

    @Test
    void testDeleteChat_throwIllegalStateException(){
        User user = new User();

        Chat chat = new Chat();
        chat.setId(UUID.randomUUID());

        ChatRequest request = new ChatRequest();
        request.setChat(chat);

        when(authentication.getPrincipal()).thenReturn(user);

        assertThrows(IllegalStateException.class, () -> chatService.delete(request, authentication));

        verify(messageRepository, never()).deleteAllByChatId(any());
        verify(userRoleChatRepository, never()).deleteAllByChat(any());
        verify(chatRepository, never()).delete(any());

        when(chatRepository.findById(any(UUID.class))).thenReturn(Optional.of(chat));

        assertThrows(IllegalStateException.class, () -> chatService.delete(request, authentication));

        verify(messageRepository, never()).deleteAllByChatId(any());
        verify(userRoleChatRepository, never()).deleteAllByChat(any());
        verify(chatRepository, never()).delete(any());
    }

    @Test
    void testSetAdminUser(){
        Chat chat = new Chat();
        chat.setId(UUID.randomUUID());

        User user = new User();
        user.setId(UUID.randomUUID());

        when(authentication.getPrincipal()).thenReturn(user);

        chatService.setAdminUser(chat, authentication);

        UserRoleChat userRoleChat = chat.getUserRole().get(0);

        assertEquals(user.getId(), userRoleChat.getUserId());
        assertEquals(chat, userRoleChat.getChat());
        assertEquals(Role.ROLE_ADMIN, userRoleChat.getRole());

        verify(chatRepository, times(1)).save(chat);
    }

    @Test
    void testSetAdminUser_throwNullPointerException(){
        Chat chat = new Chat();

        assertThrows(NullPointerException.class, () -> chatService.setAdminUser(chat, authentication));

        verify(chatRepository, never()).save(chat);
    }

    @Test
    void testCopyNotNullDetails(){
        Chat chat = new Chat();
        Chat newChat = new Chat();
        newChat.setName("test");

        chatService.copyNotNullDetails(chat, newChat);

        assertEquals(newChat.getName(), chat.getName());
    }

    @Test
    void testSetRoleToUser(){
        User user = new User();
        user.setId(UUID.randomUUID());

        User userModerator = new User();
        userModerator.setId(UUID.randomUUID());

        Chat chat = new Chat();
        chat.setId(UUID.randomUUID());
        chat.getUsers().add(user);
        chat.getUsers().add(userModerator);

        UserRoleChat userRole = new UserRoleChat();
        userRole.setChat(chat);
        userRole.setUserId(user.getId());
        userRole.setRole(Role.ROLE_ADMIN);

        chat.getUserRole().add(userRole);

        UserRoleChat userModeratorRole = new UserRoleChat();
        userModeratorRole.setChat(chat);
        userModeratorRole.setUserId(userModerator.getId());
        userModeratorRole.setRole(Role.ROLE_USER);

        chat.getUserRole().add(userModeratorRole);

        ChatRoleRequest chatRoleRequest = new ChatRoleRequest();
        chatRoleRequest.setChat(chat);
        chatRoleRequest.setRole(Role.ROLE_MODERATOR);
        chatRoleRequest.setUserId(userModerator.getId());

        when(authentication.getPrincipal()).thenReturn(user);
        when(chatRepository.findById(any(UUID.class))).thenReturn(Optional.of(chat));

        chatService.setRoleToUser(chatRoleRequest, authentication);

        UserRoleChat savedUserRoleChat = chat.getUserRole().get(1);

        assertEquals(userModerator.getId(), savedUserRoleChat.getUserId());
        assertEquals(Role.ROLE_MODERATOR, savedUserRoleChat.getRole());
        assertEquals(chat, savedUserRoleChat.getChat());

        verify(chatRepository, times(1)).save(chat);
    }

    @Test
    void testSetRoleToUser_throwNullPointerException(){
        ChatRoleRequest chatRoleRequest = new ChatRoleRequest();

        assertThrows(NullPointerException.class, () -> chatService.setRoleToUser(chatRoleRequest, authentication));

        verify(chatRepository, never()).save(any());
    }

    @Test
    void testSetRoleToUser_throwIllegalStateException(){
        User user = new User();
        user.setId(UUID.randomUUID());

        Chat chat = new Chat();
        chat.setId(UUID.randomUUID());

        ChatRoleRequest chatRoleRequest = new ChatRoleRequest();
        chatRoleRequest.setChat(chat);

        when(authentication.getPrincipal()).thenReturn(user);

        assertThrows(IllegalStateException.class,() -> chatService.setRoleToUser(chatRoleRequest, authentication));

        verify(chatRepository, never()).save(chat);

        UserRoleChat userRole = new UserRoleChat();
        userRole.setChat(chat);
        userRole.setUserId(user.getId());
        userRole.setRole(Role.ROLE_ADMIN);

        chat.getUsers().add(user);
        chat.getUserRole().add(userRole);

        User testUser = new User();
        testUser.setId(UUID.randomUUID());

        chatRoleRequest.setUserId(testUser.getId());

        assertThrows(IllegalStateException.class, () -> chatService.setRoleToUser(chatRoleRequest, authentication));
        verify(chatRepository, never()).save(chat);
    }

    @Test
    void testAddUser(){
        User user = new User();
        user.setId(UUID.randomUUID());

        Chat chat = new Chat();
        chat.setId(UUID.randomUUID());
        chat.getUsers().add(user);

        UserRoleChat userRole = new UserRoleChat();
        userRole.setUserId(user.getId());
        userRole.setRole(Role.ROLE_ADMIN);
        userRole.setChat(chat);

        chat.getUserRole().add(userRole);

        when(authentication.getPrincipal()).thenReturn(user);
        when(chatRepository.findById(any(UUID.class))).thenReturn(Optional.of(chat));

        User newUser = new User();
        newUser.setId(UUID.randomUUID());

        ChatUserRequest chatUserRequest = new ChatUserRequest();
        chatUserRequest.setChat(chat);
        chatUserRequest.setUserId(newUser.getId());

        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(newUser));

        chatService.addUser(chatUserRequest, authentication);

        UserRoleChat newUserRole = chat.getUserRole().get(1);

        assertTrue(chat.getUsers().contains(newUser));
        assertEquals(chat, newUserRole.getChat());
        assertEquals(newUser.getId(), newUserRole.getUserId());
        assertEquals(Role.ROLE_USER, newUserRole.getRole());

        verify(chatRepository, times(1)).save(chat);
    }

    @Test
    void testAddUser_throwNullPointerException(){
        ChatUserRequest chatUserRequest = new ChatUserRequest();

        assertThrows(NullPointerException.class, () -> chatService.addUser(chatUserRequest, authentication));

        verify(chatRepository, never()).save(any());
    }

    @Test
    void testAddUser_throwIllegalStateException(){
        User user = new User();
        user.setId(UUID.randomUUID());

        User newUser = new User();
        newUser.setId(UUID.randomUUID());

        Chat chat = new Chat();
        chat.setId(UUID.randomUUID());

        ChatUserRequest chatUserRequest = new ChatUserRequest();
        chatUserRequest.setChat(chat);
        chatUserRequest.setUserId(newUser.getId());

        when(authentication.getPrincipal()).thenReturn(user);

        assertThrows(IllegalStateException.class, () -> chatService.addUser(chatUserRequest, authentication));

        verify(chatRepository, never()).save(chat);

        when(chatRepository.findById(any(UUID.class))).thenReturn(Optional.of(chat));

        assertThrows(IllegalStateException.class, () -> chatService.addUser(chatUserRequest, authentication));

        verify(chatRepository, never()).save(chat);

        chat.getUsers().add(user);

        assertThrows(IllegalStateException.class, () -> chatService.addUser(chatUserRequest, authentication));

        verify(chatRepository, never()).save(chat);
    }

    @Test
    void testRemoveUser(){
        User user = new User();
        user.setId(UUID.randomUUID());

        User deletedUser = new User();
        deletedUser.setId(UUID.randomUUID());

        Chat chat = new Chat();
        chat.setId(UUID.randomUUID());
        chat.getUsers().add(user);
        chat.getUsers().add(deletedUser);

        UserRoleChat userRole = new UserRoleChat();
        userRole.setChat(chat);
        userRole.setUserId(user.getId());
        userRole.setRole(Role.ROLE_ADMIN);

        UserRoleChat deletedUserRole = new UserRoleChat();
        deletedUserRole.setChat(chat);
        deletedUserRole.setUserId(deletedUser.getId());
        deletedUserRole.setRole(Role.ROLE_USER);

        chat.getUserRole().add(userRole);
        chat.getUserRole().add(deletedUserRole);

        when(authentication.getPrincipal()).thenReturn(user);
        when(chatRepository.findById(any(UUID.class))).thenReturn(Optional.of(chat));
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(deletedUser));

        ChatUserRequest chatUserRequest = new ChatUserRequest();
        chatUserRequest.setUserId(deletedUser.getId());
        chatUserRequest.setChat(chat);

        chatService.removeUser(chatUserRequest, authentication);

        verify(chatRepository, times(1)).save(chat);
        verify(userRoleChatRepository, times(1)).delete(deletedUserRole);
    }

    @Test
    void testRemoveUser_throwNullPointerException(){
        ChatUserRequest chatUserRequest = new ChatUserRequest();

        assertThrows(NullPointerException.class, () -> chatService.removeUser(chatUserRequest, authentication));

        verify(chatRepository, never()).save(any());
        verify(userRoleChatRepository, never()).delete(any());
    }

    @Test
    void testRemoveUser_throwIllegalStateException(){
        User user = new User();
        user.setId(UUID.randomUUID());

        Chat chat = new Chat();
        chat.setId(UUID.randomUUID());

        ChatUserRequest chatUserRequest = new ChatUserRequest();
        chatUserRequest.setChat(chat);

        when(authentication.getPrincipal()).thenReturn(user);

        assertThrows(IllegalStateException.class, () -> chatService.removeUser(chatUserRequest, authentication));

        verify(chatRepository, never()).save(any());
        verify(userRoleChatRepository, never()).delete(any());

        when(chatRepository.findById(any(UUID.class))).thenReturn(Optional.of(chat));

        UserRoleChat userRole = new UserRoleChat();
        userRole.setRole(Role.ROLE_ADMIN);
        userRole.setChat(chat);
        userRole.setUserId(user.getId());

        chat.getUsers().add(user);
        chat.getUserRole().add(userRole);

        assertThrows(IllegalStateException.class, () -> chatService.removeUser(chatUserRequest, authentication));

        verify(chatRepository, never()).save(any());
        verify(userRoleChatRepository, never()).delete(any());

        User removedUser = new User();
        removedUser.setId(UUID.randomUUID());

        chat.getUsers().add(removedUser);

        chatUserRequest.setUserId(removedUser.getId());

        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(removedUser));

        assertThrows(IllegalStateException.class, () -> chatService.removeUser(chatUserRequest, authentication));

        verify(chatRepository, never()).save(any());
        verify(userRoleChatRepository, never()).delete(any());
    }
}