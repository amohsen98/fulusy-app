package com.fulusy.income;

import com.fulusy.common.security.AuthContext;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/incomes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("user")
public class IncomeResource {

    @Inject AuthContext auth;
    @Inject IncomeService service;

    @POST
    public Response create(@Valid IncomeRequest req) {
        Income i = service.create(auth.getUserId(), req);
        return Response.status(201).entity(IncomeResponse.from(i)).build();
    }

    @GET
    public List<IncomeResponse> list() {
        return service.listByUser(auth.getUserId())
                .stream().map(IncomeResponse::from).toList();
    }

    @GET
    @Path("/{id}")
    public IncomeResponse getById(@PathParam("id") Long id) {
        return IncomeResponse.from(service.getById(auth.getUserId(), id));
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        service.delete(auth.getUserId(), id);
        return Response.noContent().build();
    }
}
