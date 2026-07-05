CREATE TABLE IF NOT EXISTS alert_rules (
    id                   BIGINT       NOT NULL AUTO_INCREMENT,
    name                 VARCHAR(255) NOT NULL,
    description          VARCHAR(1000),
    competitor_id        BIGINT,
    category_filter      VARCHAR(50),
    sentiment_filter     VARCHAR(20),
    keyword_filter       VARCHAR(1000),
    min_relevance_score  DOUBLE,
    min_importance_score DOUBLE,
    severity             VARCHAR(20)  NOT NULL DEFAULT 'MEDIUM',
    is_active            BOOLEAN      NOT NULL DEFAULT TRUE,
    notify_email         BOOLEAN      NOT NULL DEFAULT TRUE,
    notify_in_app        BOOLEAN      NOT NULL DEFAULT TRUE,
    cooldown_minutes     INT          NOT NULL DEFAULT 60,
    created_at           DATETIME(6)  NOT NULL,
    updated_at           DATETIME(6)  NOT NULL,
    created_by           VARCHAR(100),
    updated_by           VARCHAR(100),
    version              BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT pk_alert_rules PRIMARY KEY (id),
    CONSTRAINT fk_alert_rule_competitor
        FOREIGN KEY (competitor_id) REFERENCES competitors (id) ON DELETE CASCADE
);
CREATE INDEX idx_alert_rules_competitor ON alert_rules (competitor_id);
CREATE INDEX idx_alert_rules_active     ON alert_rules (is_active);
CREATE INDEX idx_alert_rules_severity   ON alert_rules (severity);

CREATE TABLE IF NOT EXISTS alert_subscriptions (
    id             BIGINT      NOT NULL AUTO_INCREMENT,
    user_id        BIGINT      NOT NULL,
    alert_rule_id  BIGINT      NOT NULL,
    email_enabled  BOOLEAN     NOT NULL DEFAULT TRUE,
    in_app_enabled BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at     DATETIME(6) NOT NULL,
    updated_at     DATETIME(6) NOT NULL,
    created_by     VARCHAR(100),
    updated_by     VARCHAR(100),
    version        BIGINT      NOT NULL DEFAULT 0,
    CONSTRAINT pk_alert_subscriptions    PRIMARY KEY (id),
    CONSTRAINT uq_subscription_user_rule UNIQUE (user_id, alert_rule_id),
    CONSTRAINT fk_subscription_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_subscription_rule
        FOREIGN KEY (alert_rule_id) REFERENCES alert_rules (id) ON DELETE CASCADE
);
CREATE INDEX idx_subscriptions_user ON alert_subscriptions (user_id);
CREATE INDEX idx_subscriptions_rule ON alert_subscriptions (alert_rule_id);

CREATE TABLE IF NOT EXISTS alert_notifications (
    id                    BIGINT       NOT NULL AUTO_INCREMENT,
    alert_rule_id         BIGINT       NOT NULL,
    user_id               BIGINT       NOT NULL,
    intelligence_event_id BIGINT       NOT NULL,
    title                 VARCHAR(500) NOT NULL,
    message               TEXT         NOT NULL,
    severity              VARCHAR(20)  NOT NULL,
    status                VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    sent_at               DATETIME(6),
    acknowledged_at       DATETIME(6),
    email_recipient       VARCHAR(255),
    error_message         VARCHAR(1000),
    created_at            DATETIME(6)  NOT NULL,
    updated_at            DATETIME(6)  NOT NULL,
    created_by            VARCHAR(100),
    updated_by            VARCHAR(100),
    version               BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT pk_alert_notifications PRIMARY KEY (id),
    CONSTRAINT fk_notification_rule
        FOREIGN KEY (alert_rule_id) REFERENCES alert_rules (id) ON DELETE CASCADE,
    CONSTRAINT fk_notification_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_notification_event
        FOREIGN KEY (intelligence_event_id) REFERENCES intelligence_events (id) ON DELETE CASCADE
);
CREATE INDEX idx_notifications_user    ON alert_notifications (user_id);
CREATE INDEX idx_notifications_rule    ON alert_notifications (alert_rule_id);
CREATE INDEX idx_notifications_event   ON alert_notifications (intelligence_event_id);
CREATE INDEX idx_notifications_status  ON alert_notifications (status);
CREATE INDEX idx_notifications_created ON alert_notifications (created_at);
