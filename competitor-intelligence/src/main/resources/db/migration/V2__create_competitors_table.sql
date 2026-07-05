CREATE TABLE IF NOT EXISTS competitors (
    id                   BIGINT       NOT NULL AUTO_INCREMENT,
    name                 VARCHAR(255) NOT NULL,
    website_url          VARCHAR(500) NOT NULL,
    description          TEXT,
    industry             VARCHAR(100),
    headquarters         VARCHAR(255),
    employee_count_range VARCHAR(50),
    founded_year         INT,
    linkedin_url         VARCHAR(500),
    twitter_handle       VARCHAR(100),
    logo_url             VARCHAR(500),
    status               VARCHAR(30)  NOT NULL DEFAULT 'ACTIVE',
    priority_score       INT          NOT NULL DEFAULT 5,
    notes                TEXT,
    created_at           DATETIME(6)  NOT NULL,
    updated_at           DATETIME(6)  NOT NULL,
    created_by           VARCHAR(100),
    updated_by           VARCHAR(100),
    version              BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT pk_competitors          PRIMARY KEY (id),
    CONSTRAINT uq_competitors_website  UNIQUE (website_url)
);
CREATE INDEX idx_competitors_name     ON competitors (name);
CREATE INDEX idx_competitors_status   ON competitors (status);
CREATE INDEX idx_competitors_industry ON competitors (industry);
