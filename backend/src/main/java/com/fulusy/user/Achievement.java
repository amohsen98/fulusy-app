package com.fulusy.user;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "achievements")
public class Achievement extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "user_id", nullable = false)
    public Long userId;

    @Column(name = "achievement_key", nullable = false)
    public String achievementKey;

    @Column(name = "unlocked_at", nullable = false)
    public LocalDateTime unlockedAt = LocalDateTime.now();

    @PrePersist
    void onCreate() {
        if (unlockedAt == null) unlockedAt = LocalDateTime.now();
    }

    public static List<Achievement> findByUser(Long userId) {
        return list("userId", userId);
    }
}
