package com.competitorintel.platform.scraping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ScrapedContent {
    private String        title;
    private String        url;
    private String        content;
    private String        summary;
    private String        author;
    private String        imageUrl;
    private LocalDateTime publishedAt;
}
