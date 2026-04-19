package com.fulusy.common.security;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Set;

@ApplicationScoped
public class JwtService {

    @ConfigProperty(name = "smallrye.jwt.verify.secretkey")
    String signKey;

    public String issueToken(Long userId, String email) {
        SecretKeySpec key = new SecretKeySpec(
                signKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return Jwt.issuer("https://fulusy.app")
                .subject(String.valueOf(userId))
                .upn(email)
                .groups(Set.of("user"))
                .claim("userId", userId)
                .expiresIn(Duration.ofHours(24))
                .sign(key);
    }
}
