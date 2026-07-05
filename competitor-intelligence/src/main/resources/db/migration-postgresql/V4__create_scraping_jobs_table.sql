CREATE TABLE IF NOT EXISTS scraping_jobs (
    id                 BIGSERIAL   NOT NULL,
    source_id          BIGINT      NOT NULL,
    competitor_id      BIGINT      NOT NULL,
    status             VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    started_at         TIMESTAMP,
    completed_at       TIMESTAMP,
    duration_ms        BIGINT,
    items_scraped      INT         NOT NULL DEFAULT 0,
    items_new          INT         NOT NULL DEFAULT 0,
    error_message      TEXT,
    http_status_code   INT,
    retry_count        INT         NOT NULL DEFAULT 0,
    content_size_bytes BIGINT,
    triggered_by       VARCHAR(50) NOT NULL DEFAULT 'SCHEDULER',
    created_at         TIMESTAMP   NOT NULL,
    updated_at         TIMESTAMP   NOT NULL,
    created_by         VARCHAR(100),
    updated_by         VARCHAR(100),
    version            BIGINT      NOT NULL DEFAULT 0,
    CONSTRAINT pk_scraping_jobs PRIMARY KEY (id),
    CONSTRAINT fk_scraping_job_source
        FOREIGN KEY (source_id) REFERENCES intelligence_sources (id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_scraping_jobs_source     ON scraping_jobs (source_id);
CREATE INDEX IF NOT EXISTS idx_scraping_jobs_competitor ON scraping_jobs (competitor_id);
CREATE INDEX IF NOT EXISTS idx_scraping_jobs_status     ON scraping_jobs (status);
CREATE INDEX IF NOT EXISTS idx_scraping_jobs_started_at ON scraping_jobs (started_at);
