package com.competitorintel.platform.mapper;

import com.competitorintel.platform.domain.entity.IntelligenceEvent;
import com.competitorintel.platform.domain.entity.Tag;
import com.competitorintel.platform.dto.response.IntelligenceEventResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IntelligenceEventMapper {

    @Mapping(target = "competitorId",   source = "competitor.id")
    @Mapping(target = "competitorName", source = "competitor.name")
    @Mapping(target = "sourceId",       source = "source.id")
    @Mapping(target = "sourceName",     source = "source.name")
    @Mapping(target = "flagged",        source = "flagged")
    @Mapping(target = "tags",           source = "tags", qualifiedByName = "tagsToNames")
    IntelligenceEventResponse toResponse(IntelligenceEvent event);

    @Named("tagsToNames")
    default Set<String> tagsToNames(Set<Tag> tags) {
        if (tags == null) return Set.of();
        return tags.stream().map(Tag::getName).collect(Collectors.toSet());
    }
}
