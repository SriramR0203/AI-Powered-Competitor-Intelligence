package com.competitorintel.platform.mapper;

import com.competitorintel.platform.domain.entity.Report;
import com.competitorintel.platform.dto.response.ReportResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReportMapper {

    @Mapping(target = "competitorId",   source = "competitor.id")
    @Mapping(target = "competitorName", source = "competitor.name")
    ReportResponse toResponse(Report report);

    List<ReportResponse> toResponseList(List<Report> reports);
}
