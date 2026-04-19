package com.fulusy.auth;

import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.RestResponse;

@Path("/api/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
public class AuthResource {

    @Inject AuthService authService;

    @POST
    @Path("/register")
    public RestResponse<AuthResponse> register(@Valid RegisterRequest req) {
        AuthResponse response = authService.register(req);
        return RestResponse.status(RestResponse.Status.CREATED, response);
    }

    @POST
    @Path("/login")
    public AuthResponse login(@Valid LoginRequest req) {
        return authService.login(req);
    }
}
