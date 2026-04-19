package com.fulusy.expense;

import com.fulusy.common.security.AuthContext;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/expenses")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("user")
public class ExpenseResource {

    @Inject AuthContext auth;
    @Inject ExpenseService service;

    @POST
    public Response create(@Valid ExpenseRequest req) {
        Expense e = service.create(auth.getUserId(), req);
        return Response.status(201).entity(ExpenseResponse.from(e)).build();
    }

    @GET
    public List<ExpenseResponse> list(
            @QueryParam("year") Integer year,
            @QueryParam("month") Integer month) {
        if (year != null && month != null) {
            return service.listByUserInMonth(auth.getUserId(), year, month)
                    .stream().map(ExpenseResponse::from).toList();
        }
        return service.listByUser(auth.getUserId())
                .stream().map(ExpenseResponse::from).toList();
    }

    @GET
    @Path("/{id}")
    public ExpenseResponse getById(@PathParam("id") Long id) {
        return ExpenseResponse.from(service.getById(auth.getUserId(), id));
    }

    @PUT
    @Path("/{id}")
    public ExpenseResponse update(@PathParam("id") Long id, @Valid ExpenseRequest req) {
        return ExpenseResponse.from(service.update(auth.getUserId(), id, req));
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        service.delete(auth.getUserId(), id);
        return Response.noContent().build();
    }
}
