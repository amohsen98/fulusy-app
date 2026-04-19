package com.fulusy.expense;

import com.fulusy.common.exception.NotFoundException;
import com.fulusy.common.security.AuthContext;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/budgets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("user")
public class BudgetResource {

    @Inject AuthContext auth;

    @POST
    @Transactional
    public Response createOrUpdate(@Valid BudgetRequest req) {
        Long userId = auth.getUserId();
        Budget existing = Budget.find("userId = ?1 and categoryId = ?2", userId, req.categoryId())
                .firstResult();
        if (existing != null) {
            existing.monthlyLimit = req.monthlyLimit();
            existing.persist();
            return Response.ok(existing).build();
        }
        Budget b = new Budget();
        b.userId = userId;
        b.categoryId = req.categoryId();
        b.monthlyLimit = req.monthlyLimit();
        b.persist();
        return Response.status(201).entity(b).build();
    }

    @GET
    public List<Budget> list() {
        return Budget.findByUser(auth.getUserId());
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        Budget b = Budget.findById(id);
        if (b == null || !b.userId.equals(auth.getUserId())) {
            throw new NotFoundException("Budget not found");
        }
        b.delete();
        return Response.noContent().build();
    }
}
