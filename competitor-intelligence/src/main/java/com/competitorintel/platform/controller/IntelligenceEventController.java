package com.competitorintel.platform.controller;

import com.competitorintel.platform.domain.enums.IntelligenceCategory;
import com.competitorintel.platform.domain.enums.ProcessingStatus;
import com.competitorintel.platform.domain.enums.SentimentType;
import com.competitorintel.platform.dto.response.IntelligenceEventResponse;
import com.competitorintel.platform.dto.response.PageResponse;
import com.competitorintel.platform.service.EventProcessingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@Tag(name = "Intelligence Events", description = "AI-enriched competitor events")
public class IntelligenceEventController {

    private final EventProcessingService eventService;

    @GetMapping
    @Operation(summary = "List events with filters")
    public ResponseEntity<PageResponse<IntelligenceEventResponse>> getEvents(
            @RequestParam(required = false) Long competitorId,
            @RequestParam(required = false) IntelligenceCategory category,
            @RequestParam(required = false) SentimentType sentiment,
            @RequestParam(required = false) ProcessingStatus processingStatus,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "publishedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return ResponseEntity.ok(eventService.getEvents(
                competitorId, category, sentiment, processingStatus,
                from, to, search, PageRequest.of(page, size, sort)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IntelligenceEventResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @PostMapping("/{id}/enrich")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    @Operation(summary = "Manually trigger AI enrichment for an event")
    public ResponseEntity<Void> enrich(@PathVariable Long id) {
        eventService.enrichEvent(id);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/{id}/reprocess")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    public ResponseEntity<Void> reprocess(@PathVariable Long id) {
        eventService.reprocessEvent(id);
        return ResponseEntity.accepted().build();
    }

    @PatchMapping("/{id}/flag")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    public ResponseEntity<Void> flag(@PathVariable Long id,
                                      @RequestParam(defaultValue = "Flagged for review") String reason) {
        eventService.flagEvent(id, reason);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/unflag")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    public ResponseEntity<Void> unflag(@PathVariable Long id) {
        eventService.unflagEvent(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/process-raw")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Trigger batch AI enrichment of all pending RAW events")
    public ResponseEntity<Void> processRaw() {
        eventService.processRawEventsAsync();
        return ResponseEntity.accepted().build();
    }
}
