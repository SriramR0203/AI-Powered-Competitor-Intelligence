package com.competitorintel.platform.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reports", indexes = {
        @Index(name = "idx_reports_competitor", columnList = "competitor_id"),
        @Index(name = "idx_reports_created_by", columnList = "created_by"),
        @Index(name = "idx_reports_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Report extends BaseEntity {

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competitor_id",
            foreignKey = @ForeignKey(name = "fk_report_competitor"))
    private Competitor competitor;

    @Column(name = "report_type", nullable = false, length = 20)
    private String reportType;

    @Column(name = "format", nullable = false, length = 10)
    private String format;

    @Column(name = "file_path", length = 1000)
    private String filePath;

    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;

    @Column(name = "date_from")
    private LocalDateTime dateFrom;

    @Column(name = "date_to")
    private LocalDateTime dateTo;

    @Column(name = "row_count")
    private Integer rowCount;

    @Column(name = "parameters", columnDefinition = "TEXT")
    private String parameters;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    @Column(name = "download_count", nullable = false)
    private int downloadCount = 0;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
}
