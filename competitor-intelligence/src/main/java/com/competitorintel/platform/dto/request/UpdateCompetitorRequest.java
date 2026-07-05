package com.competitorintel.platform.dto.request;

import com.competitorintel.platform.domain.enums.CompetitorStatus;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class UpdateCompetitorRequest {

    @Size(max = 255)
    private String name;

    @URL(message = "Must be a valid URL")
    @Size(max = 500)
    private String websiteUrl;

    @Size(max = 5000)
    private String description;

    @Size(max = 100)
    private String industry;

    @Size(max = 255)
    private String headquarters;

    @Size(max = 50)
    private String employeeCountRange;

    @Min(1800) @Max(2100)
    private Integer foundedYear;

    @URL(message = "LinkedIn URL must be valid")
    @Size(max = 500)
    private String linkedinUrl;

    @Size(max = 100)
    private String twitterHandle;

    @URL(message = "Logo URL must be valid")
    @Size(max = 500)
    private String logoUrl;

    private CompetitorStatus status;

    @Min(1) @Max(10)
    private Integer priorityScore;

    @Size(max = 5000)
    private String notes;
}
