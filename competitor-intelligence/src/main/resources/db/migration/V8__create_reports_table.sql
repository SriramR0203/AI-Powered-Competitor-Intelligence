CREATE TABLE IF NOT EXISTS reports (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    name            VARCHAR(255) NOT NULL,
    description     VARCHAR(1000),
    competitor_id   BIGINT,
    report_type     VARCHAR(20)  NOT NULL,
    format          VARCHAR(10)  NOT NULL,
    file_path       VARCHAR(1000),
    file_size_bytes BIGINT,
    date_from       DATETIME(6),
    date_to         DATETIME(6),
    row_count       INT,
    parameters      TEXT,
    generated_at    DATETIME(6),
    download_count  INT          NOT NULL DEFAULT 0,
    expires_at      DATETIME(6),
    created_at      DATETIME(6)  NOT NULL,
    updated_at      DATETIME(6)  NOT NULL,
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100),
    version         BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT pk_reports PRIMARY KEY (id),
    CONSTRAINT fk_report_competitor
        FOREIGN KEY (competitor_id) REFERENCES competitors (id) ON DELETE SET NULL
);
CREATE INDEX idx_reports_competitor ON reports (competitor_id);
CREATE INDEX idx_reports_created_by ON reports (created_by);
CREATE INDEX idx_reports_created_at ON reports (created_at);
