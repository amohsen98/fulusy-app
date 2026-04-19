package com.fulusy.config;

import com.fulusy.goal.Goal;
import com.fulusy.user.PenaltyState;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;
import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class ScheduledJobs {

    private static final Logger LOG = Logger.getLogger(ScheduledJobs.class);

    /**
     * Runs daily at midnight. Marks goals as failed if deadline has passed
     * and target not reached.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    void checkGoalDeadlines() {
        LocalDate today = LocalDate.now();
        List<Goal> expired = Goal.list("status = 'active' and deadline < ?1", today);
        for (Goal g : expired) {
            if (g.currentAmount.compareTo(g.targetAmount) < 0) {
                g.status = "failed";
                g.persist();
                LOG.infof("Goal '%s' (id=%d) marked as failed — deadline passed", g.name, g.id);
            }
        }
    }

    /**
     * Runs on January 1st at 00:05. Resets penalty exception counters.
     */
    @Scheduled(cron = "0 5 0 1 1 ?")
    @Transactional
    void resetYearlyExceptions() {
        long updated = PenaltyState.update("exceptionsUsedThisYear = 0 where exceptionsUsedThisYear > 0");
        LOG.infof("Reset penalty exceptions for %d users", updated);
    }
}
