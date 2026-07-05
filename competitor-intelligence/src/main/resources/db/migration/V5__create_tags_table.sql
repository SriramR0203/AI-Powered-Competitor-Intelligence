CREATE TABLE IF NOT EXISTS tags (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(100) NOT NULL,
    color       VARCHAR(7)   NOT NULL DEFAULT '#6c757d',
    description VARCHAR(500),
    created_at  DATETIME(6)  NOT NULL,
    updated_at  DATETIME(6)  NOT NULL,
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100),
    version     BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT pk_tags      PRIMARY KEY (id),
    CONSTRAINT uq_tags_name UNIQUE (name)
);
CREATE INDEX idx_tags_name ON tags (name);

INSERT INTO tags (name, color, description, created_at, updated_at, version) VALUES
    ('product',     '#0d6efd', 'Product events',        NOW(), NOW(), 0),
    ('pricing',     '#dc3545', 'Pricing changes',       NOW(), NOW(), 0),
    ('partnership', '#0dcaf0', 'Partnership news',      NOW(), NOW(), 0),
    ('funding',     '#198754', 'Funding & investment',  NOW(), NOW(), 0),
    ('hiring',      '#ffc107', 'Hiring & talent',       NOW(), NOW(), 0),
    ('technology',  '#6f42c1', 'Technology updates',    NOW(), NOW(), 0),
    ('marketing',   '#fd7e14', 'Marketing & campaigns', NOW(), NOW(), 0),
    ('legal',       '#6c757d', 'Legal & regulatory',    NOW(), NOW(), 0);
