package com.fulusy.config;

import io.vertx.ext.web.Router;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

@ApplicationScoped
public class CorsPreflightFilter {

    public void init(@Observes Router router) {
        router.route().order(-100).handler(ctx -> {
            if ("OPTIONS".equalsIgnoreCase(ctx.request().method().name())) {
                String origin = ctx.request().getHeader("Origin");
                ctx.response()
                    .putHeader("Access-Control-Allow-Origin", origin != null ? origin : "*")
                    .putHeader("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS")
                    .putHeader("Access-Control-Allow-Headers", "accept, authorization, content-type, x-requested-with")
                    .putHeader("Access-Control-Allow-Credentials", "true")
                    .putHeader("Access-Control-Max-Age", "86400")
                    .setStatusCode(204)
                    .end();
            } else {
                ctx.next();
            }
        });
    }
}
