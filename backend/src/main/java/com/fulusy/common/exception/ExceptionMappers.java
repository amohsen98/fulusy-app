package com.fulusy.common.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import jakarta.validation.ConstraintViolationException;
import org.jboss.logging.Logger;
import java.util.Map;
import java.util.stream.Collectors;

public class ExceptionMappers {

    private static final Logger LOG = Logger.getLogger(ExceptionMappers.class);

    @Provider
    public static class BadRequestMapper implements ExceptionMapper<BadRequestException> {
        @Override
        public Response toResponse(BadRequestException e) {
            return Response.status(400).entity(Map.of("error", e.getMessage())).build();
        }
    }

    @Provider
    public static class NotFoundMapper implements ExceptionMapper<NotFoundException> {
        @Override
        public Response toResponse(NotFoundException e) {
            return Response.status(404).entity(Map.of("error", e.getMessage())).build();
        }
    }

    @Provider
    public static class UnauthorizedMapper implements ExceptionMapper<UnauthorizedException> {
        @Override
        public Response toResponse(UnauthorizedException e) {
            return Response.status(401).entity(Map.of("error", e.getMessage())).build();
        }
    }

    @Provider
    public static class ValidationMapper implements ExceptionMapper<ConstraintViolationException> {
        @Override
        public Response toResponse(ConstraintViolationException e) {
            Map<String, String> errors = e.getConstraintViolations().stream()
                    .collect(Collectors.toMap(
                            v -> v.getPropertyPath().toString(),
                            v -> v.getMessage(),
                            (a, b) -> a));
            return Response.status(400).entity(Map.of("error", "Validation failed", "fields", errors)).build();
        }
    }

    @Provider
    public static class GenericMapper implements ExceptionMapper<Throwable> {
        @Override
        public Response toResponse(Throwable e) {
            LOG.error("Unhandled exception", e);
            return Response.status(500).entity(Map.of("error", "Internal server error")).build();
        }
    }
}
