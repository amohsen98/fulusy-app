package com.fulusy.auth;

import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.ResponseStatus;

@Path("/api/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
public class AuthResource {

    @Inject AuthService authService;

    @POST
    @Path("/register")
    @ResponseStatus(201)
    public AuthResponse register(@Valid RegisterRequest req) {
        return authService.register(req);
    }

    @POST
    @Path("/login")
    public AuthResponse login(@Valid LoginRequest req) {
        return authService.login(req);
    }
}
