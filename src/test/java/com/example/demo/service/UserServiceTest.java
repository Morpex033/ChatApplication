package com.example.demo.service;

import com.example.demo.models.User;
import com.example.demo.models.role.Role;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
	@InjectMocks
	private UserService userService;
	@Mock
	private UserRepository userRepository;
	@Mock
	private PasswordEncoder passwordEncoder;
	@Mock
	private Authentication authentication;

	@Test
	void testSaveUser() {
		User user = new User();
		user.setUsername("test");
		user.setEmail("test@test.com");
		user.setPassword("test");

		when(userRepository.existsByEmail(anyString())).thenReturn(false);
		when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
		when(userRepository.save(any(User.class))).thenReturn(user);

		User savedUser = userService.save(user);

		assertNotNull(savedUser);
		assertEquals(user.getUsername(), savedUser.getUsername());
		assertEquals(user.getEmail(), savedUser.getEmail());
		assertEquals("encodedPassword", savedUser.getPassword());
		assertTrue(savedUser.getActive());
		assertTrue(savedUser.getRoles().contains(Role.ROLE_ADMIN));
		verify(userRepository, times(1)).save(user);
	}

	@Test
	void testSaveUser_EmailAlreadyTaken() {
		User user = new User();
		user.setUsername("test");
		user.setEmail("test@test.com");
		user.setPassword("test");

		when(userRepository.existsByEmail(anyString())).thenReturn(true);

		assertThrows(IllegalArgumentException.class, () -> userService.save(user));
		verify(userRepository, never()).save(user);
	}

	@Test
	void testSaveUser_passwordIsNull() {
		User user = new User();
		user.setUsername("test");
		user.setEmail("test@test.com");
		user.setPassword(null);

		when(userRepository.existsByEmail(anyString())).thenReturn(false);

		assertThrows(NullPointerException.class, () -> userService.save(user));
		verify(userRepository, never()).save(user);
	}

	@Test
	void testFindById() {
		UUID id = UUID.randomUUID();
		User user = new User();
		user.setId(id);

		when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
		User foundedUser = userService.findById(id.toString());

		assertNotNull(foundedUser);
		assertEquals(id, foundedUser.getId());
		verify(userRepository, times(1)).findById(any(UUID.class));
	}

	@Test
	void testFindById_userNotFound() {
		User user = new User();
		user.setId(UUID.randomUUID());

		assertThrows(IllegalStateException.class, () -> userService.findById(user.getId().toString()));
	}

	@Test
	void testUpdateUser() {
		User user = new User();
		User updatedUser = new User();
		updatedUser.setUsername("username");
		updatedUser.setEmail("email@example.com");

		when(authentication.getPrincipal()).thenReturn(user);

		userService.update(updatedUser, authentication);

		assertEquals(updatedUser.getUsername(), user.getUsername());
		assertEquals(updatedUser.getEmail(), user.getEmail());
		verify(userRepository, times(1)).save(user);
	}

	@Test
	void testDeleteUser(){
		User user = new User();

		userService.delete(user);
		verify(userRepository, times(1)).delete(user);
	}

	@Test
	void testCopyNotNullDetails(){
		User user = new User();
		User updatedUser = new User();
		updatedUser.setUsername("username");
		updatedUser.setEmail("email@example.com");

		userService.copyNotNullDetails(user, updatedUser);

		assertEquals(updatedUser.getEmail(), user.getEmail());
		assertEquals(updatedUser.getUsername(), user.getUsername());
	}
}
