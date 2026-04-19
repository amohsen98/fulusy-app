package com.fulusy.chat;

import com.fulusy.common.exception.BadRequestException;
import com.fulusy.common.security.AuthContext;
import com.fulusy.user.PenaltyState;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;

@Path("/api/chat")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("user")
public class ChatResource {

    @Inject AuthContext auth;
    @Inject GeminiService geminiService;

    @POST
    public Response chat(@Valid ChatRequest req) {
        Long userId = auth.getUserId();

        // Check penalty lock
        PenaltyState ps = PenaltyState.findByUserId(userId);
        if (ps != null && ps.chatbotLockedUntil != null
                && ps.chatbotLockedUntil.isAfter(LocalDateTime.now())
                && ps.allowChatbotLock == 1) {
            throw new BadRequestException("Chatbot is locked until " + ps.chatbotLockedUntil
                    + " due to missed savings goals. Review your goals to unlock.");
        }

        ChatResponse response = geminiService.chat(userId, req.message());
        return Response.ok(response).build();
    }

    @GET
    @Path("/history")
    public List<ChatMessage> history(@QueryParam("limit") @DefaultValue("50") int limit) {
        return ChatMessage.findByUser(auth.getUserId(), Math.min(limit, 200));
    }
}
