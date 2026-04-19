package com.fulusy.auth;

import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
public class AuthResource {

    @Inject AuthService authService;

    @POST
    @Path("/register")
    public Response register(@Valid RegisterRequest req) {
        AuthResponse response = authService.register(req);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @POST
    @Path("/login")
    public Response login(@Valid LoginRequest req) {
        AuthResponse response = authService.login(req);
        return Response.ok(response).build();
    }
}
