CREATE TABLE IF NOT EXISTS users (
    id                        BIGSERIAL    NOT NULL,
    username                  VARCHAR(50)  NOT NULL,
    email                     VARCHAR(255) NOT NULL,
    password_hash             VARCHAR(255) NOT NULL,
    first_name                VARCHAR(100),
    last_name                 VARCHAR(100),
    is_active                 BOOLEAN      NOT NULL DEFAULT TRUE,
    is_email_verified         BOOLEAN      NOT NULL DEFAULT FALSE,
    last_login_at             TIMESTAMP,
    failed_login_attempts     INT          NOT NULL DEFAULT 0,
    locked_until              TIMESTAMP,
    password_reset_token      VARCHAR(255),
    password_reset_expires_at TIMESTAMP,
    created_at                TIMESTAMP    NOT NULL,
    updated_at                TIMESTAMP    NOT NULL,
    created_by                VARCHAR(100),
    updated_by                VARCHAR(100),
    version                   BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT pk_users          PRIMARY KEY (id),
    CONSTRAINT uq_users_username UNIQUE (username),
    CONSTRAINT uq_users_email    UNIQUE (email)
);
CREATE INDEX IF NOT EXISTS idx_users_active ON users (is_active);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT      NOT NULL,
    role    VARCHAR(30) NOT NULL,
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_user_roles_user_id ON user_roles (user_id);
