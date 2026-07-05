package com.competitorintel.platform.service.impl;

import com.competitorintel.platform.domain.enums.CompetitorStatus;
import com.competitorintel.platform.domain.enums.ProcessingStatus;
import com.competitorintel.platform.domain.enums.ScrapingStatus;
import com.competitorintel.platform.domain.repository.*;
import com.competitorintel.platform.dto.response.DashboardStatsResponse;
import com.competitorintel.platform.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final CompetitorRepository        competitorRepository;
    private final IntelligenceSourceRepository sourceRepository;
    private final IntelligenceEventRepository  eventRepository;
    private final ScrapingJobRepository        scrapingJobRepository;
    private final AlertNotificationRepository  notificationRepository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "dashboardStats", key = "#daysBack")
    public DashboardStatsResponse getStats(int daysBack) {
        LocalDateTime since = LocalDateTime.now().minusDays(daysBack);

        long totalCompetitors  = competitorRepository.count();
        long activeCompetitors = competitorRepository.countByStatus(CompetitorStatus.ACTIVE);
        long totalSources      = sourceRepository.count();
        long activeSources     = sourceRepository.findAll().stream()
                .filter(com.competitorintel.platform.domain.entity.IntelligenceSource::isActive).count();
        long events30d         = eventRepository.countEventsSince(LocalDateTime.now().minusDays(30));
        long events7d          = eventRepository.countEventsSince(LocalDateTime.now().minusDays(7));

        // Enriched events count
        long enrichedEvents = eventRepository.count() > 0
                ? eventRepository.findAll().stream()
                  .filter(e -> e.getProcessingStatus() == ProcessingStatus.ENRICHED).count()
                : 0L;

        // Category breakdown (last 30 days across all competitors)
        Map<String, Long> eventsByCategory = new LinkedHashMap<>();
        for (Object[] row : eventRepository.findTopCompetitorsByEventCount(
                LocalDateTime.now().minusDays(30), PageRequest.of(0, 100))) {
            // reuse top-competitor query as a proxy — categories come from a different query
        }

        // Build category map from all competitors
        competitorRepository.findAll().stream().limit(10).forEach(c -> {
            List<Object[]> rows = eventRepository.countByCategoryForCompetitor(
                    c.getId(), LocalDateTime.now().minusDays(daysBack));
            for (Object[] row : rows) {
                String cat = row[0] != null ? row[0].toString() : "UNCLASSIFIED";
                long   cnt = ((Number) row[1]).longValue();
                eventsByCategory.merge(cat, cnt, Long::sum);
            }
        });

        // Sentiment breakdown
        Map<String, Long> eventsBySentiment = new LinkedHashMap<>();
        competitorRepository.findAll().stream().limit(10).forEach(c -> {
            List<Object[]> rows = eventRepository.countBySentimentForCompetitor(
                    c.getId(), LocalDateTime.now().minusDays(daysBack));
            for (Object[] row : rows) {
                String snt = row[0] != null ? row[0].toString() : "NEUTRAL";
                long   cnt = ((Number) row[1]).longValue();
                eventsBySentiment.merge(snt, cnt, Long::sum);
            }
        });

        // Top competitors by event count
        List<Map<String, Object>> topCompetitors = new ArrayList<>();
        for (Object[] row : eventRepository.findTopCompetitorsByEventCount(
                since, PageRequest.of(0, 5))) {
            Long cid   = ((Number) row[0]).longValue();
            Long count = ((Number) row[1]).longValue();
            competitorRepository.findById(cid).ifPresent(c -> {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("id", cid);
                entry.put("name", c.getName());
                entry.put("eventCount", count);
                topCompetitors.add(entry);
            });
        }

        // Scrape success rate
        long totalScrapes   = scrapingJobRepository.countByStatusSince(ScrapingStatus.SUCCESS, since)
                + scrapingJobRepository.countByStatusSince(ScrapingStatus.FAILED, since);
        long successScrapes = scrapingJobRepository.countByStatusSince(ScrapingStatus.SUCCESS, since);
        Map<String, Long> scrapeSuccessRate = new LinkedHashMap<>();
        scrapeSuccessRate.put("total",   totalScrapes);
        scrapeSuccessRate.put("success", successScrapes);
        scrapeSuccessRate.put("failed",  totalScrapes - successScrapes);

        return DashboardStatsResponse.builder()
                .totalCompetitors(totalCompetitors)
                .activeCompetitors(activeCompetitors)
                .totalSources(totalSources)
                .activeSources(activeSources)
                .eventsLast30Days(events30d)
                .eventsLast7Days(events7d)
                .enrichedEvents(enrichedEvents)
                .pendingAlerts(0L) // computed separately in alert module
                .eventsByCategory(eventsByCategory)
                .eventsBySentiment(eventsBySentiment)
                .eventsTrend(Collections.emptyList())
                .topCompetitors(topCompetitors)
                .scrapeSuccessRate(scrapeSuccessRate)
                .build();
    }
}
