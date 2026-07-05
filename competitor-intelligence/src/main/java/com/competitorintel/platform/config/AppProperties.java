package com.competitorintel.platform.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private Jwt jwt = new Jwt();
    private Ai ai = new Ai();
    private Scraping scraping = new Scraping();
    private Scheduler scheduler = new Scheduler();
    private Alert alert = new Alert();
    private Pagination pagination = new Pagination();

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
