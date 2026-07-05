package com.competitorintel.platform.mapper;

import com.competitorintel.platform.domain.entity.IntelligenceSource;
import com.competitorintel.platform.dto.request.CreateSourceRequest;
import com.competitorintel.platform.dto.request.UpdateSourceRequest;
import com.competitorintel.platform.dto.response.IntelligenceSourceResponse;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for IntelligenceSource entity ↔ DTOs.
 * Entity uses setter-based mapping (no @Builder).
 * Audit fields from BaseEntity are intentionally left unmapped (managed by JPA).
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface IntelligenceSourceMapper {

    @Mapping(target = "competitor",              ignore = true)
    @Mapping(target = "scrapingJobs",            ignore = true)
    @Mapping(target = "lastScrapedAt",           ignore = true)
    @Mapping(target = "nextScrapeAt",            ignore = true)
    @Mapping(target = "consecutiveFailures",     ignore = true)
    @Mapping(target = "contentHash",             ignore = true)
    @Mapping(target = "maxFailuresBeforeDisable",ignore = true)
    @Mapping(target = "active",
             expression = "java(true)")
    @Mapping(target = "requiresJavascript",
             expression = "java(request.getRequiresJavascript() != null ? request.getRequiresJavascript() : false)")
    @Mapping(target = "scrapeIntervalHours",
             expression = "java(request.getScrapeIntervalHours() != null ? request.getScrapeIntervalHours() : 6)")
    IntelligenceSource toEntity(CreateSourceRequest request);

    @Mapping(target = "competitorId",   source = "competitor.id")
    @Mapping(target = "competitorName", source = "competitor.name")
    @Mapping(target = "active",         source = "active")
    IntelligenceSourceResponse toResponse(IntelligenceSource source);

    List<IntelligenceSourceResponse> toResponseList(List<IntelligenceSource> sources);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "competitor",              ignore = true)
    @Mapping(target = "scrapingJobs",            ignore = true)
    @Mapping(target = "lastScrapedAt",           ignore = true)
    @Mapping(target = "nextScrapeAt",            ignore = true)
    @Mapping(target = "consecutiveFailures",     ignore = true)
    @Mapping(target = "contentHash",             ignore = true)
    @Mapping(target = "maxFailuresBeforeDisable",ignore = true)
    void updateEntity(UpdateSourceRequest request, @MappingTarget IntelligenceSource source);
}
