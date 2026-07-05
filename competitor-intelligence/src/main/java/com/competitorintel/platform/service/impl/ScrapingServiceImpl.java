package com.competitorintel.platform.service.impl;

import com.competitorintel.platform.domain.entity.IntelligenceEvent;
import com.competitorintel.platform.domain.entity.IntelligenceSource;
import com.competitorintel.platform.domain.entity.ScrapingJob;
import com.competitorintel.platform.domain.enums.ProcessingStatus;
import com.competitorintel.platform.domain.enums.ScrapingStatus;
import com.competitorintel.platform.domain.enums.SourceType;
import com.competitorintel.platform.domain.repository.IntelligenceEventRepository;
import com.competitorintel.platform.domain.repository.IntelligenceSourceRepository;
import com.competitorintel.platform.domain.repository.ScrapingJobRepository;
import com.competitorintel.platform.dto.response.ScrapingJobResponse;
import com.competitorintel.platform.exception.ResourceNotFoundException;
import com.competitorintel.platform.scraping.RssFeedScraper;
import com.competitorintel.platform.scraping.ScrapedContent;
import com.competitorintel.platform.scraping.WebScraper;
import com.competitorintel.platform.service.EventProcessingService;
import com.competitorintel.platform.service.ScrapingService;
import com.competitorintel.platform.util.HashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScrapingServiceImpl implements ScrapingService {

    private final IntelligenceSourceRepository sourceRepo;
    private final ScrapingJobRepository        jobRepo;
    private final IntelligenceEventRepository  eventRepo;
    private final WebScraper                   webScraper;
    private final RssFeedScraper               rssScraper;
    private final ApplicationContext           ctx;

    @Override @Transactional
    public void scrapeAllDueSources() {
        List<IntelligenceSource> sources = sourceRepo.findSourcesDueForScraping(LocalDateTime.now());
        log.info("Scheduled scrape: {} sources due", sources.size());
        sources.forEach(s -> scrapeSourceAsync(s.getId()));
    }

    @Override @Async
    public void scrapeSourceAsync(Long sourceId) {
        try { scrapeSourceSync(sourceId); }
        catch (Exception e) { log.error("Async scrape failed for source {}: {}", sourceId, e.getMessage()); }
    }

    @Override @Transactional
    public ScrapingJobResponse scrapeSourceSync(Long sourceId) {
        IntelligenceSource source = sourceRepo.findById(sourceId)
                .orElseThrow(() -> new ResourceNotFoundException("IntelligenceSource", sourceId));

        ScrapingJob job = new ScrapingJob();
        job.setSource(source);
        job.setCompetitorId(source.getCompetitor().getId());
        job.setStatus(ScrapingStatus.RUNNING);
        job.setStartedAt(LocalDateTime.now());
        job.setTriggeredBy("MANUAL");
        job = jobRepo.save(job);

        long start = System.currentTimeMillis();
        int scraped = 0, added = 0;

        try {
            List<ScrapedContent> contents = source.getSourceType() == SourceType.RSS_FEED
                    ? rssScraper.scrape(source) : webScraper.scrape(source);
            scraped = contents.size();

            for (ScrapedContent c : contents) {
                if (persist(source, c)) added++;
            }

            sourceRepo.resetConsecutiveFailures(source.getId(), LocalDateTime.now());
            source.setNextScrapeAt(LocalDateTime.now().plusHours(source.getScrapeIntervalHours()));
            sourceRepo.save(source);

            job.setStatus(ScrapingStatus.SUCCESS);
            job.setItemsScraped(scraped);
            job.setItemsNew(added);
            log.info("Scraped source {} → {} total, {} new", sourceId, scraped, added);

            if (added > 0) {
                try { ctx.getBean(EventProcessingService.class).processRawEventsAsync(); }
                catch (Exception e) { log.warn("Failed to trigger enrichment: {}", e.getMessage()); }
            }
        } catch (Exception e) {
            log.error("Scrape error source {}: {}", sourceId, e.getMessage());
            sourceRepo.incrementConsecutiveFailures(source.getId());
            sourceRepo.disableFailingSources();
            job.setStatus(ScrapingStatus.FAILED);
            job.setErrorMessage(e.getMessage() != null
                    ? e.getMessage().substring(0, Math.min(1000, e.getMessage().length())) : "Unknown");
        }

        job.setCompletedAt(LocalDateTime.now());
        job.setDurationMs(System.currentTimeMillis() - start);
        ScrapingJob saved = jobRepo.save(job);
        return toResponse(saved, source);
    }

    @Override @Transactional(readOnly = true)
    public List<ScrapingJobResponse> getRecentJobs(Long sourceId, int limit) {
        return jobRepo.findBySourceIdOrderByCreatedAtDesc(sourceId, PageRequest.of(0, limit))
                .stream().map(j -> toResponse(j, j.getSource())).collect(Collectors.toList());
    }

    private boolean persist(IntelligenceSource source, ScrapedContent c) {
        String hash = HashUtil.urlHash(c.getUrl() != null
                ? c.getUrl() : source.getUrl() + c.getTitle());
        if (eventRepo.existsByUrlHash(hash)) return false;

        IntelligenceEvent e = new IntelligenceEvent();
        e.setCompetitor(source.getCompetitor());
        e.setSource(source);
        e.setTitle(c.getTitle());
        e.setUrl(c.getUrl());
        e.setUrlHash(hash);
        e.setRawContent(c.getContent());
        e.setSummary(c.getSummary());
        e.setAuthor(c.getAuthor());
        e.setImageUrl(c.getImageUrl());
        e.setPublishedAt(c.getPublishedAt() != null ? c.getPublishedAt() : LocalDateTime.now());
        e.setProcessingStatus(ProcessingStatus.RAW);
        eventRepo.save(e);
        return true;
    }

    private ScrapingJobResponse toResponse(ScrapingJob job, IntelligenceSource source) {
        ScrapingJobResponse r = new ScrapingJobResponse();
        r.setId(job.getId());
        r.setSourceId(source.getId());
        r.setSourceName(source.getName());
        r.setCompetitorId(job.getCompetitorId());
        if (source.getCompetitor() != null) r.setCompetitorName(source.getCompetitor().getName());
        r.setStatus(job.getStatus());
        r.setStartedAt(job.getStartedAt());
        r.setCompletedAt(job.getCompletedAt());
        r.setDurationMs(job.getDurationMs());
        r.setItemsScraped(job.getItemsScraped());
        r.setItemsNew(job.getItemsNew());
        r.setErrorMessage(job.getErrorMessage());
        r.setRetryCount(job.getRetryCount());
        r.setTriggeredBy(job.getTriggeredBy());
        r.setCreatedAt(job.getCreatedAt());
        return r;
    }
}
