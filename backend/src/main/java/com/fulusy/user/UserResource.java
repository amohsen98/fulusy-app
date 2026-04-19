package com.fulusy.user;

import com.fulusy.common.exception.NotFoundException;
import com.fulusy.common.security.AuthContext;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/me")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("user")
public class UserResource {

    @Inject AuthContext authContext;

    @GET
    public Response getMe() {
        Long userId = authContext.getUserId();
        User user = User.findById(userId);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        return Response.ok(UserProfileResponse.from(user)).build();
    }
}
