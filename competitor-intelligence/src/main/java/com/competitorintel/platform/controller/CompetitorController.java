package com.competitorintel.platform.controller;

import com.competitorintel.platform.domain.enums.CompetitorStatus;
import com.competitorintel.platform.dto.request.CreateCompetitorRequest;
import com.competitorintel.platform.dto.request.UpdateCompetitorRequest;
import com.competitorintel.platform.dto.response.CompetitorResponse;
import com.competitorintel.platform.dto.response.CompetitorSummaryResponse;
import com.competitorintel.platform.dto.response.PageResponse;
import com.competitorintel.platform.service.CompetitorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/competitors")
@RequiredArgsConstructor
@Tag(name = "Competitors", description = "Competitor lifecycle management")
public class CompetitorController {

    private final CompetitorService competitorService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    @Operation(summary = "Create a new competitor")
    public ResponseEntity<CompetitorResponse> create(@Valid @RequestBody CreateCompetitorRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(competitorService.create(req));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get competitor by ID")
    public ResponseEntity<CompetitorResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(competitorService.getById(id));
    }

    @GetMapping
    @Operation(summary = "List competitors with search / status / industry filters")
    public ResponseEntity<PageResponse<CompetitorResponse>> getAll(
            @RequestParam(required = false) String         search,
            @RequestParam(required = false) CompetitorStatus status,
            @RequestParam(required = false) String         industry,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "priorityScore") String sortBy,
            @RequestParam(defaultValue = "desc")          String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return ResponseEntity.ok(
                competitorService.getAll(search, status, industry, PageRequest.of(page, size, sort)));
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active competitors as lightweight list")
    public ResponseEntity<List<CompetitorSummaryResponse>> getActive() {
        return ResponseEntity.ok(competitorService.getAllActive());
    }

    @GetMapping("/industries")
    @Operation(summary = "Distinct industry values for filter dropdowns")
    public ResponseEntity<List<String>> getIndustries() {
        return ResponseEntity.ok(competitorService.getIndustries());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    @Operation(summary = "Update a competitor")
    public ResponseEntity<CompetitorResponse> update(@PathVariable Long id,
                                                      @Valid @RequestBody UpdateCompetitorRequest req) {
        return ResponseEntity.ok(competitorService.update(id, req));
    }

    @PatchMapping("/{id}/archive")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    public ResponseEntity<Void> archive(@PathVariable Long id) {
        competitorService.archive(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        competitorService.activate(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        competitorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
