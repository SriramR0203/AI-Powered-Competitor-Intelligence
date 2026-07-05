package com.competitorintel.platform.mapper;

import com.competitorintel.platform.domain.entity.AlertRule;
import com.competitorintel.platform.dto.request.CreateAlertRuleRequest;
import com.competitorintel.platform.dto.response.AlertRuleResponse;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlertRuleMapper {

    @Mapping(target = "competitor",    ignore = true)
    @Mapping(target = "notifications", ignore = true)
    @Mapping(target = "active",        expression = "java(true)")
    AlertRule toEntity(CreateAlertRuleRequest request);

    @Mapping(target = "competitorId",   source = "competitor.id")
    @Mapping(target = "competitorName", source = "competitor.name")
    @Mapping(target = "active",         source = "active")
    AlertRuleResponse toResponse(AlertRule rule);

    List<AlertRuleResponse> toResponseList(List<AlertRule> rules);
}
