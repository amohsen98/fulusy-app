-- ============================================================
-- Fulusy Initial Schema - PostgreSQL 16
-- ============================================================

-- USERS -------------------------------------------------------
CREATE TABLE users (
    id                     BIGSERIAL PRIMARY KEY,
    email                  VARCHAR(255) NOT NULL UNIQUE,
    password_hash          VARCHAR(255) NOT NULL,
    name                   VARCHAR(100) NOT NULL,
    starting_balance       NUMERIC(15, 2) DEFAULT 0 NOT NULL,
    income_mode            VARCHAR(20) NOT NULL
        CHECK (income_mode IN ('fixed', 'variable', 'hybrid')),
    fixed_income_amount    NUMERIC(15, 2),
    fixed_income_day       SMALLINT CHECK (fixed_income_day BETWEEN 1 AND 31),
    currency               VARCHAR(10) DEFAULT 'EGP' NOT NULL,
    language               VARCHAR(2) DEFAULT 'ar' NOT NULL
        CHECK (language IN ('ar', 'en')),
    visual_style           VARCHAR(20) DEFAULT 'minimalist' NOT NULL,
    monthly_savings_goal   NUMERIC(15, 2),
    created_at             TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at             TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- USER_SETTINGS -----------------------------------------------
CREATE TABLE user_settings (
    id                         BIGSERIAL PRIMARY KEY,
    user_id                    BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    sound_enabled              SMALLINT DEFAULT 0 NOT NULL,
    haptics_enabled            SMALLINT DEFAULT 0 NOT NULL,
    daily_reminder_time        VARCHAR(5),
    salary_reminder            SMALLINT DEFAULT 1 NOT NULL,
    budget_warnings            SMALLINT DEFAULT 1 NOT NULL,
    streak_milestones          SMALLINT DEFAULT 1 NOT NULL,
    check_in_frequency         VARCHAR(20) DEFAULT 'bi_weekly' NOT NULL
        CHECK (check_in_frequency IN ('weekly', 'bi_weekly', 'monthly')),
    savings_goal_allocation    VARCHAR(20) DEFAULT 'general_pool' NOT NULL,
    missed_deadline_behavior   VARCHAR(20) DEFAULT 'mark_failed' NOT NULL
);

-- BUDGETS -----------------------------------------------------
CREATE TABLE budgets (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category_id     VARCHAR(20) NOT NULL
        CHECK (category_id IN ('essentials', 'transport', 'luxuries', 'shopping', 'other')),
    monthly_limit   NUMERIC(15, 2) NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uq_budget_user_cat UNIQUE (user_id, category_id)
);

-- EXPENSES ----------------------------------------------------
CREATE TABLE expenses (
    id             BIGSERIAL PRIMARY KEY,
    user_id        BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    amount         NUMERIC(15, 2) NOT NULL CHECK (amount > 0),
    category_id    VARCHAR(20) NOT NULL
        CHECK (category_id IN ('essentials', 'transport', 'luxuries', 'shopping', 'other')),
    note           VARCHAR(500),
    expense_date   DATE NOT NULL,
    is_recurring   SMALLINT DEFAULT 0 NOT NULL,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX idx_expenses_user_date ON expenses(user_id, expense_date DESC);
CREATE INDEX idx_expenses_user_cat  ON expenses(user_id, category_id);

-- INCOMES -----------------------------------------------------
CREATE TABLE incomes (
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    amount        NUMERIC(15, 2) NOT NULL CHECK (amount > 0),
    source        VARCHAR(30) NOT NULL
        CHECK (source IN ('salary', 'freelance', 'gift', 'gam3eya_payout', 'bonus', 'other')),
    note          VARCHAR(500),
    income_date   DATE NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX idx_incomes_user_date ON incomes(user_id, income_date DESC);

-- SAVINGS_CONTRIBUTIONS ---------------------------------------
CREATE TABLE savings_contributions (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    amount              NUMERIC(15, 2) NOT NULL CHECK (amount > 0),
    source              VARCHAR(30) NOT NULL
        CHECK (source IN ('manual', 'month_rollover', 'goal_contribution')),
    note                VARCHAR(500),
    contribution_date   DATE NOT NULL,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX idx_savings_user_date ON savings_contributions(user_id, contribution_date DESC);

-- GOALS -------------------------------------------------------
CREATE TABLE goals (
    id                BIGSERIAL PRIMARY KEY,
    user_id           BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name              VARCHAR(100) NOT NULL,
    icon              VARCHAR(20),
    target_amount     NUMERIC(15, 2) NOT NULL CHECK (target_amount > 0),
    starting_amount   NUMERIC(15, 2) DEFAULT 0 NOT NULL,
    current_amount    NUMERIC(15, 2) DEFAULT 0 NOT NULL,
    deadline          DATE NOT NULL,
    priority          VARCHAR(10) DEFAULT 'medium' NOT NULL
        CHECK (priority IN ('low', 'medium', 'high')),
    status            VARCHAR(15) DEFAULT 'active' NOT NULL
        CHECK (status IN ('active', 'achieved', 'cancelled', 'failed')),
    achieved_at       DATE,
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX idx_goals_user_status ON goals(user_id, status);

-- GOAL_CONTRIBUTIONS ------------------------------------------
CREATE TABLE goal_contributions (
    id                  BIGSERIAL PRIMARY KEY,
    goal_id             BIGINT NOT NULL REFERENCES goals(id) ON DELETE CASCADE,
    amount              NUMERIC(15, 2) NOT NULL CHECK (amount > 0),
    contribution_date   DATE NOT NULL,
    note                VARCHAR(500),
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX idx_goalcontribs_goal ON goal_contributions(goal_id, contribution_date DESC);

-- ADHERENCE_SNAPSHOTS -----------------------------------------
CREATE TABLE adherence_snapshots (
    id                       BIGSERIAL PRIMARY KEY,
    goal_id                  BIGINT NOT NULL REFERENCES goals(id) ON DELETE CASCADE,
    check_in_date            DATE NOT NULL,
    adherence_score          NUMERIC(5, 2) NOT NULL CHECK (adherence_score BETWEEN 0 AND 200),
    expected_saved           NUMERIC(15, 2) NOT NULL,
    actual_saved             NUMERIC(15, 2) NOT NULL,
    trend                    VARCHAR(15)
        CHECK (trend IN ('improving', 'declining', 'flat')),
    top_spending_category    VARCHAR(20),
    created_at               TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX idx_adherence_goal_date ON adherence_snapshots(goal_id, check_in_date DESC);

-- MONTHLY_SNAPSHOTS -------------------------------------------
CREATE TABLE monthly_snapshots (
    id                       BIGSERIAL PRIMARY KEY,
    user_id                  BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    month_key                VARCHAR(7) NOT NULL,
    total_spent              NUMERIC(15, 2) DEFAULT 0 NOT NULL,
    total_income             NUMERIC(15, 2) DEFAULT 0 NOT NULL,
    total_saved              NUMERIC(15, 2) DEFAULT 0 NOT NULL,
    auto_rolled_to_savings   NUMERIC(15, 2) DEFAULT 0 NOT NULL,
    essentials_spent         NUMERIC(15, 2) DEFAULT 0 NOT NULL,
    transport_spent          NUMERIC(15, 2) DEFAULT 0 NOT NULL,
    luxuries_spent           NUMERIC(15, 2) DEFAULT 0 NOT NULL,
    shopping_spent           NUMERIC(15, 2) DEFAULT 0 NOT NULL,
    other_spent              NUMERIC(15, 2) DEFAULT 0 NOT NULL,
    start_balance            NUMERIC(15, 2) NOT NULL,
    end_balance              NUMERIC(15, 2) NOT NULL,
    created_at               TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uq_snapshot_user_month UNIQUE (user_id, month_key)
);

-- GAMIFICATION_STATE ------------------------------------------
CREATE TABLE gamification_state (
    id                    BIGSERIAL PRIMARY KEY,
    user_id               BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    current_streak        INT DEFAULT 0 NOT NULL,
    longest_streak        INT DEFAULT 0 NOT NULL,
    total_days_logged     INT DEFAULT 0 NOT NULL,
    under_budget_days     INT DEFAULT 0 NOT NULL,
    months_completed      INT DEFAULT 0 NOT NULL,
    last_log_date         DATE,
    updated_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- PENALTY_STATE -----------------------------------------------
CREATE TABLE penalty_state (
    id                         BIGSERIAL PRIMARY KEY,
    user_id                    BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    strike_count               SMALLINT DEFAULT 0 NOT NULL CHECK (strike_count BETWEEN 0 AND 3),
    last_check_in_result       VARCHAR(10) CHECK (last_check_in_result IN ('pass', 'fail')),
    chatbot_locked_until       TIMESTAMP,
    quickadd_locked_until      TIMESTAMP,
    penalty_screen_required    SMALLINT DEFAULT 0 NOT NULL,
    exceptions_used_this_year  INT DEFAULT 0 NOT NULL,
    intensity                  VARCHAR(10) DEFAULT 'standard' NOT NULL
        CHECK (intensity IN ('off', 'soft', 'standard', 'hard')),
    allow_chatbot_lock         SMALLINT DEFAULT 1 NOT NULL,
    allow_quickadd_lock        SMALLINT DEFAULT 1 NOT NULL,
    updated_at                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- ACHIEVEMENTS ------------------------------------------------
CREATE TABLE achievements (
    id                 BIGSERIAL PRIMARY KEY,
    user_id            BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    achievement_key    VARCHAR(50) NOT NULL,
    unlocked_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uq_achievement_user_key UNIQUE (user_id, achievement_key)
);

-- CHAT_MESSAGES -----------------------------------------------
CREATE TABLE chat_messages (
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role          VARCHAR(10) NOT NULL CHECK (role IN ('user', 'assistant')),
    content       TEXT NOT NULL,
    language      VARCHAR(2) CHECK (language IN ('ar', 'en')),
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX idx_chat_user_date ON chat_messages(user_id, created_at DESC);
