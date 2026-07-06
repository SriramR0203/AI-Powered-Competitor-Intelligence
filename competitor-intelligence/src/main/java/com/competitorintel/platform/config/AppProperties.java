package com.competitorintel.platform.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.util.Arrays;

@Getter
@Setter
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    /**
     * Known dev-only default secrets that must never reach production.
     * Any value in this set will cause a fast-fail at startup when the
     * active Spring profile is "prod".
     */
    private static final java.util.Set<String> DEV_SECRETS = java.util.Set.of(
            "competitor-intel-default-secret-key-must-be-at-least-256-bits-long",
            "dev-only-jwt-secret-key-do-not-use-in-production-at-least-256-bits",
            "dev-only-jwt-secret-key-do-not-use-in-production-must-be-256-bits-long",
            "replace-with-a-strong-random-secret-minimum-64-characters-long"
    );

    /** Minimum acceptable secret length (256 bits = 32 bytes). */
    private static final int MIN_SECRET_LENGTH = 32;

    // Field injection is required here. @ConfigurationProperties beans are
    // instantiated by Spring Boot's binding infrastructure, which does not
    // participate in the regular constructor-injection lifecycle. Using a
    // constructor parameter leaves `environment` null when @PostConstruct runs.
    // @Autowired on a field is populated by the bean factory after construction
    // and before any @PostConstruct method is called, so the field is always
    // non-null by the time validateJwtSecret() executes.
    @Autowired
    private Environment environment;

    private Jwt jwt = new Jwt();
    private Ai ai = new Ai();
    private Scraping scraping = new Scraping();
    private Scheduler scheduler = new Scheduler();
    private Alert alert = new Alert();
    private Pagination pagination = new Pagination();

    /**
     * Validates JWT configuration at application startup.
     * Fails fast when running under the "prod" profile if:
     *   - JWT_SECRET has not been set (still the dev default), or
     *   - JWT_SECRET is shorter than 32 characters (too weak for HS256).
     *
     * This prevents a silent misconfiguration where the app boots in
     * production with a publicly known weak signing key.
     */
    @PostConstruct
    public void validateJwtSecret() {
        boolean isProd = Arrays.asList(environment.getActiveProfiles()).contains("prod");
        if (!isProd) {
            return; // dev/test profiles may use the weak default
        }

        String secret = jwt.getSecret();

        if (!StringUtils.hasText(secret) || DEV_SECRETS.contains(secret.trim())) {
            throw new IllegalStateException(
                    "[SECURITY] JWT_SECRET is not set or is still the dev-only default. " +
                    "Set a strong random secret via the JWT_SECRET environment variable on Render. " +
                    "Generate one with: openssl rand -base64 64");
        }

        if (secret.length() < MIN_SECRET_LENGTH) {
            throw new IllegalStateException(
                    "[SECURITY] JWT_SECRET is too short (" + secret.length() + " chars). " +
                    "Minimum length is " + MIN_SECRET_LENGTH + " characters (256 bits). " +
                    "Generate a strong secret with: openssl rand -base64 64");
        }
    }

    @Getter @Setter
    public static class Jwt {
        private String secret = "competitor-intel-default-secret-key-must-be-at-least-256-bits-long";
        private long expirationMs = 86400000L;
        private long refreshExpirationMs = 604800000L;
    }

    @Getter @Setter
    public static class Ai {
        private String provider = "mock";
        private OpenAi openai = new OpenAi();
        private Gemini gemini = new Gemini();

        @Getter @Setter
        public static class OpenAi {
            private String apiKey = "";
            private String model = "gpt-4o-mini";
            private String baseUrl = "https://api.openai.com/v1";
            private int timeoutSeconds = 30;
        }

        @Getter @Setter
        public static class Gemini {
            private String apiKey = "";
            private String model = "gemini-1.5-flash";
            private String baseUrl = "https://generativelanguage.googleapis.com/v1beta";
            private int timeoutSeconds = 30;
        }
    }

    @Getter @Setter
    public static class Scraping {
        private String userAgent = "Mozilla/5.0 (compatible; CompetitorIntelBot/1.0)";
        private int timeoutMs = 10000;
        private int maxRetries = 3;
        private long retryDelayMs = 2000L;
        private Selenium selenium = new Selenium();

        @Getter @Setter
        public static class Selenium {
            private boolean headless = true;
            private String driverPath = "";
        }
    }

    @Getter @Setter
    public static class Scheduler {
        private String scrapingCron = "0 0 */6 * * *";
        private String alertCron    = "0 */30 * * * *";
        private String cleanupCron  = "0 0 2 * * *";
    }

    @Getter @Setter
    public static class Alert {
        private Email email = new Email();

        @Getter @Setter
        public static class Email {
            private String from = "noreply@competitorintel.com";
        }
    }

    @Getter @Setter
    public static class Pagination {
        private int defaultPageSize = 20;
        private int maxPageSize = 100;
    }
}
