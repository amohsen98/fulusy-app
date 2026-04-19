package com.fulusy.dashboard;

import com.fulusy.common.security.AuthContext;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/api/dashboard")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("user")
public class DashboardResource {

    @Inject AuthContext auth;
    @Inject DashboardService service;

    @GET
    public DashboardResponse getDashboard() {
        return service.getDashboard(auth.getUserId());
    }
}
