package com.example.movie.dto.response.user;

public record AuthResponse(String accessToken, String tokenType) { public AuthResponse(String t){ this(t,"Bearer"); } }