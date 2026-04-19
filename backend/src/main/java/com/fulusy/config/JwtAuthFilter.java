package com.fulusy.config;

import com.fulusy.common.security.JwtService;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class JwtAuthFilter implements ContainerRequestFilter {

    @Inject
    JwtService jwtService;

    @Override
    public void filter(ContainerRequestContext ctx) {
        String path = ctx.getUriInfo().getPath();
        
        // Allow public paths
        if (path.startsWith("api/auth/") || path.startsWith("q/") || ctx.getMethod().equals("OPTIONS")) {
            return;
        }

        String authHeader = ctx.getHeaderString("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            ctx.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }

        String token = authHeader.substring(7);
        Long userId = jwtService.verifyAndGetUserId(token);
        if (userId == null) {
            ctx.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }
        
        // Set user ID as a request property so AuthContext can read it
        ctx.setProperty("userId", userId);
    }
}
