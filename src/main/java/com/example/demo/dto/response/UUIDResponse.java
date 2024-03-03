package com.example.demo.dto.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a response object ID in {@link UUID} format.
 *
 * @author Andrey Sharipov
 * @version 1.0
 */
@Getter
@Setter
@AllArgsConstructor
public class UUIDResponse {
	/**
	 * Private {@link UUID} field contains ID.
	 */
	private UUID id;
}
