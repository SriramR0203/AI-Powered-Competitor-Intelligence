package com.competitorintel.platform.service;

import com.competitorintel.platform.dto.response.ScrapingJobResponse;

import java.util.List;

public interface ScrapingService {
    void scrapeAllDueSources();
    void scrapeSourceAsync(Long sourceId);
    ScrapingJobResponse scrapeSourceSync(Long sourceId);
    List<ScrapingJobResponse> getRecentJobs(Long sourceId, int limit);
}
