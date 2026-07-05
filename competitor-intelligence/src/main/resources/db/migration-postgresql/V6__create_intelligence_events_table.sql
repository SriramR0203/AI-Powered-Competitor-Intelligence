CREATE TABLE IF NOT EXISTS intelligence_events (
    id                BIGSERIAL     NOT NULL,
    competitor_id     BIGINT        NOT NULL,
    source_id         BIGINT,
    title             VARCHAR(1000) NOT NULL,
    url               VARCHAR(2000),
    url_hash          VARCHAR(64)   NOT NULL,
    raw_content       TEXT,
    summary           TEXT,
    ai_summary        TEXT,
    key_insights      TEXT,
    category          VARCHAR(50)   NOT NULL DEFAULT 'UNCLASSIFIED',
    sentiment         VARCHAR(20)   NOT NULL DEFAULT 'NEUTRAL',
    sentiment_score   DOUBLE PRECISION,
    relevance_score   DOUBLE PRECISION,
    importance_score  DOUBLE PRECISION,
    processing_status VARCHAR(30)   NOT NULL DEFAULT 'RAW',
    ai_provider       VARCHAR(50),
    ai_model          VARCHAR(100),
    published_at      TIMESTAMP,
    processed_at      TIMESTAMP,
    author            VARCHAR(255),
    image_url         VARCHAR(1000),
    language          VARCHAR(10)   NOT NULL DEFAULT 'en',
    is_flagged        BOOLEAN       NOT NULL DEFAULT FALSE,
    flag_reason       VARCHAR(500),
    created_at        TIMESTAMP     NOT NULL,
    updated_at        TIMESTAMP     NOT NULL,
    created_by        VARCHAR(100),
    updated_by        VARCHAR(100),
    version           BIGINT        NOT NULL DEFAULT 0,
    CONSTRAINT pk_intelligence_events PRIMARY KEY (id),
    CONSTRAINT uq_events_url_hash     UNIQUE (url_hash),
    CONSTRAINT fk_event_competitor
        FOREIGN KEY (competitor_id) REFERENCES competitors (id) ON DELETE CASCADE,
    CONSTRAINT fk_event_source
        FOREIGN KEY (source_id) REFERENCES intelligence_sources (id) ON DELETE SET NULL
);
CREATE INDEX IF NOT EXISTS idx_events_competitor   ON intelligence_events (competitor_id);
CREATE INDEX IF NOT EXISTS idx_events_source       ON intelligence_events (source_id);
CREATE INDEX IF NOT EXISTS idx_events_category     ON intelligence_events (category);
CREATE INDEX IF NOT EXISTS idx_events_sentiment    ON intelligence_events (sentiment);
CREATE INDEX IF NOT EXISTS idx_events_status       ON intelligence_events (processing_status);
CREATE INDEX IF NOT EXISTS idx_events_published_at ON intelligence_events (published_at);

CREATE TABLE IF NOT EXISTS event_tags (
    event_id BIGINT NOT NULL,
    tag_id   BIGINT NOT NULL,
    CONSTRAINT pk_event_tags PRIMARY KEY (event_id, tag_id),
    CONSTRAINT fk_event_tags_event
        FOREIGN KEY (event_id) REFERENCES intelligence_events (id) ON DELETE CASCADE,
    CONSTRAINT fk_event_tags_tag
        FOREIGN KEY (tag_id) REFERENCES tags (id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_event_tags_event ON event_tags (event_id);
CREATE INDEX IF NOT EXISTS idx_event_tags_tag   ON event_tags (tag_id);
