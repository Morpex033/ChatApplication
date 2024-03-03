package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a response object ID in String format.
 *
 * @author Andrey Sharipov
 * @version 1.0
 */
@Getter
@Setter
@AllArgsConstructor
public class StringIdResponse {
	/**
	 * Private ID field.
	 */
	private String id;
}
