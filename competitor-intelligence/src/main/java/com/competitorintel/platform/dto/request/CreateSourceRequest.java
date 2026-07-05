package com.competitorintel.platform.dto.request;

import com.competitorintel.platform.domain.enums.SourceType;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class CreateSourceRequest {

    @NotNull(message = "Competitor ID is required")
    private Long competitorId;

    @NotBlank(message = "Source name is required")
    @Size(max = 255)
    private String name;

    @NotBlank(message = "URL is required")
    @URL(message = "Must be a valid URL")
    @Size(max = 1000)
    private String url;

    @NotNull(message = "Source type is required")
    private SourceType sourceType;

    @Min(1) @Max(168)
    private Integer scrapeIntervalHours;

    @Size(max = 500)
    private String cssSelector;

    @Size(max = 500)
    private String xpathSelector;

    private Boolean requiresJavascript;

    @Size(max = 5000)
    private String httpHeaders;

    @Size(max = 1000)
    private String notes;
}
