package com.competitorintel.platform.service.impl;

import com.competitorintel.platform.ai.AiRequest;
import com.competitorintel.platform.ai.AiResponse;
import com.competitorintel.platform.ai.AiService;
import com.competitorintel.platform.domain.entity.IntelligenceEvent;
import com.competitorintel.platform.domain.entity.Tag;
import com.competitorintel.platform.domain.enums.*;
import com.competitorintel.platform.domain.repository.*;
import com.competitorintel.platform.dto.response.IntelligenceEventResponse;
import com.competitorintel.platform.dto.response.PageResponse;
import com.competitorintel.platform.events.AiResponseParser;
import com.competitorintel.platform.events.EnrichmentResult;
import com.competitorintel.platform.exception.ResourceNotFoundException;
import com.competitorintel.platform.mapper.IntelligenceEventMapper;
import com.competitorintel.platform.service.AlertService;
import com.competitorintel.platform.service.EventProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventProcessingServiceImpl implements EventProcessingService {

    private static final String SYSTEM_PROMPT = """
            You are an expert competitive intelligence analyst.
            Analyse the provided competitor intelligence and respond ONLY with a JSON object.
            The JSON must have these exact keys:
            - summary (2–3 sentence plain text)
            - category: one of PRODUCT_LAUNCH,PRICING_CHANGE,PARTNERSHIP,ACQUISITION,
              LEADERSHIP_CHANGE,FUNDING,LEGAL,MARKETING_CAMPAIGN,TECHNOLOGY,HIRING,
              CUSTOMER_WIN,CUSTOMER_LOSS,REGULATORY,GENERAL_NEWS,UNCLASSIFIED
            - sentiment: one of VERY_POSITIVE,POSITIVE,NEUTRAL,NEGATIVE,VERY_NEGATIVE
            - sentimentScore: float -1.0 to 1.0
            - relevanceScore: float 0.0 to 1.0
            - importanceScore: float 0.0 to 1.0
            - keyInsights: 1–3 bullet insights as a single string
            - tags: array of 1–5 lowercase tag strings
            Respond ONLY with valid JSON. No markdown outside the JSON.
            """;

    private final IntelligenceEventRepository eventRepository;
    private final TagRepository               tagRepository;
    private final AiService                   aiService;
    private final AiResponseParser            parser;
    private final IntelligenceEventMapper     eventMapper;
    private final ApplicationContext          ctx;

    @Override
    @Async
    public void processRawEventsAsync() {
        List<IntelligenceEvent> events = eventRepository.findPendingEnrichment(
                ProcessingStatus.RAW, PageRequest.of(0, 20));
        if (events.isEmpty()) { log.debug("No RAW events to enrich"); return; }
        log.info("Enriching {} RAW events", events.size());
        for (IntelligenceEvent e : events) {
            try { enrichEvent(e.getId()); }
            catch (Exception ex) { log.error("Enrichment failed for id={}: {}", e.getId(), ex.getMessage()); }
        }
    }

    @Override
    @Transactional
    public void enrichEvent(Long eventId) {
        IntelligenceEvent event = findById(eventId);
        if (event.getProcessingStatus() == ProcessingStatus.ENRICHED) return;

        event.setProcessingStatus(ProcessingStatus.PROCESSING);
        eventRepository.save(event);

        try {
            AiResponse ai = aiService.complete(AiRequest.builder()
                    .systemPrompt(SYSTEM_PROMPT)
                    .userPrompt(buildPrompt(event))
                    .temperature(0.2)
                    .maxTokens(800)
                    .build());

            if (!ai.isSuccess()) { markFailed(event, "AI failed: " + ai.getErrorMessage()); return; }

            EnrichmentResult result = parser.parse(ai.getContent());
            applyEnrichment(event, result, ai);
            eventRepository.save(event);

            log.info("Enriched event id={} category={} sentiment={}", eventId,
                    event.getCategory(), event.getSentiment());

            // Trigger alert evaluation asynchronously after enrichment
            try {
                ctx.getBean(AlertService.class).evaluateEventAgainstRules(eventId);
            } catch (Exception e) {
                log.warn("Alert evaluation failed for event {}: {}", eventId, e.getMessage());
            }

        } catch (Exception e) {
            log.error("Enrichment exception for event {}: {}", eventId, e.getMessage());
            markFailed(event, e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<IntelligenceEventResponse> getEvents(Long competitorId,
                                                              IntelligenceCategory category,
                                                              SentimentType sentiment,
                                                              ProcessingStatus processingStatus,
                                                              LocalDateTime from, LocalDateTime to,
                                                              String search, Pageable pageable) {
        return PageResponse.of(
                eventRepository.findAll(
                        com.competitorintel.platform.domain.repository.IntelligenceEventSpecification
                                .withFilters(competitorId, category, sentiment, processingStatus, from, to, search),
                        pageable)
                        .map(eventMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public IntelligenceEventResponse getEventById(Long id) {
        return eventMapper.toResponse(findById(id));
    }

    @Override @Transactional
    public void flagEvent(Long id, String reason) {
        IntelligenceEvent e = findById(id);
        e.setFlagged(true);
        e.setFlagReason(reason);
        eventRepository.save(e);
    }

    @Override @Transactional
    public void unflagEvent(Long id) {
        IntelligenceEvent e = findById(id);
        e.setFlagged(false);
        e.setFlagReason(null);
        eventRepository.save(e);
    }

    @Override @Transactional
    public void reprocessEvent(Long id) {
        IntelligenceEvent e = findById(id);
        e.setProcessingStatus(ProcessingStatus.RAW);
        eventRepository.save(e);
        enrichEvent(id);
    }

    // ------------------------------------------------------------------ //

    private String buildPrompt(IntelligenceEvent e) {
        StringBuilder sb = new StringBuilder();
        sb.append("Competitor: ").append(e.getCompetitor() != null ? e.getCompetitor().getName() : "Unknown").append('\n');
        sb.append("Title: ").append(e.getTitle()).append('\n');
        if (StringUtils.hasText(e.getUrl())) sb.append("URL: ").append(e.getUrl()).append('\n');
        if (StringUtils.hasText(e.getRawContent())) {
            String c = e.getRawContent();
            sb.append("Content:\n").append(c.length() > 3000 ? c.substring(0, 3000) : c);
        }
        return sb.toString();
    }

    private void applyEnrichment(IntelligenceEvent e, EnrichmentResult r, AiResponse ai) {
        if (StringUtils.hasText(r.getAiSummary()))    e.setAiSummary(r.getAiSummary());
        if (r.getCategory()      != null)             e.setCategory(r.getCategory());
        if (r.getSentiment()     != null)             e.setSentiment(r.getSentiment());
        if (r.getSentimentScore() != null)            e.setSentimentScore(r.getSentimentScore());
        if (r.getRelevanceScore() != null)            e.setRelevanceScore(r.getRelevanceScore());
        if (r.getImportanceScore() != null)           e.setImportanceScore(r.getImportanceScore());
        if (StringUtils.hasText(r.getKeyInsights()))  e.setKeyInsights(r.getKeyInsights());
        e.setAiProvider(ai.getProvider());
        e.setAiModel(ai.getModel());
        e.setProcessingStatus(ProcessingStatus.ENRICHED);
        e.setProcessedAt(LocalDateTime.now());

        if (!r.getTags().isEmpty()) {
            Set<Tag> tags = new HashSet<>();
            for (String name : r.getTags()) {
                if (StringUtils.hasText(name)) tags.add(tagRepository.findOrCreate(name));
            }
            e.setTags(tags);
        }
    }

    private void markFailed(IntelligenceEvent e, String reason) {
        e.setProcessingStatus(ProcessingStatus.FAILED);
        if (reason != null && reason.length() > 500) reason = reason.substring(0, 500);
        e.setFlagReason(reason);
        eventRepository.save(e);
    }

    private IntelligenceEvent findById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("IntelligenceEvent", id));
    }
}
