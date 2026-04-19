package com.fulusy.common.security;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;

/**
 * Request-scoped helper to resolve the currently authenticated user's ID
 * from the JWT. Inject this instead of parsing JWT claims in every endpoint.
 */
@RequestScoped
public class AuthContext {

    @Inject JsonWebToken jwt;
    @Inject SecurityIdentity identity;

    public Long getUserId() {
        Long userId = jwt.getClaim("userId");
        if (userId == null) {
            // fallback: subject is the user id string
            String sub = jwt.getSubject();
            if (sub != null) return Long.parseLong(sub);
        }
        return userId;
    }

    public String getEmail() {
        return jwt.getName();
    }

    public boolean isAuthenticated() {
        return !identity.isAnonymous();
    }
}
