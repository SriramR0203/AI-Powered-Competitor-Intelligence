package com.competitorintel.platform.domain.entity;

import com.competitorintel.platform.domain.enums.CompetitorStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "competitors", indexes = {
        @Index(name = "idx_competitors_name",     columnList = "name"),
        @Index(name = "idx_competitors_status",   columnList = "status"),
        @Index(name = "idx_competitors_industry", columnList = "industry"),
        @Index(name = "idx_competitors_website",  columnList = "website_url", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"sources", "intelligenceEvents"})
@EqualsAndHashCode(of = "websiteUrl", callSuper = false)
public class Competitor extends BaseEntity {

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "website_url", nullable = false, unique = true, length = 500)
    private String websiteUrl;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "industry", length = 100)
    private String industry;

    @Column(name = "headquarters", length = 255)
    private String headquarters;

    @Column(name = "employee_count_range", length = 50)
    private String employeeCountRange;

    @Column(name = "founded_year")
    private Integer foundedYear;

    @Column(name = "linkedin_url", length = 500)
    private String linkedinUrl;

    @Column(name = "twitter_handle", length = 100)
    private String twitterHandle;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private CompetitorStatus status = CompetitorStatus.ACTIVE;

    @Column(name = "priority_score", nullable = false)
    private int priorityScore = 5;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "competitor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IntelligenceSource> sources = new ArrayList<>();

    @OneToMany(mappedBy = "competitor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IntelligenceEvent> intelligenceEvents = new ArrayList<>();
}
