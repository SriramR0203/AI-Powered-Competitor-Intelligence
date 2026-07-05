package com.competitorintel.platform.controller;

import com.competitorintel.platform.dto.request.CreateSourceRequest;
import com.competitorintel.platform.dto.request.UpdateSourceRequest;
import com.competitorintel.platform.dto.response.IntelligenceSourceResponse;
import com.competitorintel.platform.dto.response.PageResponse;
import com.competitorintel.platform.service.IntelligenceSourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sources")
@RequiredArgsConstructor
@Tag(name = "Intelligence Sources", description = "Manage competitor monitoring sources")
public class IntelligenceSourceController {

    private final IntelligenceSourceService sourceService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    @Operation(summary = "Add an intelligence source")
    public ResponseEntity<IntelligenceSourceResponse> create(@Valid @RequestBody CreateSourceRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sourceService.create(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IntelligenceSourceResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(sourceService.getById(id));
    }

    @GetMapping
    @Operation(summary = "List sources, optionally filtered by competitor")
    public ResponseEntity<PageResponse<IntelligenceSourceResponse>> getAll(
            @RequestParam(required = false) Long competitorId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(sourceService.getAll(competitorId, PageRequest.of(page, size)));
    }

    @GetMapping("/competitor/{competitorId}")
    public ResponseEntity<List<IntelligenceSourceResponse>> getByCompetitor(@PathVariable Long competitorId) {
        return ResponseEntity.ok(sourceService.getByCompetitorId(competitorId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    public ResponseEntity<IntelligenceSourceResponse> update(@PathVariable Long id,
                                                              @Valid @RequestBody UpdateSourceRequest req) {
        return ResponseEntity.ok(sourceService.update(id, req));
    }

    @PatchMapping("/{id}/enable")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    public ResponseEntity<Void> enable(@PathVariable Long id) {
        sourceService.enable(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/disable")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    public ResponseEntity<Void> disable(@PathVariable Long id) {
        sourceService.disable(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/scrape")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    @Operation(summary = "Manually trigger a scrape for this source")
    public ResponseEntity<Void> scrape(@PathVariable Long id) {
        sourceService.triggerManualScrape(id);
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        sourceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
