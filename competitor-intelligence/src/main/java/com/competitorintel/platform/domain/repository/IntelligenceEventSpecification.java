package com.competitorintel.platform.domain.repository;

import com.competitorintel.platform.domain.entity.IntelligenceEvent;
import com.competitorintel.platform.domain.enums.IntelligenceCategory;
import com.competitorintel.platform.domain.enums.ProcessingStatus;
import com.competitorintel.platform.domain.enums.SentimentType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory for dynamic IntelligenceEvent query predicates.
 */
public final class IntelligenceEventSpecification {

    private IntelligenceEventSpecification() {}

    public static Specification<IntelligenceEvent> withFilters(Long competitorId,
                                                                IntelligenceCategory category,
                                                                SentimentType sentiment,
                                                                ProcessingStatus processingStatus,
                                                                LocalDateTime from,
                                                                LocalDateTime to,
                                                                String search) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (competitorId != null) {
                predicates.add(cb.equal(root.get("competitor").get("id"), competitorId));
            }
            if (category != null) {
                predicates.add(cb.equal(root.get("category"), category));
            }
            if (sentiment != null) {
                predicates.add(cb.equal(root.get("sentiment"), sentiment));
            }
            if (processingStatus != null) {
                predicates.add(cb.equal(root.get("processingStatus"), processingStatus));
            }
            if (from != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("publishedAt"), from));
            }
            if (to != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("publishedAt"), to));
            }
            if (StringUtils.hasText(search)) {
                String pattern = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")),     pattern),
                        cb.like(cb.lower(root.get("aiSummary")), pattern)
                ));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
