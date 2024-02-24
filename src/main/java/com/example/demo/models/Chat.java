package com.example.demo.models;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.util.ArrayList;

import lombok.Data;

@Data
@Entity
@Table
public class Chat {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column
	private UUID id;
	@Column
	private String name;
	@OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<UserRoleChat> userRole = new ArrayList<>();
	@ManyToMany
	@JoinTable(
		    name = "chat_user", 
		    joinColumns = @JoinColumn(name = "chat_id"), 
		    inverseJoinColumns = @JoinColumn(name = "user_id"))
	private List<User> users = new ArrayList<>();
	@Transient
	private List<Message> messages = new ArrayList<>();

}
