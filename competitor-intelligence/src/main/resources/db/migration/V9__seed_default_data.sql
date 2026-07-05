-- BCrypt-hashed passwords:
--   admin   -> Admin123!
--   analyst -> Analyst123!
--   viewer  -> Viewer123!
INSERT INTO users (username, email, password_hash, first_name, last_name,
                   is_active, is_email_verified, created_at, updated_at, version)
VALUES
    ('admin',   'admin@competitorintel.com',
     '$2a$12$JBedIS/zeEkzhKwBxOcleu1mqed8qHC5p.ACGUd1x8/LVUCIQPVHG',
     'Admin', 'User', TRUE, TRUE, NOW(), NOW(), 0),
    ('analyst', 'analyst@competitorintel.com',
     '$2a$12$xkVg/hJ.MQyqvrt9rEmN7eAXK3utwLxk/T4hLh2TPfxiIy8JOHKXO',
     'Analyst', 'User', TRUE, TRUE, NOW(), NOW(), 0),
    ('viewer',  'viewer@competitorintel.com',
     '$2a$12$0LomxcBfnevm0dIbCvQQH.AP5Zds0KwheLpMPV9CfSaWoz6xGc2eO',
     'Viewer', 'User', TRUE, TRUE, NOW(), NOW(), 0);

INSERT INTO user_roles (user_id, role)
    SELECT id, 'ROLE_ADMIN'   FROM users WHERE username = 'admin';
INSERT INTO user_roles (user_id, role)
    SELECT id, 'ROLE_ANALYST' FROM users WHERE username = 'analyst';
INSERT INTO user_roles (user_id, role)
    SELECT id, 'ROLE_VIEWER'  FROM users WHERE username = 'viewer';

-- Sample competitors
INSERT INTO competitors (name, website_url, description, industry, headquarters,
                         status, priority_score, created_at, updated_at, version)
VALUES
    ('Acme Corp',     'https://acmecorp.example.com',
     'Enterprise software leader', 'Software', 'San Francisco, CA',
     'ACTIVE', 9, NOW(), NOW(), 0),
    ('TechRival Inc', 'https://techrival.example.com',
     'Cloud analytics platform',   'Analytics', 'New York, NY',
     'ACTIVE', 8, NOW(), NOW(), 0),
    ('InnovateCo',    'https://innovateco.example.com',
     'AI-first product company',   'Artificial Intelligence', 'Austin, TX',
     'ACTIVE', 7, NOW(), NOW(), 0);

-- Sample sources
INSERT INTO intelligence_sources (competitor_id, name, url, source_type,
                                   scrape_interval_hours, is_active,
                                   created_at, updated_at, version)
SELECT c.id,
       CONCAT(c.name, ' - Blog'),
       CONCAT(c.website_url, '/blog'),
       'BLOG', 6, TRUE, NOW(), NOW(), 0
FROM competitors c;

-- Default alert rule
INSERT INTO alert_rules (name, description, severity, is_active,
                          notify_email, notify_in_app, cooldown_minutes,
                          created_at, updated_at, version)
VALUES ('Critical Events', 'Alert on all high-importance events',
        'HIGH', TRUE, TRUE, TRUE, 30, NOW(), NOW(), 0);

-- Subscribe admin to default rule
INSERT INTO alert_subscriptions (user_id, alert_rule_id, email_enabled, in_app_enabled,
                                  created_at, updated_at, version)
SELECT u.id, r.id, TRUE, TRUE, NOW(), NOW(), 0
FROM users u, alert_rules r
WHERE u.username = 'admin' AND r.name = 'Critical Events';
