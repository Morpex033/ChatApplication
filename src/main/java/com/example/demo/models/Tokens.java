package com.example.demo.models;

public record Tokens(String accessToken, String accessTokenExpiry,
		String refreshToken, String refreshTokenExpiry) {

}
