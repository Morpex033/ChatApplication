package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents an error response object.
 *
 * @author Andrey Sharipov
 * @version 1.0
 */
@Getter
@Setter
@AllArgsConstructor
public class ExceptionResponse {
	/**
	 * Private message field.
	 */
	private String message;
}
