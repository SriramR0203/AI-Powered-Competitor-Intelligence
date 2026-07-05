CREATE TABLE IF NOT EXISTS intelligence_sources (
    id                          BIGINT        NOT NULL AUTO_INCREMENT,
    competitor_id               BIGINT        NOT NULL,
    name                        VARCHAR(255)  NOT NULL,
    url                         VARCHAR(1000) NOT NULL,
    source_type                 VARCHAR(30)   NOT NULL,
    scrape_interval_hours       INT           NOT NULL DEFAULT 6,
    css_selector                VARCHAR(500),
    xpath_selector              VARCHAR(500),
    requires_javascript         BOOLEAN       NOT NULL DEFAULT FALSE,
    is_active                   BOOLEAN       NOT NULL DEFAULT TRUE,
    last_scraped_at             DATETIME(6),
    next_scrape_at              DATETIME(6),
    consecutive_failures        INT           NOT NULL DEFAULT 0,
    max_failures_before_disable INT           NOT NULL DEFAULT 5,
    content_hash                VARCHAR(64),
    http_headers                TEXT,
    notes                       TEXT,
    created_at                  DATETIME(6)   NOT NULL,
    updated_at                  DATETIME(6)   NOT NULL,
    created_by                  VARCHAR(100),
    updated_by                  VARCHAR(100),
    version                     BIGINT        NOT NULL DEFAULT 0,
    CONSTRAINT pk_intelligence_sources PRIMARY KEY (id),
    CONSTRAINT fk_source_competitor
        FOREIGN KEY (competitor_id) REFERENCES competitors (id) ON DELETE CASCADE
);
CREATE INDEX idx_sources_competitor  ON intelligence_sources (competitor_id);
CREATE INDEX idx_sources_type        ON intelligence_sources (source_type);
CREATE INDEX idx_sources_active      ON intelligence_sources (is_active);
CREATE INDEX idx_sources_next_scrape ON intelligence_sources (next_scrape_at);
