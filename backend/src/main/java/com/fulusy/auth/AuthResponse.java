package com.fulusy.auth;

public record AuthResponse(
        String token,
        Long userId,
        String email,
        String name,
        String language
) {}
