package com.competitorintel.platform.service;

import com.competitorintel.platform.ai.AiRequest;
import com.competitorintel.platform.ai.AiResponse;
import com.competitorintel.platform.ai.AiService;
import com.competitorintel.platform.domain.entity.Competitor;
import com.competitorintel.platform.domain.entity.IntelligenceEvent;
import com.competitorintel.platform.domain.enums.IntelligenceCategory;
import com.competitorintel.platform.domain.enums.ProcessingStatus;
import com.competitorintel.platform.domain.enums.SentimentType;
import com.competitorintel.platform.domain.repository.IntelligenceEventRepository;
import com.competitorintel.platform.domain.repository.TagRepository;
import com.competitorintel.platform.events.AiResponseParser;
import com.competitorintel.platform.events.EnrichmentResult;
import com.competitorintel.platform.mapper.IntelligenceEventMapper;
import com.competitorintel.platform.service.impl.EventProcessingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventProcessingServiceTest {

    @Mock IntelligenceEventRepository eventRepository;
    @Mock TagRepository               tagRepository;
    @Mock AiService                   aiService;
    @Mock AiResponseParser            parser;
    @Mock IntelligenceEventMapper     eventMapper;
    @Mock ApplicationContext          ctx;

    @InjectMocks EventProcessingServiceImpl eventService;

    @Test
    void enrichEvent_setsEnrichedStatus_whenAiSucceeds() {
        Competitor comp = new Competitor();
        comp.setId(1L);
        comp.setName("Acme");

        IntelligenceEvent event = new IntelligenceEvent();
        event.setId(10L);
        event.setTitle("Acme launches product");
        event.setCompetitor(comp);
        event.setProcessingStatus(ProcessingStatus.RAW);

        EnrichmentResult result = EnrichmentResult.builder()
                .aiSummary("Summary").category(IntelligenceCategory.PRODUCT_LAUNCH)
                .sentiment(SentimentType.POSITIVE).sentimentScore(0.7)
                .relevanceScore(0.9).importanceScore(0.85)
                .tags(List.of("product")).parsed(true).build();

        AiResponse aiResp = AiResponse.builder().success(true).content("{}")
                .provider("mock").model("mock-1.0").build();

        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any())).thenReturn(event);
        when(aiService.complete(any(AiRequest.class))).thenReturn(aiResp);
        when(parser.parse(any())).thenReturn(result);
        when(ctx.getBean(AlertService.class)).thenReturn(mock(AlertService.class));

        eventService.enrichEvent(10L);

        assertThat(event.getProcessingStatus()).isEqualTo(ProcessingStatus.ENRICHED);
        assertThat(event.getCategory()).isEqualTo(IntelligenceCategory.PRODUCT_LAUNCH);
        assertThat(event.getSentiment()).isEqualTo(SentimentType.POSITIVE);
    }

    @Test
    void enrichEvent_marksFailedWhenAiReturnsFalse() {
        Competitor comp = new Competitor();
        comp.setId(1L);

        IntelligenceEvent event = new IntelligenceEvent();
        event.setId(11L);
        event.setTitle("Test");
        event.setCompetitor(comp);
        event.setProcessingStatus(ProcessingStatus.RAW);

        when(eventRepository.findById(11L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any())).thenReturn(event);
        when(aiService.complete(any())).thenReturn(AiResponse.failure("mock", "API error"));

        eventService.enrichEvent(11L);

        assertThat(event.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILED);
    }
}
