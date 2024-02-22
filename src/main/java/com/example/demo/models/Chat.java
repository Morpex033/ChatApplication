package com.example.demo.models;

import java.util.List;
import java.util.Map;

import com.example.demo.models.role.Role;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table
public class Chat {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Long id;
	@Column
	private String name;
	@Column
    private String userRolesJson;
	@ManyToMany(mappedBy = "chats")
	private List<User> users;
	@OneToMany
	private List<Message> messages;
	
	 public Map<Long, Role> getUserRoles() {
	        try {
	            ObjectMapper mapper = new ObjectMapper();
	            return mapper.readValue(userRolesJson, new TypeReference<Map<Long, Role>>() {});
	        } catch (JsonProcessingException e) {
	            e.printStackTrace();
	            return null;
	        }
	    }

	    public void setUserRoles(Map<Long, Role> userRoles) {
	        try {
	            ObjectMapper mapper = new ObjectMapper();
	            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
	            this.userRolesJson = mapper.writeValueAsString(userRoles);
	        } catch (JsonProcessingException e) {
	            e.printStackTrace();
	        }
	    }
	
	public Role getUserRole(Long userId) {
        return getUserRoles().getOrDefault(userId, null);
    }
    
    public void setUserRole(Long userId, Role role) {
    	getUserRoles().put(userId, role);
    }
}
