package com.competitorintel.platform.service;

import com.competitorintel.platform.domain.enums.IntelligenceCategory;
import com.competitorintel.platform.domain.enums.ProcessingStatus;
import com.competitorintel.platform.domain.enums.SentimentType;
import com.competitorintel.platform.dto.response.IntelligenceEventResponse;
import com.competitorintel.platform.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface EventProcessingService {
    void processRawEventsAsync();
    void enrichEvent(Long eventId);
    PageResponse<IntelligenceEventResponse> getEvents(Long competitorId,
                                                       IntelligenceCategory category,
                                                       SentimentType sentiment,
                                                       ProcessingStatus processingStatus,
                                                       LocalDateTime from, LocalDateTime to,
                                                       String search, Pageable pageable);
    IntelligenceEventResponse getEventById(Long id);
    void flagEvent(Long id, String reason);
    void unflagEvent(Long id);
    void reprocessEvent(Long id);
}
