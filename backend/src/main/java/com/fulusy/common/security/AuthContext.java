package com.fulusy.common.security;

import com.fulusy.common.exception.UnauthorizedException;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;

@RequestScoped
public class AuthContext {

    @Context
    ContainerRequestContext requestContext;

    public Long getCurrentUserId() {
        Object userId = requestContext.getProperty("userId");
        if (userId == null) {
            throw new UnauthorizedException("Not authenticated");
        }
        return (Long) userId;
    }
}
