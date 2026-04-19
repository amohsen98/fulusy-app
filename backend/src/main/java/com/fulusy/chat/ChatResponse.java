package com.fulusy.chat;

import java.time.LocalDateTime;

public record ChatResponse(
        String reply,
        String language,
        LocalDateTime timestamp
) {}
