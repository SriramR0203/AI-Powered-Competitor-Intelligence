package com.competitorintel.platform.scheduler;

import com.competitorintel.platform.service.AlertService;
import com.competitorintel.platform.service.ScrapingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScrapingScheduler {

    private final ScrapingService scrapingService;
    private final AlertService    alertService;

    /** Scrape all due sources per configured cron (default every 6 hours). */
    @Scheduled(cron = "${app.scheduler.scraping-cron:0 0 */6 * * *}")
    public void scheduledScrape() {
        log.info("Scheduled scrape starting…");
        try { scrapingService.scrapeAllDueSources(); }
        catch (Exception e) { log.error("Scheduled scrape error: {}", e.getMessage(), e); }
    }

    /** Send any queued email alert notifications every 30 minutes. */
    @Scheduled(cron = "${app.scheduler.alert-cron:0 */30 * * * *}")
    public void sendAlertEmails() {
        try { alertService.sendPendingEmailNotifications(); }
        catch (Exception e) { log.error("Alert email send error: {}", e.getMessage(), e); }
    }
}
