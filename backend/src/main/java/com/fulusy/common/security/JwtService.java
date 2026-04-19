package com.fulusy.common.security;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Duration;
import java.util.Set;

@ApplicationScoped
public class JwtService {

    public String issueToken(Long userId, String email) {
        return Jwt.issuer("https://fulusy.app")
                .subject(String.valueOf(userId))
                .upn(email)
                .groups(Set.of("user"))
                .claim("userId", userId)
                .expiresIn(Duration.ofDays(1))
                .sign();
    }
}
