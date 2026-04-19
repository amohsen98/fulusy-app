package com.fulusy.auth;

import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.Map;

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@PermitAll
public class AuthResource {

    @Inject AuthService authService;

    @POST
    @Path("/register")
    public Map<String, Object> register(@Valid RegisterRequest req) {
        AuthResponse response = authService.register(req);
        return Map.of(
            "token", response.token(),
            "userId", response.userId(),
            "email", response.email(),
            "name", response.name(),
            "language", response.language()
        );
    }

    @POST
    @Path("/login")
    public Map<String, Object> login(@Valid LoginRequest req) {
        AuthResponse response = authService.login(req);
        return Map.of(
            "token", response.token(),
            "userId", response.userId(),
            "email", response.email(),
            "name", response.name(),
            "language", response.language()
        );
    }
}
