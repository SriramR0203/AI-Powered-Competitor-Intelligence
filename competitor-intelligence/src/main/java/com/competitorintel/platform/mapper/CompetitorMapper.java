package com.competitorintel.platform.mapper;

import com.competitorintel.platform.domain.entity.Competitor;
import com.competitorintel.platform.dto.request.CreateCompetitorRequest;
import com.competitorintel.platform.dto.request.UpdateCompetitorRequest;
import com.competitorintel.platform.dto.response.CompetitorResponse;
import com.competitorintel.platform.dto.response.CompetitorSummaryResponse;
import com.competitorintel.platform.domain.enums.CompetitorStatus;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for Competitor entity ↔ DTOs.
 *
 * Note: entity uses plain @NoArgsConstructor + @Setter (no @Builder),
 * so MapStruct uses setter-based mapping. Base-class audit fields
 * (id, createdAt, etc.) are not present in the request DTOs and are
 * left unmapped — which is fine since they are managed by JPA/Hibernate.
 * We set unmappedTargetPolicy = IGNORE to suppress warnings for those fields.
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CompetitorMapper {

    @Mapping(target = "sources",             ignore = true)
    @Mapping(target = "intelligenceEvents",  ignore = true)
    @Mapping(target = "status",
             expression = "java(request.getStatus() != null ? request.getStatus() : com.competitorintel.platform.domain.enums.CompetitorStatus.ACTIVE)")
    @Mapping(target = "priorityScore",
             expression = "java(request.getPriorityScore() != null ? request.getPriorityScore() : 5)")
    Competitor toEntity(CreateCompetitorRequest request);

    // activeSourceCount and recentEventCount are not on the entity;
    // they are enriched in the service layer after mapping.
    CompetitorResponse toResponse(Competitor competitor);

    CompetitorSummaryResponse toSummary(Competitor competitor);

    List<CompetitorSummaryResponse> toSummaryList(List<Competitor> competitors);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "sources",            ignore = true)
    @Mapping(target = "intelligenceEvents", ignore = true)
    void updateEntity(UpdateCompetitorRequest request, @MappingTarget Competitor competitor);
}
