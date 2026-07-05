package com.competitorintel.platform.service;

import com.competitorintel.platform.dto.request.CreateSourceRequest;
import com.competitorintel.platform.dto.request.UpdateSourceRequest;
import com.competitorintel.platform.dto.response.IntelligenceSourceResponse;
import com.competitorintel.platform.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IntelligenceSourceService {
    IntelligenceSourceResponse create(CreateSourceRequest request);
    IntelligenceSourceResponse getById(Long id);
    List<IntelligenceSourceResponse> getByCompetitorId(Long competitorId);
    PageResponse<IntelligenceSourceResponse> getAll(Long competitorId, Pageable pageable);
    IntelligenceSourceResponse update(Long id, UpdateSourceRequest request);
    void enable(Long id);
    void disable(Long id);
    void delete(Long id);
    void triggerManualScrape(Long id);
}
