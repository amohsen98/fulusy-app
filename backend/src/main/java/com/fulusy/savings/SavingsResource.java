package com.fulusy.savings;

import com.fulusy.common.exception.NotFoundException;
import com.fulusy.common.security.AuthContext;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Path("/api/savings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("user")
public class SavingsResource {

    @Inject AuthContext auth;

    @POST
    @Transactional
    public Response create(@Valid SavingsRequest req) {
        SavingsContribution s = new SavingsContribution();
        s.userId = auth.getUserId();
        s.amount = req.amount();
        s.source = req.source();
        s.note = req.note();
        s.contributionDate = req.contributionDate();
        s.persist();
        return Response.status(201).entity(SavingsResponse.from(s)).build();
    }

    @GET
    public List<SavingsResponse> list() {
        return SavingsContribution.findByUser(auth.getUserId())
                .stream().map(SavingsResponse::from).toList();
    }

    @GET
    @Path("/total")
    public Map<String, BigDecimal> total() {
        List<SavingsContribution> all = SavingsContribution.findByUser(auth.getUserId());
        BigDecimal total = all.stream()
                .map(s -> s.amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return Map.of("totalSaved", total);
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        SavingsContribution s = SavingsContribution.findById(id);
        if (s == null || !s.userId.equals(auth.getUserId())) {
            throw new NotFoundException("Savings contribution not found");
        }
        s.delete();
        return Response.noContent().build();
    }
}
