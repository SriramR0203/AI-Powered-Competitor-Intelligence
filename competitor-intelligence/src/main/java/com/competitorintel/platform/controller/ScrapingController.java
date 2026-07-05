package com.competitorintel.platform.controller;

import com.competitorintel.platform.dto.response.ScrapingJobResponse;
import com.competitorintel.platform.service.ScrapingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/scraping")
@RequiredArgsConstructor
@Tag(name = "Scraping", description = "Manual scraping triggers and job history")
public class ScrapingController {

    private final ScrapingService scrapingService;

    @PostMapping("/run-all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Trigger scrape of all due sources (ADMIN only)")
    public ResponseEntity<Void> runAll() {
        scrapingService.scrapeAllDueSources();
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/sources/{sourceId}/run")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    @Operation(summary = "Synchronously scrape a specific source")
    public ResponseEntity<ScrapingJobResponse> runOne(@PathVariable Long sourceId) {
        return ResponseEntity.ok(scrapingService.scrapeSourceSync(sourceId));
    }

    @GetMapping("/sources/{sourceId}/jobs")
    @Operation(summary = "Get recent scraping jobs for a source")
    public ResponseEntity<List<ScrapingJobResponse>> jobs(
            @PathVariable Long sourceId,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(scrapingService.getRecentJobs(sourceId, limit));
    }
}
