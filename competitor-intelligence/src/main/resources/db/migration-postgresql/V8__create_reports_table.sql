CREATE TABLE IF NOT EXISTS reports (
    id              BIGSERIAL    NOT NULL,
    name            VARCHAR(255) NOT NULL,
    description     VARCHAR(1000),
    competitor_id   BIGINT,
    report_type     VARCHAR(20)  NOT NULL,
    format          VARCHAR(10)  NOT NULL,
    file_path       VARCHAR(1000),
    file_size_bytes BIGINT,
    date_from       TIMESTAMP,
    date_to         TIMESTAMP,
    row_count       INT,
    parameters      TEXT,
    generated_at    TIMESTAMP,
    download_count  INT          NOT NULL DEFAULT 0,
    expires_at      TIMESTAMP,
    created_at      TIMESTAMP    NOT NULL,
    updated_at      TIMESTAMP    NOT NULL,
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100),
    version         BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT pk_reports PRIMARY KEY (id),
    CONSTRAINT fk_report_competitor
        FOREIGN KEY (competitor_id) REFERENCES competitors (id) ON DELETE SET NULL
);
CREATE INDEX IF NOT EXISTS idx_reports_competitor ON reports (competitor_id);
CREATE INDEX IF NOT EXISTS idx_reports_created_by ON reports (created_by);
CREATE INDEX IF NOT EXISTS idx_reports_created_at ON reports (created_at);
