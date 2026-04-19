package com.fulusy.user;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "penalty_state")
public class PenaltyState extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    public Long userId;

    @Column(name = "strike_count", nullable = false)
    public Short strikeCount = 0;

    @Column(name = "last_check_in_result")
    public String lastCheckInResult;  // pass|fail

    @Column(name = "chatbot_locked_until")
    public LocalDateTime chatbotLockedUntil;

    @Column(name = "quickadd_locked_until")
    public LocalDateTime quickaddLockedUntil;

    @Column(name = "penalty_screen_required", nullable = false)
    public Short penaltyScreenRequired = 0;

    @Column(name = "exceptions_used_this_year", nullable = false)
    public Integer exceptionsUsedThisYear = 0;

    @Column(nullable = false)
    public String intensity = "standard";

    @Column(name = "allow_chatbot_lock", nullable = false)
    public Short allowChatbotLock = 1;

    @Column(name = "allow_quickadd_lock", nullable = false)
    public Short allowQuickaddLock = 1;

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public static PenaltyState findByUserId(Long userId) {
        return find("userId", userId).firstResult();
    }
}
