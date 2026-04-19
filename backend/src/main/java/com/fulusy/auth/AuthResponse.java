package com.fulusy.auth;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class AuthResponse {
    public String token;
    public Long userId;
    public String email;
    public String name;
    public String language;

    public AuthResponse() {}

    public AuthResponse(String token, Long userId, String email, String name, String language) {
        this.token = token;
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.language = language;
    }

    public String getToken() { return token; }
    public Long getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getLanguage() { return language; }

    public void setToken(String token) { this.token = token; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setEmail(String email) { this.email = email; }
    public void setName(String name) { this.name = name; }
    public void setLanguage(String language) { this.language = language; }

    // Record-style accessor for backward compatibility
    public String token() { return token; }
    public Long userId() { return userId; }
    public String email() { return email; }
    public String name() { return name; }
    public String language() { return language; }
}
