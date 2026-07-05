package com.competitorintel.platform.mapper;

import com.competitorintel.platform.domain.entity.AlertNotification;
import com.competitorintel.platform.dto.response.AlertNotificationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlertNotificationMapper {

    @Mapping(target = "alertRuleId",   source = "alertRule.id")
    @Mapping(target = "alertRuleName", source = "alertRule.name")
    @Mapping(target = "eventId",       source = "intelligenceEvent.id")
    @Mapping(target = "eventTitle",    source = "intelligenceEvent.title")
    @Mapping(target = "competitorName",source = "intelligenceEvent.competitor.name")
    AlertNotificationResponse toResponse(AlertNotification notification);

    List<AlertNotificationResponse> toResponseList(List<AlertNotification> notifications);
}
