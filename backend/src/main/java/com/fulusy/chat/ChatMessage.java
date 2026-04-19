package com.fulusy.chat;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.panache.common.Sort;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "chat_messages")
public class ChatMessage extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "user_id", nullable = false)
    public Long userId;

    @Column(nullable = false)
    public String role;  // user|assistant

    @Column(nullable = false)
    @JdbcTypeCode(SqlTypes.LONG32VARCHAR)
    public String content;

    public String language;  // ar|en

    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public static List<ChatMessage> findByUser(Long userId, int limit) {
        return find("userId", Sort.by("createdAt").descending(), userId)
                .page(0, limit)
                .list();
    }
}
