package com.competitorintel.platform.scraping;

import com.competitorintel.platform.config.AppProperties;
import com.competitorintel.platform.domain.entity.IntelligenceSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Selenium-based scraper for JS-heavy pages.
 * Falls back gracefully if ChromeDriver is not installed.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SeleniumScraper {

    private final AppProperties appProperties;

    public List<ScrapedContent> scrape(IntelligenceSource source) {
        List<ScrapedContent> results = new ArrayList<>();
        WebDriver driver = null;
        try {
            driver = createDriver();
            driver.get(source.getUrl());
            new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(d -> d.findElement(By.tagName("body")).isDisplayed());

            String pageSource  = driver.getPageSource();
            String currentUrl  = driver.getCurrentUrl();
            Document doc       = Jsoup.parse(pageSource, currentUrl);
            String selector    = StringUtils.hasText(source.getCssSelector())
                    ? source.getCssSelector() : "body";
            String content     = doc.select(selector).text();

            if (StringUtils.hasText(content)) {
                results.add(ScrapedContent.builder()
                        .title(cap(doc.title(), 900))
                        .url(currentUrl)
                        .content(cap(content, 50000))
                        .publishedAt(LocalDateTime.now())
                        .build());
            }
        } catch (Exception e) {
            log.error("Selenium scrape failed for source {}: {}", source.getId(), e.getMessage());
            throw new RuntimeException("Selenium scrape failed: " + e.getMessage(), e);
        } finally {
            if (driver != null) { try { driver.quit(); } catch (Exception ignored) {} }
        }
        return results;
    }

    private WebDriver createDriver() {
        ChromeOptions opts = new ChromeOptions();
        opts.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage",
                "--disable-gpu", "--window-size=1920,1080", "--disable-extensions",
                "--user-agent=" + appProperties.getScraping().getUserAgent());
        String driverPath = appProperties.getScraping().getSelenium().getDriverPath();
        if (StringUtils.hasText(driverPath)) {
            System.setProperty("webdriver.chrome.driver", driverPath);
        }
        return new ChromeDriver(opts);
    }

    private String cap(String s, int max) {
        if (s == null) return null;
        return s.length() > max ? s.substring(0, max) : s;
    }
}
