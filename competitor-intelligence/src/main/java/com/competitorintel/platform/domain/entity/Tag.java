package com.competitorintel.platform.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.NaturalId;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tags", indexes = {
        @Index(name = "idx_tags_name", columnList = "name", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "events")
@EqualsAndHashCode(of = "name", callSuper = false)
public class Tag extends BaseEntity {

    @NaturalId
    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "color", length = 7)
    private String color = "#6c757d";

    @Column(name = "description", length = 500)
    private String description;

    @ManyToMany(mappedBy = "tags")
    private Set<IntelligenceEvent> events = new HashSet<>();
}
