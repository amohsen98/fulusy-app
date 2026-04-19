package com.fulusy.user;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "user_settings")
public class UserSettings extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    public Long userId;

    @Column(name = "sound_enabled", nullable = false)
    public Short soundEnabled = 0;

    @Column(name = "haptics_enabled", nullable = false)
    public Short hapticsEnabled = 0;

    @Column(name = "daily_reminder_time")
    public String dailyReminderTime;

    @Column(name = "salary_reminder", nullable = false)
    public Short salaryReminder = 1;

    @Column(name = "budget_warnings", nullable = false)
    public Short budgetWarnings = 1;

    @Column(name = "streak_milestones", nullable = false)
    public Short streakMilestones = 1;

    @Column(name = "check_in_frequency", nullable = false)
    public String checkInFrequency = "bi_weekly";

    @Column(name = "savings_goal_allocation", nullable = false)
    public String savingsGoalAllocation = "general_pool";

    @Column(name = "missed_deadline_behavior", nullable = false)
    public String missedDeadlineBehavior = "mark_failed";

    public static UserSettings findByUserId(Long userId) {
        return find("userId", userId).firstResult();
    }
}
