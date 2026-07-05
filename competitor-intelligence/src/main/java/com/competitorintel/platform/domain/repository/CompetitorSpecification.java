package com.competitorintel.platform.domain.repository;

import com.competitorintel.platform.domain.entity.Competitor;
import com.competitorintel.platform.domain.enums.CompetitorStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory for dynamic Competitor query predicates.
 */
public final class CompetitorSpecification {

    private CompetitorSpecification() {}

    public static Specification<Competitor> withFilters(String search,
                                                         CompetitorStatus status,
                                                         String industry) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(search)) {
                String pattern = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")),        pattern),
                        cb.like(cb.lower(root.get("description")), pattern),
                        cb.like(cb.lower(root.get("industry")),    pattern)
                ));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (StringUtils.hasText(industry)) {
                predicates.add(cb.equal(cb.lower(root.get("industry")), industry.toLowerCase()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
