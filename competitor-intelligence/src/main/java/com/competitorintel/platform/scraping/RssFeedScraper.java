package com.competitorintel.platform.scraping;

import com.competitorintel.platform.domain.entity.IntelligenceSource;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class RssFeedScraper {

    public List<ScrapedContent> scrape(IntelligenceSource source) {
        List<ScrapedContent> results = new ArrayList<>();
        try {
            SyndFeed feed = new SyndFeedInput().build(new XmlReader(new URL(source.getUrl())));
            for (SyndEntry entry : feed.getEntries()) {
                ScrapedContent c = mapEntry(entry);
                if (c != null) results.add(c);
            }
            log.debug("RSS parsed {} items from {}", results.size(), source.getUrl());
        } catch (Exception e) {
            log.error("RSS parse failed for source {}: {}", source.getId(), e.getMessage());
            throw new RuntimeException("RSS parse failed: " + e.getMessage(), e);
        }
        return results;
    }

    private ScrapedContent mapEntry(SyndEntry entry) {
        String title = entry.getTitle();
        if (!StringUtils.hasText(title)) return null;

        String content = "";
        if (entry.getContents() != null && !entry.getContents().isEmpty()) {
            content = entry.getContents().get(0).getValue();
        } else if (entry.getDescription() != null) {
            content = entry.getDescription().getValue();
        }
        content = content.replaceAll("<[^>]*>", " ").replaceAll("\\s+", " ").trim();

        LocalDateTime pub = entry.getPublishedDate() != null
                ? entry.getPublishedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
                : LocalDateTime.now();

        return ScrapedContent.builder()
                .title(cap(title, 900))
                .url(entry.getLink())
                .content(cap(content, 50000))
                .summary(cap(content, 500))
                .author(entry.getAuthor())
                .publishedAt(pub)
                .build();
    }

    private String cap(String s, int max) {
        if (s == null) return null;
        return s.length() > max ? s.substring(0, max) : s;
    }
}
