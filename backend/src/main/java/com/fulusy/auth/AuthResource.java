package com.fulusy.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/api/auth")
@Consumes(MediaType.APPLICATION_JSON)
@PermitAll
public class AuthResource {

    @Inject AuthService authService;
    @Inject ObjectMapper objectMapper;

    @POST
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
    public String register(@Valid RegisterRequest req) throws Exception {
        AuthResponse response = authService.register(req);
        return objectMapper.writeValueAsString(response);
    }

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public String login(@Valid LoginRequest req) throws Exception {
        AuthResponse response = authService.login(req);
        return objectMapper.writeValueAsString(response);
    }
}
