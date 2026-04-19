package com.fulusy.goal;

import com.fulusy.common.security.AuthContext;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("/api/goals")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("user")
public class GoalResource {

    @Inject AuthContext auth;
    @Inject GoalService service;

    @POST
    public Response create(@Valid GoalRequest req) {
        Goal g = service.create(auth.getUserId(), req);
        return Response.status(201).entity(GoalResponse.from(g)).build();
    }

    @GET
    public List<GoalResponse> list(@QueryParam("status") String status) {
        List<Goal> goals = "active".equals(status)
                ? service.listActive(auth.getUserId())
                : service.listAll(auth.getUserId());
        return goals.stream().map(GoalResponse::from).toList();
    }

    @GET
    @Path("/{id}")
    public GoalResponse getById(@PathParam("id") Long id) {
        return GoalResponse.from(service.getById(auth.getUserId(), id));
    }

    @POST
    @Path("/{id}/contribute")
    public GoalResponse contribute(@PathParam("id") Long id, @Valid ContributeRequest req) {
        return GoalResponse.from(service.contribute(auth.getUserId(), id, req));
    }

    @PATCH
    @Path("/{id}/status")
    public GoalResponse updateStatus(@PathParam("id") Long id, Map<String, String> body) {
        String status = body.get("status");
        return GoalResponse.from(service.updateStatus(auth.getUserId(), id, status));
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        service.delete(auth.getUserId(), id);
        return Response.noContent().build();
    }
}
