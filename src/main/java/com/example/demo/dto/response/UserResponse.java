package com.example.demo.dto.response;

import java.util.Collection;

import com.example.demo.models.role.Role;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserResponse {
	private String username;
	@Email
	private String email;
	private Collection<Role> roles;
}
