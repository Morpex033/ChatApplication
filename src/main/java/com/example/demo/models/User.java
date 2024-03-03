package com.example.demo.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.demo.models.role.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a user entity in the application.
 *
 * <p>This class defines the structure of a user entity, including its unique identifier, username, password,
 * email, active status, roles, and associated chats.</p>
 *
 * @author Andrey Sharipov
 * @version 1.0
 * @see UserDetails
 */
@Data
@Entity
@Table(name = "users")
@NoArgsConstructor(force = true)
public class User implements UserDetails {
	/**
	 * The unique identifier for the user.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column
	private UUID id;
	/**
	 * The username of the user.
	 */
	@Column
	private String username;
	/**
	 * The password of the user.
	 */
	@Column
	@JsonProperty(access = Access.WRITE_ONLY)
	private String password;
	/**
	 * The email address of the user.
	 */
	@Email
	@Column(unique = true)
	private String email;
	/**
	 * The active status of the user.
	 */
	@Column
	private Boolean active;
	/**
	 * The {@link Role} assigned to the user.
	 *
	 * @see Role
	 */
	@ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
	@CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
	@Enumerated(EnumType.STRING)
	private Collection<Role> roles = new HashSet<>();
	/**
	 * The list of {@link Chat} the user is associated with.
	 *
	 * @see Chat
	 */
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JsonIgnore
	private List<Chat> chats = new ArrayList<>();

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles;
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return active;
	}
}
